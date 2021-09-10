package com.doudou.bugly.config

object CrashExcelConfig {

    const val TITLE_ISSUE_ID = "issueId"
    const val TITLE_ERROR_TYPE = "错误类型"
    const val TITLE_PRODUCT_VERSION = "软件版本"
    const val TITLE_VERSION = "异常版本"
    const val TITLE_PRODUCT_NAME = "产品名称"
    const val TITLE_EXCEPTION_COUNT = "异常次数"
    const val TITLE_IMEI_COUNT = "影响设备数"
    const val TITLE_STACK_FEATURE = "堆栈特征"
    const val TITLE_STACK_DETAIL = "堆栈详情"
    const val TITLE_STACK_DETAIL_DECODE = "翻译堆栈"
    const val TITLE_CRASH_URL = "Crash详情链接"
    const val TITLE_LAUNCHER_TIME = "使用时长"
    const val TITLE_USER_ID = "用户 ID"
    const val TITLE_LAST_CRASH_TIME = "最后一次Crash时间"
    const val TITLE_CURRENT_STATUS = "当前状态"
    const val TITLE_HANDLE_PEOPLE = "处理人"
    const val TITLE_HARDWARE = "设备机型"
    const val TITLE_DEVICE_ID = "设备ID"
    const val TITLE_OS_VERSION = "系统版本"
    const val TITLE_ROM = "ROM"
    const val TITLE_CPU_NAME = "CUP名字"
    const val TITLE_CPU_TYPE = "CPU架构"

    val headers = mutableListOf<String>().also {
        it.add(TITLE_ISSUE_ID)
        it.add(TITLE_ERROR_TYPE)
        it.add(TITLE_PRODUCT_VERSION)
        it.add(TITLE_VERSION)
        it.add(TITLE_PRODUCT_NAME)
        it.add(TITLE_EXCEPTION_COUNT)
        it.add(TITLE_IMEI_COUNT)
        it.add(TITLE_STACK_FEATURE)
        it.add(TITLE_STACK_DETAIL)
        it.add(TITLE_STACK_DETAIL_DECODE)
        it.add(TITLE_CRASH_URL)
        it.add(TITLE_LAST_CRASH_TIME)
        it.add(TITLE_LAUNCHER_TIME)
        it.add(TITLE_USER_ID)
        it.add(TITLE_CURRENT_STATUS)
        it.add(TITLE_HANDLE_PEOPLE)
        it.add(TITLE_HARDWARE)
        it.add(TITLE_DEVICE_ID)
        it.add(TITLE_OS_VERSION)
        it.add(TITLE_ROM)
        it.add(TITLE_CPU_NAME)
        it.add(TITLE_CPU_TYPE)
    }

    val columns = mutableMapOf<String, Int>().also {
        headers.forEachIndexed { index, s ->
            it[s] = index
        }
    }

}