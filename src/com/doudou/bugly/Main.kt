package com.doudou.bugly

import com.doudou.bugly.manager.BuglyManager


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

@JvmName("main")
fun main(vararg args: String) {
    try {
        println("launch bugly helper.")
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

        ArgsKey.values().forEachIndexed { _, argsKey ->
            val key = argsKey.value
            if (ArgsKey.KEY_NDK_HOME.value.equals(key, true)) {
                if (ndk.isNullOrBlank()) {
                    println("args $key illegal!")
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
        BuglyManager.generatorExcel(get(argsMap, ArgsKey.KEY_APP_ID.value),
            get(argsMap, ArgsKey.KEY_TOKEN.value),
            get(argsMap, ArgsKey.KEY_COOKIE_OLD_API.value),
            get(argsMap, ArgsKey.KEY_COOKIE_NEW_API.value),
            get(argsMap, ArgsKey.KEY_APP_VERSION.value),
            get(argsMap, ArgsKey.KEY_START_DATE_STR.value),
            get(argsMap, ArgsKey.KEY_END_DATE_STR.value),
            get(argsMap, ArgsKey.KEY_PAGE_INDEX.value).toInt(),
            get(argsMap, ArgsKey.KEY_PAGE_SIZE.value).toInt(),
            get(argsMap, ArgsKey.KEY_UNITY_SO_PATH.value),
            get(argsMap, ArgsKey.KEY_IL_2_CPP_SO_PATH.value),
            get(argsMap, ArgsKey.KEY_OUT_DIR.value),
            "$ndk")
    } catch (e: Throwable) {
        e.printStackTrace()
        Thread.sleep(10_000)
    }
}

private fun pause(time: Long = 10_000) = Thread.sleep(time)

private fun get(argMap: MutableMap<String, String>, key: String) : String {
    return argMap[getKey(key)] ?: ""
}

private fun getKey(key: String) = key?.toLowerCase()