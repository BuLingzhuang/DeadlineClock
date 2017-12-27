package com.bulingzhuang.dlc.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bulingzhuang.dlc.util.Constants
import com.bulingzhuang.dlc.util.showLogE

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2017/12/26
 * 描    述：广播监听
 * ================================================
 */
class MyReceiver(private val mListener: ReceiverListener) : BroadcastReceiver() {

    interface ReceiverListener {
        fun isForeground(isForeground: Boolean)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Constants.RECEIVER_ACTION_DETECTION -> {
                val isForeground = intent.getBooleanExtra(Constants.RECEIVER_DETECTION_IS_FOREGROUND, true)
                showLogE("监听到广播，isForeground = $isForeground")
                mListener.isForeground(isForeground)
            }
        }
    }
}