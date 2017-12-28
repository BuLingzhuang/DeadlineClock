package com.bulingzhuang.dlc.util

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bulingzhuang.dlc.BuildConfig
import com.bulingzhuang.dlc.R

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
fun String.showToast(context: Context, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this, length).show()
}

/**
 * 黑底白字的SnakeBar带点击操作
 */
fun Context.showSnakeBarWithAction(msg: String, genView: View, actionTitle: String, actionListener: View.OnClickListener, duration: Int = Snackbar.LENGTH_LONG) {
    val snackBar = Snackbar.make(genView, msg, duration)
    snackBar.setAction(actionTitle, actionListener)
    val layout = snackBar.view
    layout.background = ContextCompat.getDrawable(this, R.drawable.snackbar_bg_dark)
    val tvText = layout.findViewById<TextView>(android.support.design.R.id.snackbar_text)
    val tvAction = layout.findViewById<TextView>(android.support.design.R.id.snackbar_action)
    tvText.setTextColor(ContextCompat.getColor(this, android.R.color.white))
    tvAction.setTextColor(ContextCompat.getColor(this, R.color.amber500))
    snackBar.show()
}

/**
 * dp转px
 */
fun Context.dp2px(dpValue: Float): Int {
    return (this.resources.displayMetrics.density * dpValue + 0.5f).toInt()
}

/**
 * 普通LogE
 */
fun showLogE(msg: String, tag: String = "BankSteel") {
    if (BuildConfig.DEBUG) {
        Log.e(tag, msg)
    }
}

fun setViewsOnClickListener(listener: View.OnClickListener, vararg views: View) {
    views.forEach { it.setOnClickListener(listener) }
}