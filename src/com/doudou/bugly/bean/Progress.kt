package com.doudou.bugly.bean

import com.doudou.bugly.Log
import java.text.DecimalFormat

data class Progress(val max: Int, var current: Int, val periodInMillis: Long = 1_000) {

    private val progressFormat by lazy {
        DecimalFormat("0.##")
    }
    private var startTimeInMillis: Long = -1L

    fun update(newProgress: Int) {
        current = newProgress
        val currentTimeInMillis = System.currentTimeMillis()
        if (currentTimeInMillis - startTimeInMillis >= periodInMillis
            || current === max) {
            startTimeInMillis = currentTimeInMillis
            Log.i("process: ${progressFormat.format(current * 100F / max)}%")
        }
    }
}