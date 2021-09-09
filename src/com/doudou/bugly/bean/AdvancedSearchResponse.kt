package com.doudou.bugly.bean

data class AdvancedSearchResponse(
    // 一级
    val status: Int,
    val msg: String?,
    val ret: AdvancedSearchResponse,

    // 二级
    val appId: String,
    val platformId: String?,
    val issueList: MutableList<AdvancedSearchResponse>,
    var appName: String? = "unknown",

    // 三级
    val issueId: String,
    var searchVer: String?,
    val issueHash: String,
    val issueCount: String,
    val crashInfo: CrashInfo?,
    val issueVersions: MutableList<IssueVersion>,
    val ftName: String?,
    val issueDocMap: MutableMap<String, Any?>,
    val crossVersionIssueId: Long,
    val esCount: Long,
    val esDeviceCount: Long,
) : BaseBean() {
    fun buildVersion() : String {
        if (issueVersions.isEmpty()) {
            return ""
        }
        var ver = ""
        issueVersions.forEachIndexed { index, issueVersion ->
            ver += if (index === 0) {
                issueVersion.version
            } else {
                "、${issueVersion.version}"
            }
        }
        return ver
    }
}