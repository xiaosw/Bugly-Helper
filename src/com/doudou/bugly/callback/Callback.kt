package com.doudou.bugly.callback

interface Callback<T> {

    fun onFail(code: Int, msg: String?)

    fun onSuccess(t: T)

}