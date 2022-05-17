package com.doudou.bugly

import com.doudou.bugly.callback.Callback
import com.doudou.bugly.manager.BuglyManager
import com.doudou.bugly.manager.CallStackDecoder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger

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
    KEY_CMD("-cmd")
}

const val KEY_DECODER_ADDR = "-decode"

const val APP_VER_SPLIT = "&"
const val TEST = false

@JvmName("main")
fun main(vararg args: String) {
    try {
        Log.i("launch bugly helper.")
        if (TEST) {
//            BuglyManager.generatorExcel("2527295ba1",
//                "805749931",
//                "bugly-session=s%3ArsW5lAd3gKgVD1FwDnhWeo2FvhPh5bDv.XdU%2BGLfaRH9PalZJ5mdQjDIiceDeqPy07YbCGiDeW04",
//                "bugly_session=eyJpdiI6IjExbE1GVTc5b0VHR041U2N1WWZzMVE9PSIsInZhbHVlIjoiYXVzbXlkWmJqY21uMjJoeFVGc3g5S09qeEoxZUU5S3N2SSs1TFRhbktSOXo3RTUyc1ZiVVBQWjRJSEVaOTFQN0l6MW9DUUpma05OS1h2NWhKVFZUaGc9PSIsIm1hYyI6IjY2ZDU2YjBkODJhZTY4ZDUwYWEwZmQ2OGU4Y2QzYWRkMmY2YjZhM2RlN2Y3Nzc5NGUwNDZmZWMwNmUzYjI3NDQifQ%3D%3D",
//                "1.4.1",
//                "2021-10-28",
//                "2021-10-28",
//                0,
//                100,
//                "C:\\\\Users\\\\admin\\\\Downloads\\\\libunity.sym.so",
//                "C:\\\\Users\\\\admin\\\\Downloads\\\\arm64-v8a\\\\libil2cpp.so",
//                "E:\\Workspace\\Demo\\Bugly-Helper\\release\\build",
//                "D:\\Dev\\NDK\\android-ndk-r19c-windows-x86_64\\android-ndk-r19c\\toolchains\\aarch64-linux-android-4.9\\prebuilt\\windows-x86_64\\bin\\aarch64-linux-android-addr2line.exe -C -f -e",
//                AtomicInteger(0),
//                1,
//                System.currentTimeMillis()
//            )

            val decodeMap = mutableMapOf<String, String>()
            decodeMap[getKey(ArgsKey.KEY_UNITY_SO_PATH.value)] = "/Users/Master/Documents/Self/Bugly-Helper/release/libs/arm64-v8a/libunity.sym.so"
            decodeMap[getKey(ArgsKey.KEY_IL_2_CPP_SO_PATH.value)] = "/Users/Master/Downloads/libil2cpp4.so"
            decodeMap[getKey(KEY_DECODER_ADDR)] ="/Users/Master/Documents/Self/Bugly-Helper/release/CallStack.txt"
            decodeAddrs(decodeMap, "/Users/Master/DevelopTool/Java/NDK/android-ndk-r20/toolchains/aarch64-linux-android-4.9/prebuilt/darwin-x86_64/bin/aarch64-linux-android-addr2line -C -f -e")
            return
        }


        val argsMap = mutableMapOf<String, String>()
        val argCount = args.size
        for (i in args.indices step 2) {
            if (i + 1 < argCount) {
                argsMap[getKey(args[i])] = args[i + 1]
            }
        }

        val cmd = argsMap[getKey(ArgsKey.KEY_CMD.value)]
        if (cmd.isNullOrBlank()) {
            Log.i("args ${ArgsKey.KEY_CMD.value} illegal!")
            pause()
            return
        }
        if (argsMap.containsKey(KEY_DECODER_ADDR)) {
            decodeAddrs(argsMap, cmd)
            return
        }

        ArgsKey.values().forEachIndexed { _, argsKey ->
            val key = argsKey.value
            if (!ArgsKey.KEY_APP_VERSION.value.equals(key, true)
                && argsMap[getKey(key)].isNullOrBlank()) {
                println("args $key illegal!")
                pause()
                return
            }
        }

        val appVer = get(argsMap, ArgsKey.KEY_APP_VERSION.value)
        val indexCount = AtomicInteger()
        val startTime = System.currentTimeMillis()
        if (appVer.isNullOrBlank() || !appVer.contains(APP_VER_SPLIT)) {
            generatorExcel(appVer, argsMap, cmd, indexCount, 1, startTime)
            return
        }
        appVer.split(APP_VER_SPLIT).also {
            it.forEach { ver ->
                generatorExcel(ver, argsMap, cmd, indexCount, it.size, startTime)
            }
        }
    } catch (tr: Throwable) {
        Log.e(tr)
        Thread.sleep(10_000)
    }
}

private fun decodeAddrs(argsMap: MutableMap<String, String>, cmd: String) {
    argsMap[KEY_DECODER_ADDR]?.let { content ->
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
                CallStackDecoder.decodeCallStack(it, cmd, unity, il2cpp, "decode-addrs", object : Callback<String> {
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

private fun generatorExcel(appVer: String?, argsMap: MutableMap<String, String>, cmd: String, indexCount: AtomicInteger,
                           totalCount: Int, startTime: Long) {
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
                cmd,
                indexCount,
                totalCount,
                startTime)
        }
    }.start()
}

private fun pause(time: Long = 10_000) = Thread.sleep(time)

private fun get(argMap: MutableMap<String, String>, key: String) : String {
    return argMap[getKey(key)] ?: ""
}

private fun getKey(key: String) = key?.toLowerCase()