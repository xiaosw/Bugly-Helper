package com.doudou.bugly.bean

data class AppInfo(
    val appName: String,
    val appId: String,
    val pid: Int,
    val logoUrl: String,
    val type: Int,
    val isSdkApp: Int,
    val appKey: String,
    val appFrom: Int,
    val isGray: Boolean,
    val createTime: String,
    val userdataEnable: Int,
    val ownerId: String,
    val memberCount: Int,
    val enableUserAuit: Int,
    val showAuit: Int,
    val betaEnable: Int
) : BaseBean()