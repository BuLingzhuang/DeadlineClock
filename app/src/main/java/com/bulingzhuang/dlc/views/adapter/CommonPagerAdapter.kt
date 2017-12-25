package com.bulingzhuang.dlc.views.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.bulingzhuang.dlc.views.fragment.BaseFragment

/**
 * ================================================
 * 作    者：bulingzhuang
 * 版    本：1.0
 * 创建日期：2017/11/9
 * 描    述：适用于一般Fragment的适配器
 * 修订历史：
 * ================================================
 */
class CommonPagerAdapter(fm: FragmentManager, private val mFragmentList: MutableList<BaseFragment>) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mFragmentList[position].title
    }
}