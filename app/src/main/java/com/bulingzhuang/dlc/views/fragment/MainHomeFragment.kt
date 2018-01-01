package com.bulingzhuang.dlc.views.fragment


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bulingzhuang.dlc.R
import com.bulingzhuang.dlc.service.CountDownService
import com.bulingzhuang.dlc.util.*
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
        scv_progress.setListener { isEdit, cNum ->
            if (isEdit) {
                when (cNum) {
                    in 0 until 1 -> tv_coin.text = "x0"
                    in 1 until  1800 -> tv_coin.text = "x1"
                    in 1800 .. 3600 -> tv_coin.text = "x2"
                    in 3601 .. 5400 -> tv_coin.text = "x3"
                    in 5401 .. 7200 -> tv_coin.text = "x4"
                    //TODO 待调整
                }
            }
//            in 0 until 1 -> tv_coin.text = "x0"
//            in 1 .. 1800 -> tv_coin.text = "x1"
//            in 1801 .. 3600 -> tv_coin.text = "x2"
//            in 3601 .. 5400 -> tv_coin.text = "x3"
//            in 5401 .. 7200 -> tv_coin.text = "x4"
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
                if (SharePreferencesUtil.getBoolean(Constants.SP_HIDE_START_NOTE)) {
                    startCountDown()
                } else {
                    HomeDialogFragment.newInstance().show(childFragmentManager, "HomeDialog")
                }
            }
        }
    }

    /**
     * 开始专注倒计时
     */
    fun startCountDown() {
        showCountDown()
        mBinder?.startCountDown(scv_progress.currentNum)
    }

    /**
     * 展示倒计时中样式
     */
    private fun showCountDown() {
        scv_progress.setEdit(false)
        wv.visibility = View.VISIBLE
        v_visibility.visibility = View.GONE
        btn_start.visibility = View.GONE
    }

    /**
     * 展示编辑样式
     */
    private fun showEdit() {
        scv_progress.currentNum = 5400
        scv_progress.setEdit(true)
        btn_start.visibility = View.VISIBLE
        wv.visibility = View.GONE
        v_visibility.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinder?.removeCountDownListener(tagName)
        context.unbindService(mConn)
    }
}
