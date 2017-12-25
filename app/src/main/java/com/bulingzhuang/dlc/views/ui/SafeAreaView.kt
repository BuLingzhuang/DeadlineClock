package com.bulingzhuang.dlc.views.ui

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.bulingzhuang.dlc.R

/**
 * ================================================
 * 作    者：bulingzhuang
 * 版    本：1.0
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2017/12/20
 * 描    述：圆角View
 * ================================================
 */
class SafeAreaView : View {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(context, attrs)
    }

    private var mType = 0//起始角度
    private val mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)//背景色画笔
    private val xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

    private fun initAttr(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SafeAreaView)
        mType = typedArray.getInt(R.styleable.SafeAreaView_type, 0)
        val bgColor = typedArray.getColor(R.styleable.SafeAreaView_bgColor, ContextCompat.getColor(context, R.color.colorPrimaryDark))
        typedArray.recycle()
        mBgPaint.color = bgColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = setMeasureSize(widthMeasureSpec, 40f)
        val height = setMeasureSize(heightMeasureSpec, 40f)

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), mBgPaint)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), mBgPaint)
            mBgPaint.xfermode = xfermode
            when (mType) {
                0 -> {
                    canvas.drawArc(-width.toFloat(), -height.toFloat(), width.toFloat(), height.toFloat(), 0f, 90f, true, mBgPaint)
                }
                1 -> {
                    canvas.drawArc(0f, -height.toFloat(), width.toFloat() * 2, height.toFloat(), 90f, 90f, true, mBgPaint)
                }
                2 -> {
                    canvas.drawArc(0f, 0f, width.toFloat() * 2, height.toFloat() * 2, 180f, 90f, true, mBgPaint)
                }
                else -> {
                    canvas.drawArc(-width.toFloat(), 0f, width.toFloat(), height.toFloat() * 2, 270f, 90f, true, mBgPaint)
                }
            }
            mBgPaint.xfermode = null
            canvas.restore()
        }
    }

    /**
     * dp转px
     */
    private fun dp2px(dpValue: Float): Int {
        return (context.resources.displayMetrics.density * dpValue + 0.5f).toInt()
    }

    /**
     * 测量尺寸
     */
    private fun setMeasureSize(measureSpec: Int, size: Float): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (mode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> minOf(specSize, dp2px(size))
            MeasureSpec.UNSPECIFIED -> dp2px(size)
            else -> dp2px(size)
        }
    }
}