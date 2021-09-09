package com.doudou.bugly.bean

data class IssueVersion(
    val version: String,
    val cont: Int,
    val deviceCount: Int
) : BaseBean()