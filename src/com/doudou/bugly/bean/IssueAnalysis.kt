package com.doudou.bugly.bean

data class IssueAnalysis(
    var issueId: String = NO_ID,
    var exceptionName: String = "",
    var productVersion: String = "",
    var version: String = "",
    var status: String = "",
    var url: String = "",
    var crashNum: Int = 0,
    var imeiCount: Int = 0,
    var stackFeature: String = "",
    var callStack: String = "",
    var callStackDecode: String = "",
    var productName: String = "",
    var lastCrashTime: String = "",
    var reportUserId: String = "",
    var currentState: String = "",
    var handlePeople: String = "",
    var hardware: String = "",
    var osVersion: String = "",
    var rom: String = "",
    var cpuName: String = "",
    var cpuType: String = "",
    var startTime: Long = 0,
    var crashTime: String = "",
    var launchTime: Long = 0,
) : BaseBean() {

    fun isValid() = issueId != NO_ID

    companion object {
        private const val NO_ID = ""
    }

}