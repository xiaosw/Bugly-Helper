package com.doudou.bugly

import com.doudou.bugly.callback.Callback
import com.doudou.bugly.manager.BuglyManager
import com.doudou.bugly.manager.CallStackDecoder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

enum class ArgsKey(val value: String) {
    KEY_APP_ID("-appId"),
    KEY_TOKEN("-token"),
    KEY_COOKIE_OLD_API("-cookieOld"),
    KEY_COOKIE_NEW_API("-cookieNew"),
    KEY_APP_VERSION("-appVer"),
    KEY_START_DATE_STR("-startDateStr"),
    KEY_END_DATE_STR("-endDateStr"),
    KEY_PAGE_INDEX("-pageIndex"),
    KEY_PAGE_SIZE("-pageSize"),
    KEY_UNITY_SO_PATH("-unity"),
    KEY_IL_2_CPP_SO_PATH("-il2cpp"),
    KEY_OUT_DIR("-out"),
    KEY_NDK_HOME("-ndk")
}

const val KEY_DECODER_ADDR = "-decode"

const val APP_VER_SPLIT = "&"
const val TEST = false

@JvmName("main")
fun main(vararg args: String) {
    try {
        Log.i("launch bugly helper.")
        if (TEST) {
            BuglyManager.generatorExcel("2527295ba1",
                "1599244150",
                "bugly-session=s%3A9rqhBQRF3jn5nuo2cmvgelowmf4k1HU4.j62OQv3AYC7GCdlZBaDpMyIwuhxgFNVvibkc9XqVmgo",
                "bugly_session=eyJpdiI6Imt6VGJUSWFkbkJVYXpqMmlNTnpya0E9PSIsInZhbHVlIjoibmhpSW5ubjByM1FmWDZSdVRFZ2I3ZXA0Mk9pVkd4R2lmUmhRcUJSeDlGRkc1bEhhdUpGQlBhd0NMaTBZK2JwSSt5OEh2VjB0MGllRHk2NGFnXC9IY0hnPT0iLCJtYWMiOiI3ZDUzNzk3MzMwNDk3M2IyNWMxMjM4MmRjMWUxZjc3MmVhZWMxYzdjNTY1OWIyN2RhMmQyODRmYzI3ZjA0YWQ1In0%3D",
                "1.2.5",
                "2021-09-09",
                "2021-09-09",
                0,
                100,
                "C:\\\\Users\\\\admin\\\\Downloads\\\\libunity.sym.so",
                "C:\\\\Users\\\\admin\\\\Downloads\\\\arm64-v8a\\\\libil2cpp.so",
                "E:\\Workspace\\Demo\\Bugly-Helper\\release\\\\build",
                "D:\\Dev\\NDK\\android-ndk-r19c-windows-x86_64\\android-ndk-r19c")
            return
        }


        val argsMap = mutableMapOf<String, String>()
        val argCount = args.size
        for (i in args.indices step 2) {
            if (i + 1 < argCount) {
                argsMap[getKey(args[i])] = args[i + 1]
            }
        }

        val tempNdk = argsMap[getKey(ArgsKey.KEY_NDK_HOME.value)]
        var ndk = if (tempNdk.isNullOrBlank()) {
            System.getenv()["NDK_HOME"] ?: System.getenv()["NDK_ANDROID"]
        } else if (!System.getenv()[tempNdk].isNullOrBlank()) {
            System.getenv()[tempNdk]
        } else {
            tempNdk
        }

        if (argsMap.containsKey(KEY_DECODER_ADDR)) {
            decodeAddrs(argsMap, ndk)
            return
        }

        ArgsKey.values().forEachIndexed { _, argsKey ->
            val key = argsKey.value
            if (ArgsKey.KEY_NDK_HOME.value.equals(key, true)) {
                if (ndk.isNullOrBlank()) {
                    Log.i("args $key illegal!")
                    pause()
                    return
                }
            } else if (!ArgsKey.KEY_APP_VERSION.value.equals(key, true)
                && argsMap[getKey(key)].isNullOrBlank()) {
                println("args $key illegal!")
                pause()
                return
            }
        }

        val appVer = get(argsMap, ArgsKey.KEY_APP_VERSION.value)
        if (appVer.isNullOrBlank() || !appVer.contains(APP_VER_SPLIT)) {
            generatorExcel(appVer, argsMap, ndk)
            return
        }
        appVer.split(APP_VER_SPLIT).forEach {
            generatorExcel(it, argsMap, ndk)
        }
    } catch (tr: Throwable) {
        Log.e(tr)
        Thread.sleep(10_000)
    }
}

