package com.doudou.bugly.manager

import com.doudou.bugly.Log
import com.doudou.bugly.bean.AdvancedSearchResponse
import com.doudou.bugly.bean.IssueAnalysis
import com.doudou.bugly.config.CrashExcelConfig
import com.doudou.bugly.config.DefMsgConfig
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.Color
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object ExcelManager {

    fun createCrashTable(outDir: String, name: String, response: AdvancedSearchResponse) {
        Log.i("createCrashTable: $name")
        with(XSSFWorkbook()) {
            val crashSheet = createSheet("Crash").also {
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_STACK_FEATURE]!!, 256 * 80)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_EXP_MESSAGE]!!, 256 * 80)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_PRODUCT_VERSION]!!, 256 * 16)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_VERSION]!!, 256 * 26)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_STACK_DETAIL]!!, 256 * 50)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_STACK_DETAIL_DECODE]!!, 256 * 100)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_CRASH_URL]!!, 256 * 80)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_ERROR_TYPE]!!, 256 * 40)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_LAST_CRASH_TIME]!!, 256 * 20)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_LAUNCHER_TIME]!!, 256 * 16)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_USER_ID]!!, 256 * 32)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_OS_VERSION]!!, 256 * 26)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_HARDWARE]!!, 256 * 20)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_DEVICE_ID]!!, 256 * 20)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_ROM]!!, 256 * 40)
            }
            val titleRow = crashSheet.createRow(0)
            CrashExcelConfig.columns.forEach { (title, columnIndex) ->
                titleRow.createCell(columnIndex).also {
                    it.setCellValue(title)
                }
            }

            val zeroTime = Calendar.getInstance().also {
                it.set(Calendar.HOUR_OF_DAY, 0)
                it.set(Calendar.MINUTE, 0)
                it.set(Calendar.SECOND, 0)
                it.set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val useTimeFormat = SimpleDateFormat("HH时mm分ss秒")
            response.ret.issueList.forEachIndexed { index, issue ->
                crashSheet.createRow(index + 1).apply {
                    with(issue) {
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_ISSUE_ID]!!).setCellValue(issueId)
                        val expName = get(issueDocMap, "expName")
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_ERROR_TYPE]!!).setCellValue(expName)
//                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_PRODUCT_VERSION]!!).setCellValue(get(issueDocMap, "productVersion"))
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_PRODUCT_VERSION]!!).setCellValue(searchVer)

                        val expMessage = get(crashInfo?.crashDocMap, "expMessage")?.let {
                            val signalIndexOf = it.indexOf("signal ")
                            if (signalIndexOf != -1) {
                                var temp = it.substring(signalIndexOf)
                                val end = it.indexOf("\n")
                                return@let if (end === -1) temp else temp.substring(0, end)
                            }
                            it
                        }
                        var ver = get(issueDocMap, "subIssueVersions")
                        if (ver.isNullOrEmpty()) {
                            ver = buildVersion()
                        }
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_VERSION]!!).setCellValue(ver)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_PRODUCT_NAME]!!).setCellValue(appName)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_EXCEPTION_COUNT]!!).setCellValue(get(issueDocMap, "count").toFloat().toInt().toString())
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_IMEI_COUNT]!!).setCellValue(get(issueDocMap, "deviceCount").toFloat().toInt().toString())
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_STACK_FEATURE]!!).setCellValue(get(issueDocMap, "keyStack"))
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_EXP_MESSAGE]!!).setCellValue(expMessage)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_STACK_DETAIL]!!).setCellValue(get(crashInfo?.crashDocMap, "callStack"))

                        val callStackDecode = get(crashInfo?.crashDocMap, "callStackDecode")
                        val callStackDecodeStyle = createCellStyle()
                        if (callStackDecode.startsWith(DefMsgConfig.NOT_FIND_ADDR)
                            || callStackDecode.startsWith(DefMsgConfig.NOT_CONFIG_ABI)) {
                            callStackDecodeStyle.setFont(createFont().apply {
                                setColor(XSSFColor(Color.RED, null))
                            })
                        }
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_STACK_DETAIL_DECODE]!!).apply {
                            setCellValue(callStackDecode)
                            cellStyle = callStackDecodeStyle
                        }
                        val detailUrl = if ("ANR_EXCEPTION".equals(expName, true)) {
                            BuglyManager.URL_ANR_ANALYSIS_ISSUE_DETAIL
                        } else {
                            BuglyManager.URL_CRASH_ANALYSIS_ISSUE_DETAIL
                        }.replace(BuglyManager.APP_ID_PLACEHOLDER, response.ret.appId)
                            .replace(BuglyManager.ISSUE_PLACEHOLDER, issue.issueId)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_CRASH_URL]!!).setCellValue(detailUrl)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_LAST_CRASH_TIME]!!).setCellValue(get(crashInfo?.crashDocMap, "crashTime"))
                        val launchTime = safe2Int(get(crashInfo?.crashDocMap, "launchTime"), 0)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_LAUNCHER_TIME]!!).setCellValue(useTimeFormat.format(zeroTime + launchTime * 1000))
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_USER_ID]!!).setCellValue(get(crashInfo?.crashDocMap, "userId"))
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_CURRENT_STATUS]!!).setCellValue(get(issueDocMap, "status").toFloat().toInt().toString())
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_HANDLE_PEOPLE]!!).setCellValue(get(issueDocMap, "processorName"))
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_HARDWARE]!!).setCellValue(get(crashInfo?.crashDocMap, "model"))
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_DEVICE_ID]!!).setCellValue(get(crashInfo?.crashDocMap, "deviceId"))
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_OS_VERSION]!!).setCellValue(get(crashInfo?.crashDocMap, "osVer"))
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_ROM]!!).setCellValue(get(crashInfo?.crashDocMap, "rom"))
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_CPU_NAME]!!).setCellValue(get(crashInfo?.crashDocMap, "cpuName"))
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_CPU_TYPE]!!).setCellValue(get(crashInfo?.crashDocMap, "cpuType"))
                    }
                }
            }
            Log.i("createCrashTable complete!")
            val excelName = if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
                name
            } else {
                "${name}.xlsx"
            }
            val dir = File(outDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val path = if (outDir.endsWith(File.separator)) {
                outDir + excelName
            } else {
                outDir + File.separator + excelName
            }
            saveFile(this, path)
        }
    }

    private fun safe2Int(any: Any?, def: Int = 0) : Int {
        return any?.toString()?.let {
            try {
                return it.toInt()
            } catch (ignore: Exception){}
            def
        } ?: def
    }

    private fun get(map: MutableMap<String, Any?>?, key: String) : String {
        return map?.get(key)?.toString() ?: ""
    }

    fun createCrashTable(outDir: String, name: String, issueAnalysisList: MutableList<IssueAnalysis>) {
        Log.i("createCrashTable: $name")
        with(XSSFWorkbook()) {
            val crashSheet = createSheet("Crash").also {
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_STACK_FEATURE]!!, 256 * 80)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_EXP_MESSAGE]!!, 256 * 80)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_PRODUCT_VERSION]!!, 256 * 16)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_VERSION]!!, 256 * 26)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_STACK_DETAIL]!!, 256 * 50)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_STACK_DETAIL_DECODE]!!, 256 * 100)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_CRASH_URL]!!, 256 * 80)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_ERROR_TYPE]!!, 256 * 40)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_LAST_CRASH_TIME]!!, 256 * 20)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_LAUNCHER_TIME]!!, 256 * 16)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_USER_ID]!!, 256 * 32)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_OS_VERSION]!!, 256 * 30)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_HARDWARE]!!, 256 * 20)
                it.setColumnWidth(CrashExcelConfig.columns[CrashExcelConfig.TITLE_ROM]!!, 256 * 30)
            }
            val titleRow = crashSheet.createRow(0)
            CrashExcelConfig.columns.forEach { (title, columnIndex) ->
                titleRow.createCell(columnIndex).also {
                    it.setCellValue(title)
                }
            }

            val zeroTime = Calendar.getInstance().also {
                it.set(Calendar.HOUR_OF_DAY, 0)
                it.set(Calendar.MINUTE, 0)
                it.set(Calendar.SECOND, 0)
                it.set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val useTimeFormat = SimpleDateFormat("HH时mm分ss秒")
            issueAnalysisList.forEachIndexed { index, issueAnalysis ->
                crashSheet.createRow(index + 1).apply {
                    with(issueAnalysis) {
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_ISSUE_ID]!!).setCellValue(issueId)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_ERROR_TYPE]!!).setCellValue(exceptionName)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_PRODUCT_VERSION]!!).setCellValue(productVersion)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_VERSION]!!).setCellValue(version)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_PRODUCT_NAME]!!).setCellValue(productName)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_EXCEPTION_COUNT]!!).setCellValue("$crashNum")
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_IMEI_COUNT]!!).setCellValue("$imeiCount")
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_STACK_FEATURE]!!).setCellValue(stackFeature)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_STACK_DETAIL]!!).setCellValue(callStack)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_STACK_DETAIL_DECODE]!!).setCellValue(callStackDecode)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_CRASH_URL]!!).setCellValue(url)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_LAST_CRASH_TIME]!!).setCellValue(lastCrashTime)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_LAUNCHER_TIME]!!).setCellValue(useTimeFormat.format(zeroTime + launchTime * 1000))
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_USER_ID]!!).setCellValue(reportUserId)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_CURRENT_STATUS]!!).setCellValue(status)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_HANDLE_PEOPLE]!!).setCellValue(handlePeople)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_HARDWARE]!!).setCellValue(hardware)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_OS_VERSION]!!).setCellValue(osVersion)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_ROM]!!).setCellValue(rom)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_CPU_NAME]!!).setCellValue(cpuName)
                        createCell(CrashExcelConfig.columns[CrashExcelConfig.TITLE_CPU_TYPE]!!).setCellValue(cpuType)
                    }
                }
            }

            val excelName = if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
                name
            } else {
                "${name}.xlsx"
            }
            val path = if (outDir.endsWith(File.separator)) {
                outDir + excelName
            } else {
                outDir + File.separator + excelName
            }
            val p = saveFile(this, path)
            Log.i("createCrashTable complete: $p")
        }
    }

    private fun saveFile(workbook: Workbook, path: String) : String? {
        val file = File(path)
        try {
            Log.i("saveFile: $path")
            if (file.exists()) {
                file.delete()
            }
            FileOutputStream(file).use {
                workbook.write(it)
            }
            Log.i("save file success!")
            return path
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.i("save file fail!")
        return null
    }

}