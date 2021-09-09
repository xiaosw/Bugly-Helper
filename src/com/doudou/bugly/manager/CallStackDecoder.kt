package com.doudou.bugly.manager

import com.doudou.bugly.callback.Callback
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder

object CallStackDecoder {

    private const val ADDR_PREFIX_UNITY_SO = "libunity.0x"
    private const val ADDR_PREFIX_IL2CPP_SO = "libil2cpp.0x"
    private const val ADDR_SUFFIX = "(Native Method)"
    private val CMD = "${System.getenv()["NDK_HOME"]}\\toolchains\\aarch64-linux-android-4.9\\prebuilt\\windows-x86_64\\bin\\aarch64-linux-android-addr2line.exe -C -f -e "
    private const val IGNORE_DECODE = "??:?"

    fun decodeCallStack(callStack: String, ndk: String, unitySoPath: String, il2cppSoPath: String, callback: Callback<String>) {
        val addrs = parseAddrs(callStack)
        if (addrs.isEmpty()) {
            callback?.onSuccess(callStack)
            return
        }
        decodeCallStack(addrs, ndk, unitySoPath, il2cppSoPath, callback)
    }

    fun decodeCallStack(addrsList: MutableList<Addrs>, ndk: String, unitySoPath: String, il2cppSoPath: String, callback: Callback<String>) {

        val cmdPrefix = "$ndk\\toolchains\\aarch64-linux-android-4.9\\prebuilt\\windows-x86_64\\bin\\aarch64-linux-android-addr2line.exe -C -f -e "
        object : Thread(){
            override fun run() {
                super.run()
                val sb = StringBuilder()
                addrsList.forEachIndexed { index, addrs ->
                    var recycle: Process? = null
                    try {
                        when {
                            addrs.type === Addrs.TYPE_UNITY -> {
                                unitySoPath
                            }
                            addrs.type === Addrs.TYPE_IL2CPP -> {
                                il2cppSoPath
                            }
                            else -> {
                                null
                            }
                        }?.let { soPath ->
                            val cmd = "$cmdPrefix $soPath ${addrs.addrs()}"
                            // println("cmd = $cmd")
                            Runtime.getRuntime().exec(cmd)?.let { process ->
                                BufferedReader(InputStreamReader(process.inputStream)).use { br ->
                                    var line: String?
                                    while (br.readLine().also { l -> line = l } != null) {
                                        if (IGNORE_DECODE == line) {
                                            continue
                                        }
                                        sb.append(line).append("\n")
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        recycle?.destroy()
                    }
                    if (index < addrsList.size - 1) {
                        sb.append("\n\n")
                    }
                }
                callback.onSuccess(sb.toString())
            }
        }.start()
    }

    fun parseAddrs(callStack: String) : MutableList<Addrs> {
        var detail = move2Start(callStack)
        var start = 0
        var end = 0
        var type = -1
        var addrs: Addrs? = null
        val result = mutableListOf<Addrs>()

        while ((detail.startsWith(ADDR_PREFIX_UNITY_SO).also {
                if (it) {
                    start = ADDR_PREFIX_UNITY_SO.length
                    type = Addrs.TYPE_UNITY
                }
            } || detail.startsWith(ADDR_PREFIX_IL2CPP_SO).also {
                if (it) {
                    start = ADDR_PREFIX_IL2CPP_SO.length
                    type = Addrs.TYPE_IL2CPP
                }
            }) && checkIndex(detail.indexOf(ADDR_SUFFIX).also { end = it })) {

            if (addrs?.type != type) {
                addrs = Addrs(type)
                result.add(addrs)
            }
            addrs.addAddr(detail.substring(start, end))
            detail = detail.substring(end + ADDR_SUFFIX.length)
            detail = move2Start(detail)
        }
        return result
    }

    private fun move2Start(detail: String) : String {
        val indexOf = detail.indexOf("lib")
        if (checkIndex(indexOf)) {
            return detail.substring(indexOf)
        }
        return ""
    }

    fun checkIndex(index: Int) : Boolean = index != -1

    data class Addrs (val type: Int) {

        companion object {
            const val TYPE_UNITY = 1
            const val TYPE_IL2CPP = 2
        }

        private val sb = StringBuilder("")

        fun addAddr(addr: String?) {
            if (addr.isNullOrEmpty()) {
                return
            }
            sb.append(addr).append(" ")
        }

        fun addrs() = sb.toString()
    }
}