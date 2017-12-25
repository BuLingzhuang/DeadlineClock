package com.bulingzhuang.dlc.util

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bulingzhuang.dlc.BuildConfig

/**
 * ================================================
 * 作    者：bulingzhuang
 * 版    本：1.0
 * 创建日期：2017/11/9
 * 描    述：扩展函数
 * 修订历史：
 * ================================================
 */


/**
 * 普通Toast(扩展Context)
 */
fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, length).show()
}

/**
 * 普通Toast(扩展String)
 */
fun String.showToast(context: Context, length: Int = Toast.LENGTH_SHORT){
    Toast.makeText(context,this,length).show()
}

/**
 * 普通LogE
 */
fun showLogE(msg: String, tag: String = "BankSteel") {
    if (BuildConfig.DEBUG) {
        Log.e(tag, msg)
    }
}

fun setViewsOnClickListener(listener: View.OnClickListener,vararg views: View){
    views.forEach { it.setOnClickListener(listener) }
}