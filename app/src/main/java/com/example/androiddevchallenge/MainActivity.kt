/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PauseCircleOutline
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.theme.TimeToComposeTheme
import com.example.androiddevchallenge.ui.viemodel.CountdownViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        window.makeTransparentStatusBar(nightMode == Configuration.UI_MODE_NIGHT_YES)
        setContent {
            TimeToComposeTheme {
                TimeToCompose()
            }
        }
    }
}

@Composable
fun TimeToCompose(viewModel: CountdownViewModel = viewModel()) {
    val running by viewModel.running.observeAsState(initial = false)
    val timeInMillis by viewModel.millis.observeAsState(initial = 0)
    val totalTimeInMillis by viewModel.totalMillis.observeAsState(initial = 0)
    val secondaryColor = MaterialTheme.colors.secondary
    Surface(color = MaterialTheme.colors.background) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .drawBehind {
                    if (running) {
                        val percent = (timeInMillis.toDouble() / totalTimeInMillis).toFloat()
                        val offset = Offset(0f, size.height)
                        drawRect(
                            secondaryColor,
                            topLeft = offset,
                            size = Size(size.width - offset.x, (size.height * percent) - offset.y)
                        )
                    }
                }
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CountdownTime(running, timeInMillis, viewModel)
            }
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Controls(running, onClickStartPause = {
                    viewModel.toggleRunning()
                }, onClickCancel = {
                    viewModel.cancel()
                })
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CountdownTime(running: Boolean, timeInMillis: Long, viewModel: CountdownViewModel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column {
            AnimatedVisibility(visible = !running) { TimeArrow(true) { viewModel.incrementMinutes() } }
            Text((timeInMillis / 60_000).toString().padStart(2, '0'), style = MaterialTheme.typography.h2, fontWeight = FontWeight.Bold)
            AnimatedVisibility(visible = !running) { TimeArrow(false) { viewModel.decrementMinutes() } }
        }
        Text(":", style = MaterialTheme.typography.h2, fontWeight = FontWeight.Bold)
        Column {
            AnimatedVisibility(visible = !running) { TimeArrow(true) { viewModel.incrementSeconds() } }
            Text(((timeInMillis % 60_000) / 1000).toString().padStart(2, '0'), style = MaterialTheme.typography.h2, fontWeight = FontWeight.Bold)
            AnimatedVisibility(visible = !running) { TimeArrow(false) { viewModel.decrementSeconds() } }
        }
        Text(((timeInMillis % 60_000) % 1000).toString().padStart(3, '0'), style = MaterialTheme.typography.h4, modifier = Modifier.offset(y = 10.dp))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Controls(running: Boolean, onClickStartPause: () -> Unit, onClickCancel: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        IconButton(onClick = onClickStartPause, modifier = Modifier
            .width(150.dp)
            .height(150.dp)) {
            Icon(
                imageVector = if (running) Icons.Outlined.PauseCircleOutline else Icons.Outlined.PlayCircleOutline,
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        }
        AnimatedVisibility(visible = running) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onClickCancel) {
                    Text(
                        stringResource(R.string.cancel),
                        color = MaterialTheme.colors.onSurface,
                        style = MaterialTheme.typography.button.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TimeArrow(up: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier
        .width(75.dp)
        .height(75.dp)) {
        Icon(
            imageVector = if (up) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@SuppressWarnings("deprecated")
fun Window.makeTransparentStatusBar(nightModeEnabled: Boolean) {
    if (!nightModeEnabled) {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    statusBarColor = Color.TRANSPARENT
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    TimeToComposeTheme {
        TimeToCompose()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    TimeToComposeTheme(darkTheme = true) {
        TimeToCompose()
    }
}
