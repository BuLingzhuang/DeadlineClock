package com.bulingzhuang.dlc.base

import android.app.Application
import com.bulingzhuang.dlc.util.SharePreferencesUtil

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2017/12/27
 * 描    述：
 * ================================================
 */
class DLCApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        //初始化SharePreference
        SharePreferencesUtil.initializeInstance(this)
    }
}