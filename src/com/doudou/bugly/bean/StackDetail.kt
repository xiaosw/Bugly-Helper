package com.doudou.bugly.bean

data class StackDetail(
    val msg: String?,
    val code: Int,
    val data: StackDetail?,
    val productVersion: String?,
    val callStack: String?,
    val hardware: String?,
    val osVersion: String?,
    val rom: String?,
    val cpuName: String?,
    val cpuType: String?,
    val startTime: Long?,
    val crashTime: String?,
    val launchTime: String,
    val crashHash: String?,
    val userId: String?,
    var crashMap: MutableMap<String, Any?>?,
    var detailMap: MutableMap<String, Any?>?,
) : BaseBean()