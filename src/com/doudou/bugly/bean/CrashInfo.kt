package com.doudou.bugly.bean

data class CrashInfo(val crashId: String,
                     val crashHash: String,
                     val tagId: Long,
                     var crashDocMap: MutableMap<String, Any?>?)