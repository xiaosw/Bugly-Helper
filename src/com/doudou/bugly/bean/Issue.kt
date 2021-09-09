package com.doudou.bugly.bean

data class Issue(
    val crashNum: Int,
    val exceptionName: String,
    val exceptionMessage: String,
    val keyStack: String,
    val lastestUploadTime: String,
    val issueId: String,
    val imeiCount: Int,
    val processor: String,
    val status: String,
    val count: Int,
    val ftName: String,
    val issueVersions: MutableList<IssueVersion>,
) {

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