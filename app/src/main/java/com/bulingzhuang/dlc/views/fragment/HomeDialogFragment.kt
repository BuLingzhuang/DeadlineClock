package com.bulingzhuang.dlc.views.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import com.bulingzhuang.dlc.R
import com.bulingzhuang.dlc.util.Constants
import com.bulingzhuang.dlc.util.SharePreferencesUtil

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2017/12/27
 * 描    述：
 * ================================================
 */
class HomeDialogFragment : DialogFragment() {
    companion object {
        fun newInstance(): HomeDialogFragment {
            return HomeDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
//        builder.setTitle("说明")
        builder.setView(LayoutInflater.from(context).inflate(R.layout.fragment_home_dialog, null))
        builder.setNeutralButton("取消", { _: DialogInterface, _: Int -> })
        builder.setNegativeButton("不再提醒", { _: DialogInterface, _: Int ->
            run {
                SharePreferencesUtil.setValue(context, Constants.SP_HIDE_START_NOTE, true)
            }
        })
        builder.setPositiveButton("开始", { _: DialogInterface, _: Int ->
            run {
                if (parentFragment != null && parentFragment is MainHomeFragment) {
                    (parentFragment as MainHomeFragment).startCountDown()
                }
            }
        })
        return builder.create()
    }
}