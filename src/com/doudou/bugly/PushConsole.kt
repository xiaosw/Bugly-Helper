package com.doudou.bugly

import com.doudou.bugly.http.OkHttpUtil

fun main(vararg args: String) {
    OkHttpUtil.post("https://stapi.shareinstall.cn/send/sendMessage", PushParams.sign()
        , object : OkHttpUtil.CommonCallback {
        override fun onComplete(code: Int, result: String?) {
            Log.i("code = $code, result = $result")
        }

    })
}