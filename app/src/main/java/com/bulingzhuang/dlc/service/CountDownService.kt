package com.bulingzhuang.dlc.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import com.bulingzhuang.dlc.util.showLogE

/**
 * ================================================
 * 作    者：bulingzhuang
 * 版    本：1.0
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2017/12/26
 * 描    述：倒计时Service
 * ================================================
 */
class CountDownService : Service() {

    interface CountDownListener {
        fun onTick(cTime: Int)
        fun onFinish(isIntact: Boolean)
    }

    companion object {
        private val COUNT_DOWN_INTERVAL = 1000L
    }

    private val mListenerList: HashMap<String, CountDownListener> = HashMap()
    private var mCount: MyTimeCount? = null
    private val mBinder = MyBinder()


    override fun onCreate() {
        super.onCreate()
        showLogE("service的onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showLogE("service的onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        showLogE("service的onDestroy")
        mBinder.cancelCountDown()
        super.onDestroy()
    }

    inner class MyBinder : Binder() {
        fun startCountDown(secondTime: Int) {
            mCount = MyTimeCount(secondTime * 1000L, COUNT_DOWN_INTERVAL)
            mCount?.start()
        }

        fun cancelCountDown() {
            if (mCount != null) {
                showLogE("binder的cancelCountDown")
                mListenerList.forEach { it.value.onFinish(false) }
                mCount?.cancel()
                mCount = null
            }
        }

        fun setCountDownListener(tag: String, listener: CountDownListener) {
            mListenerList.put(tag, listener)
        }

        fun removeCountDownListener(tag: String) {
            mListenerList.remove(tag)
        }

        fun isCountDown(): Boolean {
            return mCount != null
        }
    }

    private inner class MyTimeCount(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            mListenerList.forEach { it.value.onFinish(true) }
            cancel()
        }

        override fun onTick(millisUntilFinished: Long) {
            mListenerList.forEach { it.value.onTick((millisUntilFinished / 1000).toInt()) }
        }

    }
}
