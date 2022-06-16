package com.doudou.bugly.manager

import com.doudou.bugly.bean.*
import com.doudou.bugly.callback.Callback
import com.doudou.bugly.http.OkHttpUtil
import com.doudou.bugly.Log
import com.doudou.bugly.config.AppConfig
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

    /**
     * 应用信息
     */
    private const val URL_APP_INFO = "https://bugly.qq.com/v4/api/old/get-app-info?appId=${APP_ID_PLACEHOLDER}&pid=1&types=version,member,tag,channel&fsn=bbf5e2fe-1c6e-4c85-b1a7-f57ae8b1f05a"

    fun queryAppList(token: String, oldCookie: String, callback: Callback<AppListResponse>) {
        Log.i("query app list...")
        OkHttpUtil.get(token, oldCookie, URL_APP_LIST, object : OkHttpUtil.CommonCallback {
            override fun onComplete(code: Int, response: String?) {
                Log.i("query app list code = $code")
                response?.let { responseJson ->
                    val appListResponse = Gson().fromJson(responseJson, AppListResponse::class.java)
                    appListResponse?.data?.let { appList ->
                        val count = AtomicInteger(0)
                        val size = appList.size
                        appList.forEach { appInfo ->
                            queryAppSubInfo(appInfo.appId, token, oldCookie, object : Callback<AppInfoResponse> {
                                override fun onFail(code: Int, msg: String?) {
                                    checkComplete()
                                }

                                override fun onSuccess(t: AppInfoResponse) {
                                    appInfo.processorList = t.data.processorList
                                    appInfo.versionList = t.data.versionList
                                    checkComplete()
                                }

                                fun checkComplete() {
                                    if (addAndGet(count, size)) {
                                        Log.i("query app list complete...$size")
                                        callback.onSuccess(appListResponse)
                                    }
                                }
                            })

                        }
                        return
                    }
                    Log.i("query app list complete...${appListResponse.data?.size}")
                    callback.onSuccess(appListResponse)
                    return
                }
                callback?.onFail(-1, "response is null!")
            }

        })
    }

    private fun queryAppSubInfo(appId: String, token: String, oldCookie: String, callback: Callback<AppInfoResponse>) {
        Log.i("query app($appId) info...")
        OkHttpUtil.get(token, oldCookie, URL_APP_INFO.replace(APP_ID_PLACEHOLDER, appId), object : OkHttpUtil.CommonCallback {
            override fun onComplete(code: Int, response: String?) {
                Log.i("query app($appId) info code = $code")
                response?.let { responseJson ->
                    val appInfo = Gson().fromJson(responseJson, AppInfoResponse::class.java)
                    Log.i("query app($appId) info complete...processor = ${appInfo?.data?.processorList?.size}")
                    callback.onSuccess(appInfo)
                    return
                }
                callback?.onFail(-1, "response is null!")
            }

        })
    }

    fun queryAppInfo(appId: String, token: String, oldCookie: String, callback: Callback<AppInfo>) {
        Log.i("query app($appId) info...")
        OkHttpUtil.get(token, oldCookie, URL_APP_LIST, object : OkHttpUtil.CommonCallback {
            override fun onComplete(code: Int, response: String?) {
                Log.i("query app($appId) code = $code")
                response?.let { responseJson ->
                    val appListResponse = Gson().fromJson(responseJson, AppListResponse::class.java)
                    appListResponse?.data?.let { appList ->
                        appList.forEach { appInfo ->
                            if (appId == appInfo.appId) {
                                queryAppSubInfo(appInfo.appId, token, oldCookie, object : Callback<AppInfoResponse> {
                                    override fun onFail(code: Int, msg: String?) {
                                        Log.i("query app($appId) sub code = $code")
                                        callback.onFail(code, msg)
                                    }

                                    override fun onSuccess(t: AppInfoResponse) {
                                        appInfo.processorList = t.data.processorList
                                        appInfo.versionList = t.data.versionList
                                        callback.onSuccess(appInfo)
                                    }
                                })
                                return
                            }
                        }
                    }
                    Log.i("query app list complete...${appListResponse.data?.size}")
                    callback.onFail(-1, "not find")
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
        Log.i("queryCrashAnalysis...")
        OkHttpUtil.get(token, cookie, url, object : OkHttpUtil.CommonCallback {
            override fun onComplete(code: Int, response: String?) {
                Log.i("code = $code, response = $response")
                response?.let { responseJson ->
                    Log.i("queryCrashAnalysis parse...")
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
//        i("queryStackDetail...")
//        i("queryStackDetail: $url")
        OkHttpUtil.get(token, cookie, url, object : OkHttpUtil.CommonCallback {
            override fun onComplete(code: Int, decode: String?) {
//                i("queryStackDetail complete...")
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
//        i("queryStackDetailByHash: $url")
        OkHttpUtil.get(token, cookie, url, object : OkHttpUtil.CommonCallback {
            override fun onComplete(code: Int, decode: String?) {
                if (code != 200) {
                    callback.onFail(code, null)
                    return
                }
                val responseJson = decode ?: ""
                // i("queryStackDetailByHash: response = $responseJson")
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
        queryAppInfo(appId, token, cookieOldApi, object : Callback<AppInfo> {
            override fun onFail(code: Int, msg: String?) {
                doLast(null)
            }

            override fun onSuccess(appInfo: AppInfo) {
                doLast(appInfo)
            }

            private fun doLast(appInfo: AppInfo?) {
                Log.i("advancedSearch...")
                val processors = mutableMapOf<String, String>()
                var appName = appInfo?.let {
                    it.processorList?.forEach { processor ->
                        processors[processor.userId] = processor.name
                    }
                    it.appName
                } ?: "Unknown"
                queryIssueList(appId, token, cookieOldApi, cookieNewApi, version, startDateStr, endDateStr, pageIndex
                        , pageSize, appName, processors, callback)
            }

        })
    }

    private fun handlerVer(version: String?) : String {
        return if (version?.isNotEmpty() == true) {
            version
        } else "all"
    }

    private fun queryIssueList(appId: String, token: String, cookieOldApi: String, cookieNewApi: String, version: String?
                               , startDateStr: String,endDateStr: String, pageIndex: Int, pageSize: Int, appName: String
                               , processors: MutableMap<String, String>, callback: Callback<AdvancedSearchResponse>
                               , lastData: AdvancedSearchResponse? = null) {
        val searchUrl = version?.let {
            "https://bugly.qq.com/v2/search?startDateStr=${startDateStr}&start=${pageIndex * pageSize}&userSearchPage=%2Fv2%2Fcrash-reporting%2Fadvanced-search%2F2527295ba1&endDateStr=${endDateStr}&pid=1&platformId=1&date=custom&sortOrder=desc&useSearchTimes=11&version=${version}&rows=${pageSize}&sortField=matchCount&appId=${appId}&fsn=d054240e-9fbf-432c-a1fc-e7ac7770a13c"
        } ?: "https://bugly.qq.com/v2/search?startDateStr=${startDateStr}&start=${pageIndex * pageSize}&userSearchPage=%2Fv2%2Fcrash-reporting%2Fadvanced-search%2F2527295ba1&endDateStr=${endDateStr}&pid=1&platformId=1&date=custom&sortOrder=desc&useSearchTimes=11&rows=${pageSize}&sortField=matchCount&appId=${appId}&fsn=d054240e-9fbf-432c-a1fc-e7ac7770a13c"

        Log.i("queryIssueList【${handlerVer(version)}】: page = ${pageIndex + 1}")
        OkHttpUtil.get(token, cookieNewApi, searchUrl, object : OkHttpUtil.CommonCallback {
            override fun onComplete(code: Int, response: String?) {
                if (code != 200) {
                    callback.onFail(code, response)
                    return
                }
                response?.let { responseJson ->
                    val advancedSearchResponse = Gson().fromJson(responseJson, AdvancedSearchResponse::class.java)
                    if (advancedSearchResponse.status != 200) {
                        callback.onFail(advancedSearchResponse.status, advancedSearchResponse.msg)
                        Log.i(" queryIssueList【${handlerVer(version)}】: $response")
                        return
                    }
                    val size = advancedSearchResponse.ret.issueList.size
                    val allData = lastData?.also {
                        it.ret.issueList.addAll(advancedSearchResponse.ret.issueList)
                    } ?: advancedSearchResponse
                    if (size === 0) {
                        complete(allData)
                        return
                    }
                    val count = AtomicInteger()
                    Log.i("queryIssueList【${handlerVer(version)}】: size = $size")
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
                                    val isComplete = addAndGet(count, size)
                                    progress.update(count.get())
                                    if (isComplete) {
                                        if (checkHasNextPage(advancedSearchResponse)) {
                                            queryIssueList(appId, token, cookieOldApi, cookieNewApi, version, startDateStr, endDateStr, pageIndex + 1
                                                    , pageSize, appName, processors, callback, allData)
                                        } else {
                                            complete(allData)
                                        }
                                    }
                                }

                                override fun onSuccess(t: StackDetail) {
                                    if (t.data?.crashMap?.isEmpty() != false) {
                                        Log.i("----------------> t = $t")
                                    }
                                    issue.issueDocMap?.put("processorName", processors[issue.issueDocMap["processor"]])
                                    issue.crashInfo?.apply {
                                        crashDocMap = t.data?.crashMap.apply {
                                            this?.put("launchTime", t.data?.launchTime)
                                        }
                                    }
                                    val isComplete = addAndGet(count, size)
                                    progress.update(count.get())
                                    if (isComplete) {
                                        if (checkHasNextPage(advancedSearchResponse)) {
                                            queryIssueList(appId, token, cookieOldApi, cookieNewApi, version, startDateStr, endDateStr, pageIndex + 1
                                                    , pageSize, appName, processors, callback, allData)
                                        } else {
                                            complete(allData)
                                        }
                                    }
                                }

                            })
                        } else {
                            val detailUrlByHash = URL_STACK_DETAIL_BY_HASH
                                    .replace(APP_ID_PLACEHOLDER, appId)
                                    .replace(CRASH_HASH_PLACEHOLDER, issue.crashInfo?.crashHash ?: "")
                            queryStackDetailByHash(token, cookieOldApi, detailUrlByHash, object : Callback<StackDetail> {
                                override fun onFail(code: Int, msg: String?) {
                                    if (addAndGet(count, size)) {
                                        if (checkHasNextPage(advancedSearchResponse)) {
                                            queryIssueList(appId, token, cookieOldApi, cookieNewApi, version, startDateStr, endDateStr, pageIndex + 1
                                                    , pageSize, appName, processors, callback, allData)
                                        } else {
                                            complete(allData)
                                        }
                                    }
                                    progress.update(count.get())
                                }

                                override fun onSuccess(t: StackDetail) {
                                    issue.crashInfo?.apply {
                                        Log.i("issueId =${issue.issueId}, ${t.data?.launchTime}")
                                        crashDocMap?.put("launchTime", t.data?.launchTime)
                                    }
                                    if (addAndGet(count, size)) {
                                        if (checkHasNextPage(advancedSearchResponse)) {
                                            queryIssueList(appId, token, cookieOldApi, cookieNewApi, version, startDateStr, endDateStr, pageIndex + 1
                                                    , pageSize, appName, processors, callback, allData)
                                        } else {
                                            complete(allData)
                                        }
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

            private fun checkHasNextPage(advancedSearchResponse: AdvancedSearchResponse) : Boolean {
                if (advancedSearchResponse?.ret?.issueList.size === pageSize) {
                    return true
                }
                return false
            }

        })
    }

    fun generatorExcel(appId: String, token: String, cookieOldApi: String, cookieNewApi: String, version: String?,
                       startDateStr: String, endDateStr: String, pageIndex: Int, pageSize: Int
                       , abis: MutableMap<String, SoInfo>, outDir: String, cmd: String, indexCount: AtomicInteger,
                       totalCount: Int, startTime: Long) {
        val start = System.currentTimeMillis()
        Log.i("generatorExcel... ")
        advancedSearch(appId, token, cookieOldApi, cookieNewApi, version, startDateStr, endDateStr, pageIndex, pageSize,
            object : Callback<AdvancedSearchResponse> {
                override fun onFail(code: Int, msg: String?) {
                    Log.i("generatorExcel fail! code = $code, msg = $msg")
                }

                override fun onSuccess(response: AdvancedSearchResponse) {
                    val count = AtomicInteger()
                    val size = response.ret.issueList.size
                    // i("advancedSearch complete: count = $size")
                    Log.i("decodeCallStack... size = $size")
                    val progress = Progress(size, count.get())
                    response.ret.issueList.forEach {
                        val callStack = it.crashInfo?.crashDocMap?.get("callStack")?.toString() ?: ""
                        val cpuType = it.crashInfo?.crashDocMap?.get("cpuType")?.toString()?.toLowerCase() ?: ""
                        val so = abis[cpuType]
                        if (null == so && AppConfig.isDebug) {
                            val issueId = it.crashInfo?.crashDocMap?.get("issueId")?.toString()?.toLowerCase() ?: ""
                            Log.e("【$issueId】 cupType is 【$cpuType】, please config abi [-abis {\"$cpuType\":{\"unity\":\"xxx\", \"il2cpp\":\"xxx\"}}]")
                        }
                        CallStackDecoder.decodeCallStack(callStack, cmd, so?.unity, so?.il2cpp, version, object : Callback<String> {
                            override fun onFail(code: Int, msg: String?) {
                                val isComplete = addAndGet(count, size)
                                progress.update(count.get())
                                if (isComplete) {
                                    onComplete()
                                }
                            }

                            override fun onSuccess(decodeAddr: String) {
                                it.crashInfo?.crashDocMap?.put("callStackDecode", decodeAddr)
                                val isComplete = addAndGet(count, size)
                                progress.update(count.get())
                                if (isComplete) {
                                    onComplete()
                                }
                            }

                            private fun onComplete() {
                                Log.i("decodeCallStack complete...")
                                var pageNum = size / pageSize
                                if (size % pageSize > 0) {
                                    pageNum++
                                }
                                val excelName = version?.let {
                                    "Bugly_advanced_search_${version}_${startDateStr}_${endDateStr}_${pageNum}_$size"
                                } ?: "Bugly_advanced_search_all_${startDateStr}_${endDateStr}_${pageNum}_$size"
                                ExcelManager.createCrashTable(outDir, excelName, response)
                                Log.i("total duration = ${System.currentTimeMillis() - start}")
                                if (addAndGet(indexCount, totalCount)) {
                                    println("\n\n\n")
                                    Log.i("******************** Complete! all duration = ${System.currentTimeMillis() - startTime}ms ********************\n")
                                }
                            }

                        }, cpuType)
                    }
                }

            })
    }

    fun generatorExcel(appId: String, pageIndex: Int, pageSize: Int, token: String, cookie: String,
                       unitySoPath: String, il2cppSoPath: String, outDir: String, cmd: String) {
        val start = System.currentTimeMillis()
        Log.i("generatorExcel...")
        queryCrashAnalysis(appId, pageIndex, pageSize, token, cookie, object : Callback<MutableList<IssueAnalysis>> {
            override fun onFail(code: Int, msg: String?) {
                Log.i("queryCrashAnalysis fail!")
            }

            override fun onSuccess(t: MutableList<IssueAnalysis>) {
                val count = AtomicInteger()
                val size = t.size
                Log.i("queryStackDetail...")
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
                            Log.i("queryStackDetail complete...")
                            Log.i("decodeCallStack...")
                            val count = AtomicInteger()
                            val size = t.size
                            t.forEach {
                                CallStackDecoder.decodeCallStack(it.callStack, cmd, unitySoPath, il2cppSoPath, callback =  object : Callback<String> {
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
                                        Log.i("decodeCallStack complate...")
                                        ExcelManager.createCrashTable(outDir, "CrashAnalysis", t)
                                        Log.i("total duration = ${System.currentTimeMillis() - start}")
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