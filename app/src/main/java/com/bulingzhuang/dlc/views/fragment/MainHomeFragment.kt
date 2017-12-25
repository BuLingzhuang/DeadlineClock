package com.bulingzhuang.dlc.views.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import com.bulingzhuang.dlc.R
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
class MainHomeFragment : BaseFragment() {

    companion object {
        fun newInstance(title: String): MainHomeFragment {
            val fragment = MainHomeFragment()
            val args = Bundle()
            fragment.title = title
            args.putString(TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_main_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scv_progress.setListener {
            when (it) {
                in 0..2400 -> tv_coin.text = "x1"
                in 2401..4800 -> tv_coin.text = "x2"
                else -> tv_coin.text = "x3"
            }
        }
    }
}
