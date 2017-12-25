package com.bulingzhuang.dlc.views.fragment


import android.support.v4.app.Fragment


/**
 * ================================================
 * 作    者：bulingzhuang
 * 版    本：1.0
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2017/12/21
 * 描    述：BaseFragment
 * ================================================
 */
open class BaseFragment : Fragment() {

    var title: String = ""

    companion object {
        val TITLE = "com.intelligent.warehouse.titleName"
    }
}
