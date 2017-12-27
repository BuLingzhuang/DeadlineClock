package com.bulingzhuang.dlc.views.fragment


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bulingzhuang.dlc.R
import com.bulingzhuang.dlc.service.CountDownService
import com.bulingzhuang.dlc.util.AccessibilityUtils
import com.bulingzhuang.dlc.util.setViewsOnClickListener
import com.bulingzhuang.dlc.util.showLogE
import com.bulingzhuang.dlc.util.showToast
import kotlinx.android.synthetic.main.fragment_main_home.*


/**
 * ================================================
 * 作    者：bulingzhuang
 * 版    本：1.0
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2017/12/22
 * 描    述：首页Fragment
 * ================================================
 */
class MainHomeFragment : BaseFragment(), View.OnClickListener {

    companion object {
        val tagName: String = MainHomeFragment::class.java.name
        fun newInstance(title: String): MainHomeFragment {
            val fragment = MainHomeFragment()
            val args = Bundle()
            fragment.title = title
            args.putString(TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    private var mBinder: CountDownService.MyBinder? = null

    private val mConn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service != null && service is CountDownService.MyBinder) {
                mBinder = service
                mBinder?.setCountDownListener(tagName, mCountDownListener)
            }
        }

    }

    private val mCountDownListener = object : CountDownService.CountDownListener {
        override fun onTick(cTime: Int) {
            if (scv_progress != null) {
                scv_progress.currentNum = cTime
            }
        }

        override fun onFinish(isIntact: Boolean) {
            //TODO 在这里重置操作
            context.showToast(if (isIntact) {
                "完成专注"
            } else {
                "中断"
            })
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_main_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context.bindService(Intent(context, CountDownService::class.java), mConn, Context.BIND_AUTO_CREATE)
        scv_progress.setListener {
            when (it) {
                in 0..2400 -> tv_coin.text = "x1"
                in 2401..4800 -> tv_coin.text = "x2"
                else -> tv_coin.text = "x3"
            }
        }
        scv_progress.setEdit(true)
        setViewsOnClickListener(this, btn_start)
    }

    override fun onResume() {
        super.onResume()
        if (mBinder != null) {
            if (mBinder!!.isCountDown()) {
                showCountDown()
            } else {
                showEdit()
            }
        }
        btn_start.isEnabled = AccessibilityUtils.isAccessibilityOpen(context)
    }

    override fun onPause() {
        super.onPause()
        v_visibility.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_start -> {
                showCountDown()
                mBinder?.startCountDown(scv_progress.currentNum)
            }
        }
    }

    private fun showCountDown() {
        wv.visibility = View.VISIBLE
        v_visibility.visibility = View.GONE
        btn_start.visibility = View.GONE
        scv_progress.setEdit(false)
    }

    private fun showEdit() {
        wv.visibility = View.GONE
        v_visibility.visibility = View.GONE
        scv_progress.currentNum = 5400
        btn_start.visibility = View.VISIBLE
        scv_progress.setEdit(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinder?.removeCountDownListener(tagName)
        context.unbindService(mConn)
    }
}
