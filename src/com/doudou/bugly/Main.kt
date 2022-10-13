package com.doudou.bugly

import com.doudou.bugly.bean.SoInfo
import com.doudou.bugly.callback.Callback
import com.doudou.bugly.config.AppConfig
import com.doudou.bugly.manager.BuglyManager
import com.doudou.bugly.manager.CallStackDecoder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
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
    // armeabi: ARMv5, ARMv8, x86, x86_64
    // armeabi-v7a: ARMv7， ARMv8， x86
    // arm64-v8a: ARMv8
    // mips: MIPS, MIPS64
    // mips64： MIPS64
    // x86: x86, x86_64
    // x86_64: x86_64
    KEY_ABIS("-abis"),
    KEY_OUT_DIR("-out"),
    KEY_CMD("-cmd")
}

const val KEY_DECODER_ADDR = "-decode"

const val APP_VER_SPLIT = "&"
const val TEST = false

@JvmName("main")
fun main(vararg args: String) {
    try {
        val argss = arrayOf("-appId", "b0b1bd57b7",
            "-token", "3783489",
            "-cookieOld", "bugly-session=s%3Ad6IijAW67BZv9IJGZGFPbYg6abULEmUY.M%2BGl%2BLLM7FQtFOuOuUn7oLiGmQcBLBPb9qkJpKWTyBw",
            "-cookieNew", "bugly_session=eyJpdiI6IjNCdnhMYW9YYmE3dG1HNDBLamdNVHc9PSIsInZhbHVlIjoiT09IbDRIWk1tRWFHRUdsSFRcL2NFU1Q0TklsNjVyQ0ZGUGxIUU1uREpYeVFGckdEa1ZBSG55NlZ6MWw3K3BOWjhsY3YwNEVGRWNlSU0wXC8ydytYMkJPUT09IiwibWFjIjoiZWZjNTg2YWFmZDkyZjBhNTVkY2JlNzEzZjdiYjBiZTZiYzE5OTliMDczZmVhOGE1OGJhMmUyNzcyMGMyOTg2NCJ9",
            "-appVer", "2.0.0",
            "-startDateStr", "2022-10-10",
            "-endDateStr", "2022-10-13",
            "-pageIndex", "0",
            "-pageSize", "100",
            "-debug", "false",
            "-abis", "{\"arm64-v8a\":{\"unity\":\"E:\\Workspace\\Demo\\Bugly-Helper\\release\\libs\\arm64-v8a\\libunity.sym.so\", \"il2cpp\":\"E:\\Workspace\\Demo\\Bugly-Helper\\release\\libs\\arm64-v8a\\libil2cpp.so\"}, \"armeabi-v7a\":{\"unity\":\"E:\\Workspace\\Demo\\Bugly-Helper\\release\\libs\\arm64-v8a\\libunity.sym.so\", \"il2cpp\":\"E:\\Workspace\\Demo\\Bugly-Helper\\release\\libs\\arm64-v8a\\libil2cpp.so\"}}",
            "-out", "E:\\Workspace\\Demo\\Bugly-Helper\\release\\build",
            "-cmd", "D:\\Dev\\NDK\\ndk-r19c\\android-ndk-r19c\\toolchains\\aarch64-linux-android-4.9\\prebuilt\\windows-x86_64\\bin\\aarch64-linux-android-addr2line.exe -C -f -e")
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
            decodeMap[getKey(ArgsKey.KEY_ABIS.value)] = """{"armeabi":{"unity":"unity.so","il2cpp":"il2cpp.so"}}"""
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
        AppConfig.isDebug = "true".equals(argsMap[getKey("-debug")], true)

        if (AppConfig.isDebug) {
            argsMap.forEach { (key, value) ->
                Log.i("arg: $key = $value")
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

private fun handleJsonStr(json: String) = if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
    json.replace("\\\\", "##**!?!**##")
        .replace("\\", "\\\\")
        .replace("\\##**!?!**##", "\\\\")
} else json

private fun parseAbis(abis: String?) = abis?.let {
    val abisType = object : TypeToken<MutableMap<String, SoInfo>>(){}.type
    try {
        GsonBuilder().disableHtmlEscaping().create().fromJson<MutableMap<String, SoInfo>>(handleJsonStr(abis), abisType)
    } catch (e: Exception) {
        Log.e(e.message)
        mutableMapOf()
    }
} ?: mutableMapOf()

private fun decodeAddrs(argsMap: MutableMap<String, String>, cmd: String) {
    argsMap[KEY_DECODER_ADDR]?.let { content ->
        val abi = argsMap[getKey(ArgsKey.KEY_ABIS.value)]
        if (abi.isNullOrEmpty()) {
            Log.e("abis is null")
            pause()
            return
        }
        val soInfo = Gson().fromJson(handleJsonStr(abi), SoInfo::class.java)
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
                CallStackDecoder.decodeCallStack(it, cmd, soInfo.unity, soInfo.il2cpp,"decode-addrs", object : Callback<String> {
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
                parseAbis(get(argsMap, ArgsKey.KEY_ABIS.value)),
                get(argsMap, ArgsKey.KEY_OUT_DIR.value),
                cmd,
                indexCount,
                totalCount,
                startTime)
        }
    }.start()
}

private fun pause(time: Long = 60_000) = Thread.sleep(time)

private fun get(argMap: MutableMap<String, String>, key: String) : String {
    return argMap[getKey(key)] ?: ""
}

private fun getKey(key: String) = key?.toLowerCase()