private fun decodeAddrs(argsMap: MutableMap<String, String>, ndk: String?) {
    argsMap[KEY_DECODER_ADDR]?.let { content ->
        if (ndk.isNullOrEmpty()) {
            Log.e("ndk is null")
            pause()
            return
        }
        val unity = argsMap[getKey(ArgsKey.KEY_UNITY_SO_PATH.value)] ?: ""
        if (unity.isNullOrEmpty()) {
            Log.e("unity so is null")
            pause()
            return
        }
        val il2cpp = argsMap[getKey(ArgsKey.KEY_IL_2_CPP_SO_PATH.value)] ?: ""
        if (il2cpp.isNullOrEmpty()) {
            Log.e("il2cpp so is null")
            pause()
            return
        }
        try {
            val decodeFile = File(content)
            val decodeContent = if (decodeFile.exists() && decodeFile.isFile) {
                FileInputStream(decodeFile).use {  fis ->
                    ByteArrayOutputStream().use { baos ->
                        val buff = ByteArray(1024)
                        var len: Int
                        while (fis.read(buff).also { len = it } != -1) {
                            baos.write(buff, 0, len)
                        }
                        String(baos.toByteArray(), Charsets.UTF_8).trim { it <= ' ' }
                    }
                }
            } else {
                content
            }
            decodeContent?.let {
                CallStackDecoder.decodeCallStack(it, "$ndk", unity, il2cpp, "decode-addrs", object : Callback<String> {
                    override fun onFail(code: Int, msg: String?) {
                        Log.e("decode fail: $code, $msg")
                    }

                    override fun onSuccess(t: String) {
                        Log.e("decode success: \n$t")
                    }

                })
                pause(20_000)
                return
            }
        } catch (e: Exception) {
            Log.e(e)
        }
    }
    Log.e("decode content is null!")
    pause()
}

private fun generatorExcel(appVer: String?, argsMap: MutableMap<String, String>, ndk: String?) {
    object : Thread(appVer ?: "all-app-version"){
        override fun run() {
            super.run()
            BuglyManager.generatorExcel(get(argsMap, ArgsKey.KEY_APP_ID.value),
                get(argsMap, ArgsKey.KEY_TOKEN.value),
                get(argsMap, ArgsKey.KEY_COOKIE_OLD_API.value),
                get(argsMap, ArgsKey.KEY_COOKIE_NEW_API.value),
                appVer,
                get(argsMap, ArgsKey.KEY_START_DATE_STR.value),
                get(argsMap, ArgsKey.KEY_END_DATE_STR.value),
                get(argsMap, ArgsKey.KEY_PAGE_INDEX.value).toInt(),
                get(argsMap, ArgsKey.KEY_PAGE_SIZE.value).toInt(),
                get(argsMap, ArgsKey.KEY_UNITY_SO_PATH.value),
                get(argsMap, ArgsKey.KEY_IL_2_CPP_SO_PATH.value),
                get(argsMap, ArgsKey.KEY_OUT_DIR.value),
                "$ndk")
        }
    }.start()
}

private fun pause(time: Long = 10_000) = Thread.sleep(time)

private fun get(argMap: MutableMap<String, String>, key: String) : String {
    return argMap[getKey(key)] ?: ""
}

private fun getKey(key: String) = key?.toLowerCase()