package com.bulingzhuang.dlc

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.view.KeyEvent
import android.view.View
import com.bulingzhuang.dlc.receiver.MyReceiver
import com.bulingzhuang.dlc.service.CountDownService
import com.bulingzhuang.dlc.util.*
import com.bulingzhuang.dlc.views.adapter.CommonPagerAdapter
import com.bulingzhuang.dlc.views.fragment.BaseFragment
import com.bulingzhuang.dlc.views.fragment.MainHomeFragment
import com.bulingzhuang.dlc.views.fragment.TestFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener, MyReceiver.ReceiverListener {

    companion object {
        val tagName: String = MainActivity::class.java.name
    }

    private val mFragmentList = ArrayList<BaseFragment>()
    private var mCurrentSelPos = 0

    private var mBinder: CountDownService.MyBinder? = null

    private val mConn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service != null && service is CountDownService.MyBinder) {
                mBinder = service
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        init()
        showLogE("create")
    }

    override fun onResume() {
        super.onResume()
        showLogE("resume")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        if (!AccessibilityUtils.isAccessibilityOpen(this)) {
            showSnakeBarWithAction("麻烦开一下辅助权限，谢谢 ( ˘•ω•˘ )", cl_gen, "前往",
                    View.OnClickListener { AccessibilityUtils.goAccess(this) },
                    Snackbar.LENGTH_INDEFINITE)
        }
    }

    override fun onPause() {
        super.onPause()
        showLogE("pause")
        stopService(Intent(this, CountDownService::class.java))
    }

    override fun onStop() {
        super.onStop()
        showLogE("stop")
    }

    override fun onDestroy() {
        super.onDestroy()
        showLogE("destroy")
        unbindService(mConn)
    }

    private fun init() {
        setViewsOnClickListener(this, iv_home, iv_achievement, iv_mine, iv_shutdown)
        mFragmentList.add(MainHomeFragment.newInstance("首页"))
        mFragmentList.add(TestFragment.newInstance("成就"))
        mFragmentList.add(TestFragment.newInstance("我的"))
        vp_content.adapter = CommonPagerAdapter(supportFragmentManager, mFragmentList)
        vp_content.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                setBarScrolled(position, positionOffset)
            }

            override fun onPageSelected(position: Int) {}
        })
        startService(Intent(this, CountDownService::class.java))
        bindService(Intent(this, CountDownService::class.java), mConn, Context.BIND_AUTO_CREATE)
        val filter = IntentFilter()
        filter.addAction(Constants.RECEIVER_ACTION_DETECTION)
        registerReceiver(MyReceiver(this), filter)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_home -> {
                vp_content.currentItem = 0
            }
            R.id.iv_achievement -> {
                vp_content.currentItem = 1
            }
            R.id.iv_mine -> {
                vp_content.currentItem = 2
            }
            R.id.iv_shutdown -> {//后退
                back()
            }
        }
    }

    /**
     * 设置底部Bar滚动中显示
     */
    private fun setBarScrolled(position: Int, positionOffset: Float) {
//        showLogE("position = $position,positionOffset = $positionOffset")
        when (position) {
            0 -> {
                t_home.alpha = 1 - positionOffset
                t_achievement.alpha = positionOffset
                t_mine.alpha = 0f
            }
            1 -> {
                t_achievement.alpha = 1 - positionOffset
                t_mine.alpha = positionOffset
            }
            2 -> {
                t_home.alpha = 0f
                t_achievement.alpha = 0f
                t_mine.alpha = 1f
            }
        }
    }

    /**
     * 当前应用是否是前台应用的监听
     */
    override fun isForeground(isForeground: Boolean) {
        mBinder?.cancelCountDown()
    }

    private var firstTime = 0L
    //连点两次后退退出应用
    override fun onKeyDown(paramInt: Int, paramKeyEvent: KeyEvent): Boolean {
        if (KeyEvent.KEYCODE_BACK == paramInt) {
            back()
            return true
        }
        return super.onKeyDown(paramInt, paramKeyEvent)
    }

    private fun back() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - firstTime >= 2000L) {
            invalidateOptionsMenu()
            showToast("再点一次退出")
            firstTime = currentTime
        } else {
            finish()
        }
    }
}
