package com.bulingzhuang.dlc.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.bulingzhuang.dlc.util.Constants
import com.bulingzhuang.dlc.util.showLogE

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2017/12/26
 * 描    述：
 * ================================================
 */
class DetectionService : AccessibilityService() {

    companion object {
        val systemUI = "com.android.systemui"
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val foregroundPackageName = event.packageName.toString()
            showLogE("当前前台的应用：" + foregroundPackageName)
            val intent = Intent(Constants.RECEIVER_ACTION_DETECTION)
            val isForeground = packageName == foregroundPackageName || systemUI == foregroundPackageName
            intent.putExtra(Constants.RECEIVER_DETECTION_IS_FOREGROUND, isForeground)
            sendBroadcast(intent)
        }
    }
}