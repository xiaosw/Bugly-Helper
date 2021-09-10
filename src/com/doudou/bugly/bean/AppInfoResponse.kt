package com.doudou.bugly.bean

data class AppInfoResponse(
    val code: Int,
    val data: AppInfoResponse,
    val msg: String?,
    val traceId: String,
    val timestamp: String,

    val versionList: MutableList<Version>,
    val processorList: MutableList<Processor>
) : BaseBean()