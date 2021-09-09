package com.doudou.bugly.manager

import com.doudou.bugly.bean.*
import com.doudou.bugly.callback.Callback
import com.doudou.bugly.http.OkHttpUtil
import com.google.gson.Gson
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger

object BuglyManager {
    const val APP_ID_PLACEHOLDER = "###app_id###"
    const val ISSUE_PLACEHOLDER = "###issue###"
    private const val CRASH_HASH_PLACEHOLDER = "###crash_hash###"

    /**
     * 崩溃分析: 崩溃
     */
    const val URL_CRASH_ANALYSIS_ISSUE_DETAIL = "https://bugly.qq.com/v2/crash-reporting/crashes/${APP_ID_PLACEHOLDER}/${ISSUE_PLACEHOLDER}?pid=1"
    /**
     * ANR 分析
     */
    const val URL_ANR_ANALYSIS_ISSUE_DETAIL = "https://bugly.qq.com/v2/crash-reporting/blocks/${APP_ID_PLACEHOLDER}/${ISSUE_PLACEHOLDER}?pid=1#"

    /**
     * 栈详情
     */
    private const val URL_STACK_DETAIL = "https://bugly.qq.com/v4/api/old/get-last-crash?appId=${APP_ID_PLACEHOLDER}&pid=1&issueId=${ISSUE_PLACEHOLDER}&crashDataType=undefined"
    private const val URL_STACK_DETAIL_BY_HASH = "https://bugly.qq.com/v4/api/old/get-crash-detail?appId=${APP_ID_PLACEHOLDER}&pid=1&crashHash=${CRASH_HASH_PLACEHOLDER}&fsn=3035d1bd-c0e0-49ef-8a5e-042bd711fc01"

    /**
     * 应用列表
     */
    private const val URL_APP_LIST = "https://bugly.qq.com/v4/api/old/app-list"

    fun queryAppList(token: String, oldCookie: String, callback: Callback<AppListResponse>) {
        println("query app list...")
        OkHttpUtil.get(token, oldCookie, URL_APP_LIST, object : OkHttpUtil.CommonCallback {
            override fun onComplete(code: Int, response: String?) {
                println("query app list code = $code")
                response?.let { responseJson ->
                    val appList = Gson().fromJson(responseJson, AppListResponse::class.java)
                    println("query app list complete...${appList.data?.size}")
                    callback.onSuccess(appList)
                    return
                }
                callback?.onFail(-1, "response is null!")
            }

        })
    }

    fun queryCrashAnalysis(appId: String, pageIndex: Int, pageSize: Int, token: String, cookie: String,
                           callback: Callback<MutableList<IssueAnalysis>>) {
        val url = "https://bugly.qq.com/v4/api/old/get-issue-list?start=${pageIndex * pageSize}&searchType=errorType&exceptionTypeList=Crash,Native&pid=1&platformId=1&date=last_7_day&sortOrder=desc&status=0&rows=${pageSize}&sortField=uploadTime&appId=${appId}&fsn=831bde36-fd95-4179-b836-df19b3a77b11"
//        val url = "https://bugly.qq.com/v4/api/old/get-issue-list?start=${pageIndex * pageSize}&searchType=errorType&exceptionTypeList=Crash,Native&pid=1&platformId=1&startDateStr=2021-08-05&endDateStr=2021-08-05&sortOrder=desc&status=0&rows=${pageSize}&sortField=uploadTime&appId=${appId}&fsn=831bde36-fd95-4179-b836-df19b3a77b11"
        println("queryCrashAnalysis...")
        OkHttpUtil.get(token, cookie, url, object : OkHttpUtil.CommonCallback {
            override fun onComplete(code: Int, response: String?) {
                println("code = $code, response = $response")
                response?.let { responseJson ->
                    println("queryCrashAnalysis parse...")
                    parseCrashAnalysisResponse(appId, responseJson).let {
                        callback.onSuccess(it)
                    }
                }
            }

        })
    }

