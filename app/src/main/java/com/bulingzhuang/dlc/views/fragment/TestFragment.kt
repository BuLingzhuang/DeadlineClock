package com.bulingzhuang.dlc.views.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bulingzhuang.dlc.R

/**
 * ================================================
 * 作    者：bulingzhuang
 * 版    本：1.0
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2017/12/21
 * 描    述：TestFragment
 * ================================================
 */
class TestFragment : BaseFragment() {

    companion object {
        fun newInstance(title: String): TestFragment {
            val fragment = TestFragment()
            val args = Bundle()
            fragment.title = title
            args.putString(TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val inflate = inflater?.inflate(R.layout.fragment_test, container, false)
        inflate?.findViewById<TextView>(R.id.tv_content)?.text = arguments.getString(TITLE, "")
        return inflate
    }

}
