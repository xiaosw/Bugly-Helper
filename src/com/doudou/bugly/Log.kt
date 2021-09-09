package com.doudou.bugly

const val DEBUG = true

object Log {
    inline fun i(msg: String?) {
        if (DEBUG) println("${Thread.currentThread().name} >>> $msg")
    }

    inline fun e(msg: String?) {
        if (DEBUG) System.err.println("${Thread.currentThread().name} >>> $msg")
    }

    inline fun e(tr: Throwable?) {
        if (DEBUG) tr?.printStackTrace()
    }
}