    private fun parseCrashAnalysisResponse(appId: String, responseJson: String) : MutableList<IssueAnalysis> {
        val issueAnalysisList = mutableListOf<IssueAnalysis>()
        try {
            val issueList = Gson().fromJson(responseJson, CrashAnalysis::class.java).data.issueList
            issueList.forEach { issue ->
                IssueAnalysis().apply {
                    issueId = issue.issueId
                    exceptionName = issue.exceptionName
                    stackFeature = issue.keyStack
                    version = issue.buildVersion()
                    this.url = URL_CRASH_ANALYSIS_ISSUE_DETAIL
                        .replace(APP_ID_PLACEHOLDER, appId)
                        .replace(ISSUE_PLACEHOLDER, issueId)
                    crashNum = issue.crashNum
                    imeiCount = issue.imeiCount
                    productName = "迷你军团"
                    lastCrashTime = issue.lastestUploadTime
                    status = issue.status
                    issueAnalysisList.add(this)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return issueAnalysisList
    }

    private fun addAndGet(count: AtomicInteger, total: Int) = count.addAndGet(1) === total

    private fun safe2Long(any: Any?, def: Long = 0L) : Long {
        try {
            return any?.toString()?.toLong() ?: def
        } catch (ignore: Exception) {}
        return def
    }

    fun queryStackDetail(token: String, cookie: String, url: String, appId: String, callback: Callback<StackDetail>) {
//        println("queryStackDetail...")
//        println("queryStackDetail: $url")
        OkHttpUtil.get(token, cookie, url, object : OkHttpUtil.CommonCallback {
            override fun onComplete(code: Int, decode: String?) {
//                println("queryStackDetail complete...")
                if (code != 200) {
                    callback.onFail(code, null)
                    return
                }
                val responseJson = decode ?: ""
                if (responseJson.isEmpty()) {
                    callback.onFail(code, "response is null!")
                    return
                }
                val stackDetail = Gson().fromJson(responseJson, StackDetail::class.java)
                val detailUrlByHash = URL_STACK_DETAIL_BY_HASH
                    .replace(APP_ID_PLACEHOLDER, appId)
                    .replace(CRASH_HASH_PLACEHOLDER, stackDetail.data?.crashHash ?: "")
                queryStackDetailByHash(token, cookie, detailUrlByHash, object : Callback<StackDetail> {
                    override fun onFail(code: Int, msg: String?) {
                        callback.onSuccess(stackDetail)
                    }

                    override fun onSuccess(t: StackDetail) {
                        stackDetail.data?.crashMap = t.data?.crashMap
                        stackDetail.data?.detailMap = t.data?.detailMap
                        callback.onSuccess(stackDetail)
                    }
                })
            }
        })
    }

    fun queryStackDetailByHash(token: String, cookie: String, url: String, callback: Callback<StackDetail>) {
//        println("queryStackDetailByHash: $url")
        OkHttpUtil.get(token, cookie, url, object : OkHttpUtil.CommonCallback {
            override fun onComplete(code: Int, decode: String?) {
                if (code != 200) {
                    callback.onFail(code, null)
                    return
                }
                val responseJson = decode ?: ""
                // println("queryStackDetailByHash: response = $responseJson")
                if (responseJson.isEmpty()) {
                    callback.onFail(code, "response is null!")
                    return
                }
                callback.onSuccess(Gson().fromJson(responseJson, StackDetail::class.java))
            }
        })
    }

    fun advancedSearch(
        appId: String, token: String, cookieOldApi: String, cookieNewApi: String, version: String?, startDateStr: String,
        endDateStr: String, pageIndex: Int, pageSize: Int, callback: Callback<AdvancedSearchResponse>
    ) {
        queryAppList(token, cookieOldApi, object : Callback<AppListResponse> {
            override fun onFail(code: Int, msg: String?) {
                doLast(mutableListOf())
            }

            override fun onSuccess(t: AppListResponse) {
                doLast(t.data ?: mutableListOf())
            }

            private fun doLast(appList: MutableList<AppInfo>) {
                println("advancedSearch...")
                val searchUrl = version?.let {
                    "https://bugly.qq.com/v2/search?startDateStr=${startDateStr}&start=${pageIndex * pageSize}&userSearchPage=%2Fv2%2Fcrash-reporting%2Fadvanced-search%2F2527295ba1&endDateStr=${endDateStr}&pid=1&platformId=1&date=custom&sortOrder=desc&useSearchTimes=11&version=${version}&rows=${pageSize}&sortField=matchCount&appId=${appId}&fsn=d054240e-9fbf-432c-a1fc-e7ac7770a13c"
                } ?: "https://bugly.qq.com/v2/search?startDateStr=${startDateStr}&start=${pageIndex * pageSize}&userSearchPage=%2Fv2%2Fcrash-reporting%2Fadvanced-search%2F2527295ba1&endDateStr=${endDateStr}&pid=1&platformId=1&date=custom&sortOrder=desc&useSearchTimes=11&rows=${pageSize}&sortField=matchCount&appId=${appId}&fsn=d054240e-9fbf-432c-a1fc-e7ac7770a13c"
                var appName = "Unknown"
                appList.forEach {
                    if (it.appId == appId) {
                        appName = it.appName
                        return@forEach
                    }
                }
                OkHttpUtil.get(token, cookieNewApi, searchUrl, object : OkHttpUtil.CommonCallback {
                    override fun onComplete(code: Int, response: String?) {
                        if (code != 200) {
                            callback.onFail(code, response)
                            return
                        }
                        println("advancedSearch: code = $code")
                        response?.let { responseJson ->
                            val advancedSearchResponse = Gson().fromJson(responseJson, AdvancedSearchResponse::class.java)
                            val count = AtomicInteger()
                            val size = advancedSearchResponse.ret.issueList.size
                            println("advancedSearch: size = $size")
                            val progress = Progress(size, count.get())
                            advancedSearchResponse.ret.issueList.forEach { issue ->
                                issue.searchVer = version
                                issue.appName = appName
//                        if (issue.crashInfo?.crashDocMap.isNullOrEmpty()) {
                                if (true) {
                                    val detailUrl = URL_STACK_DETAIL
                                        .replace(APP_ID_PLACEHOLDER, appId)
                                        .replace(ISSUE_PLACEHOLDER, issue.issueId)
                                    queryStackDetail(token, cookieOldApi, detailUrl, appId, object : Callback<StackDetail> {
                                        override fun onFail(code: Int, msg: String?) {
                                            if (addAndGet(count, size)) {
                                                complete(advancedSearchResponse)
                                            }
                                            progress.update(count.get())
                                        }

                                        override fun onSuccess(t: StackDetail) {
                                            if (t.data?.crashMap?.isEmpty() != false) {
                                                println("----------------> t = $t")
                                            }
                                            issue.crashInfo?.apply {
//                                        if (crashDocMap.isNullOrEmpty()) {process
//                                            crashDocMap = t.data?.crashMap
//                                        }
                                                crashDocMap = t.data?.crashMap.apply {
                                                    this?.put("launchTime", t.data?.launchTime)
                                                }

                                            }
                                            if (addAndGet(count, size)) {
                                                complete(advancedSearchResponse)
                                            }
                                            progress.update(count.get())
                                        }

                                    })
                                } else {
                                    val detailUrlByHash = URL_STACK_DETAIL_BY_HASH
                                        .replace(APP_ID_PLACEHOLDER, appId)
                                        .replace(CRASH_HASH_PLACEHOLDER, issue.crashInfo?.crashHash ?: "")
                                    queryStackDetailByHash(token, cookieOldApi, detailUrlByHash, object : Callback<StackDetail> {
                                        override fun onFail(code: Int, msg: String?) {
                                            if (addAndGet(count, size)) {
                                                complete(advancedSearchResponse)
                                            }
                                            progress.update(count.get())
                                        }

                                        override fun onSuccess(t: StackDetail) {
                                            issue.crashInfo?.apply {
                                                println("issueId =${issue.issueId}, ${t.data?.launchTime}")
                                                crashDocMap?.put("launchTime", t.data?.launchTime)
                                            }
                                            if (addAndGet(count, size)) {
                                                complete(advancedSearchResponse)
                                            }
                                            progress.update(count.get())
                                        }
                                    })
                                }
                            }
//                    callback.onSuccess(advancedSearchResponse)
                        } ?: callback.onFail(code, "response is null!")

                    }

                    private fun complete(advancedSearchResponse: AdvancedSearchResponse) {
                        callback.onSuccess(advancedSearchResponse)
                    }

                })
            }

        })
    }

    fun generatorExcel(appId: String, token: String, cookieOldApi: String, cookieNewApi: String, version: String?,
                       startDateStr: String, endDateStr: String, pageIndex: Int, pageSize: Int, unitySoPath: String,
                       il2cppSoPath: String, outDir: String, ndk: String) {
        val start = System.currentTimeMillis()
        println("generatorExcel... ")
        advancedSearch(appId, token, cookieOldApi, cookieNewApi, version, startDateStr, endDateStr, pageIndex, pageSize,
            object : Callback<AdvancedSearchResponse> {
                override fun onFail(code: Int, msg: String?) {
                    println("generatorExcel fail! code = $code, msg = $msg")
                }

                override fun onSuccess(response: AdvancedSearchResponse) {
                    val count = AtomicInteger()
                    val size = response.ret.issueList.size
                    // println("advancedSearch complete: count = $size")
                    println("decodeCallStack...")
                    response.ret.issueList.forEach {
                        val callStack = it.crashInfo?.crashDocMap?.get("callStack")?.toString() ?: ""
                        CallStackDecoder.decodeCallStack(callStack, ndk, unitySoPath, il2cppSoPath, object : Callback<String> {
                            override fun onFail(code: Int, msg: String?) {
                                if (addAndGet(count, size)) {
                                    onComplete()
                                }
                            }

                            override fun onSuccess(decodeAddr: String) {
                                it.crashInfo?.crashDocMap?.put("callStackDecode", decodeAddr)
                                if (addAndGet(count, size)) {
                                    onComplete()
                                }
                            }

                            private fun onComplete() {
                                println("decodeCallStack complete...")
                                val excelName = version?.let {
                                    "Bugly_advanced_search_${version}_${startDateStr}_$endDateStr"
                                } ?: "Bugly_advanced_search_${startDateStr}_$endDateStr"
                                ExcelManager.createCrashTable(outDir, excelName, response)
                                println("total duration = ${System.currentTimeMillis() - start}")
                            }

                        })
                    }
                }

            })
    }

    fun generatorExcel(appId: String, pageIndex: Int, pageSize: Int, token: String, cookie: String,
                       unitySoPath: String, il2cppSoPath: String, outDir: String, ndk: String) {
        val start = System.currentTimeMillis()
        println("generatorExcel...")
        queryCrashAnalysis(appId, pageIndex, pageSize, token, cookie, object : Callback<MutableList<IssueAnalysis>> {
            override fun onFail(code: Int, msg: String?) {
                println("queryCrashAnalysis fail!")
            }

            override fun onSuccess(t: MutableList<IssueAnalysis>) {
                val count = AtomicInteger()
                val size = t.size
                println("queryStackDetail...")
                t.forEach { issueAnalysis ->
                    val detailUrl = URL_STACK_DETAIL
                        .replace(APP_ID_PLACEHOLDER, appId)
                        .replace(ISSUE_PLACEHOLDER, issueAnalysis.issueId)
                    queryStackDetail(token, cookie, detailUrl, appId, object : Callback<StackDetail> {
                        override fun onFail(code: Int, msg: String?) {
                            addAndGet(count, size)
                            if (addAndGet(count, size)) {
                                onComplete()
                            }
                        }

                        override fun onSuccess(detail: StackDetail) {
                            issueAnalysis.productVersion = detail.data?.productVersion ?: ""
                            issueAnalysis.callStack = detail.data?.callStack ?: ""
                            issueAnalysis.hardware = detail.data?.hardware ?: ""
                            issueAnalysis.osVersion = detail.data?.osVersion ?: ""
                            issueAnalysis.rom = detail.data?.rom ?: ""
                            issueAnalysis.cpuName = detail.data?.cpuName ?: ""
                            issueAnalysis.cpuType = detail.data?.cpuType ?: ""
                            issueAnalysis.startTime = detail.data?.startTime ?: 0
                            issueAnalysis.crashTime = detail.data?.crashTime ?: ""
                            issueAnalysis.launchTime = safe2Long(detail.data?.launchTime)
                            issueAnalysis.reportUserId = detail.data?.crashMap?.get("userId")?.toString() ?: ""
                            if (addAndGet(count, size)) {
                                onComplete()
                            }
                        }

                        private fun onComplete() {
                            println("queryStackDetail complete...")
                            println("decodeCallStack...")
                            val count = AtomicInteger()
                            val size = t.size
                            t.forEach {
                                CallStackDecoder.decodeCallStack(it.callStack, ndk, unitySoPath, il2cppSoPath, object : Callback<String> {
                                    override fun onFail(code: Int, msg: String?) {
                                        if (addAndGet(count, size)) {
                                            onComplete()
                                        }
                                    }

                                    override fun onSuccess(t: String) {
                                        it.callStackDecode = t
                                        if (addAndGet(count, size)) {
                                            onComplete()
                                        }
                                    }

                                    private fun onComplete() {
                                        println("decodeCallStack complate...")
                                        ExcelManager.createCrashTable(outDir, "CrashAnalysis", t)
                                        println("total duration = ${System.currentTimeMillis() - start}")
                                    }

                                })
                            }
                        }

                    })
                }

            }

        })
    }

}