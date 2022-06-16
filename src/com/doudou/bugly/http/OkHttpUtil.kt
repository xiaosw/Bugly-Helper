package com.doudou.bugly.http

import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

object OkHttpUtil {

    private val mOkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    fun get(xToken: String, cookie: String, url: String, callback: CommonCallback) {
//        println("get: xToken = $xToken, cookie = $cookie url = $url")
        val request = Request
            .Builder()
            .url(url)
            .get()
            .addHeader("X-token", xToken)
            .addHeader("cookie", cookie)
            .build()
        mOkHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback.onComplete(-1, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onComplete(200, response.body()?.string())
            }

        })
    }

    fun get(cookie: String, rowIndex: Int, url: String, callback: CommonCallback) {
//        println("get url: $url")
        val request = Request
            .Builder()
            .url(url)
            .get()
            .addHeader("cookie",cookie)
            .build()
        mOkHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback.onComplete(rowIndex, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onComplete(rowIndex, response.body()?.string())
            }

        })
    }

    interface CommonCallback {

        fun onComplete(row: Int, decode: String?)
    }

}