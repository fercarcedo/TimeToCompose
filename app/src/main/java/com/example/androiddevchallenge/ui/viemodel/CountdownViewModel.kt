package com.example.androiddevchallenge.ui.viemodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CountdownViewModel : ViewModel() {
    private val _running = MutableLiveData(false)
    val running: LiveData<Boolean> = _running

    private val _millis = MutableLiveData(0L)
    val millis: LiveData<Long> = _millis

    private val _totalMillis = MutableLiveData(0L)
    val totalMillis: LiveData<Long> = _totalMillis

    private var timer: CountDownTimer? = null

    fun toggleRunning() {
        val newRunning = _running.value?.let { !it } ?: false
        _running.value = newRunning
        if (newRunning) {
            timer = object : CountDownTimer(currentMillis, 1) {
                override fun onTick(millisUntilFinished: Long) {
                    _millis.value = millisUntilFinished
                }

                override fun onFinish() {
                    reset()
                }
            }.start()
        } else {
            timer?.cancel()
            _running.value = false
        }
    }

    fun cancel() {
        timer?.cancel()
        _running.value = false
        _millis.value = 0
        _totalMillis.value = 0
    }

    fun incrementMinutes() {
        val newMillis = currentMillis + 60_000
        _millis.value = newMillis
        _totalMillis.value = newMillis
    }

    fun decrementMinutes() {
        val currentTimeMillis = currentMillis
        if (currentTimeMillis >= 60_000) {
            val newMillis = currentTimeMillis - 60_000
            _millis.value = newMillis
            _totalMillis.value = newMillis
        }
    }

    fun incrementSeconds() {
        val newMillis = currentMillis + 1_000
        _millis.value = newMillis
        _totalMillis.value = newMillis
    }

    fun decrementSeconds() {
        val currentTimeMillis = currentMillis
        if (currentTimeMillis >= 1_000) {
            val newMillis = currentTimeMillis - 1_000
            _millis.value = newMillis
            _totalMillis.value = newMillis
        }
    }

    fun reset() {
        _millis.value = 0
        _totalMillis.value = 0
        _running.value = false
    }

    private val currentMillis: Long
        get() = _millis.value ?: 0
}