package com.doudou.bugly.bean

/**
 * {
 *      "name":"1.2.8_ProductA32_release",
 *      "enable":1,
 *      "sdkVersion":"3.3.3-3.7.7"
 * }
 */
class Version(val name: String, private val enable: Int, val sdkVersion: String) : BaseBean() {

    fun isEnable() = (enable === 1)

}