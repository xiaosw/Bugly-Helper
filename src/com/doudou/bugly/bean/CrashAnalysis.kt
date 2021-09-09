package com.doudou.bugly.bean


data class CrashAnalysis(
    // 一级
    val code: String,
    val msg: String?,
    val data: CrashAnalysis,
    val traceId: String,
    val timestamp: String,

    // 二级
    val appId: String,
    val platformId: String,
    val issueList: MutableList<Issue>,

) : BaseBean()