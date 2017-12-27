package com.bulingzhuang.dlc.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.bulingzhuang.dlc.service.DetectionService

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2017/12/27
 * 描    述：
 * ================================================
 */
class AccessibilityUtils {
    companion object {
        /**
         * 检查当前应用是否开启辅助权限
         */
        fun isAccessibilityOpen(context: Context): Boolean {
            val serviceName = context.packageName + "/" + DetectionService::class.java.canonicalName
            val enabledI = Settings.Secure.getInt(context.applicationContext.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
            return if (enabledI == 1) {
                val servicesListStr = Settings.Secure.getString(context.applicationContext.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                val split = servicesListStr.split(":")
//                showLogE("获取到的数据：$servicesListStr")
                split.contains(serviceName)
            } else {
                false
            }
        }

        /**
         * 前往辅助开启页面
         */
        fun goAccess(context: Context) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}