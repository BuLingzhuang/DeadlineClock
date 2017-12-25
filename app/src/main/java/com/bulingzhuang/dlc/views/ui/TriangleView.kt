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
 * 描    述：三角View
 * ================================================
 */
class TriangleView : View {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(context, attrs)
    }

    private val mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)//背景色画笔
    private val mBgPath = Path()//背景路径
    private var mHeightPercent = 1f

    private fun initAttr(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TriangleView)
        val bgColor = typedArray.getColor(R.styleable.TriangleView_color, ContextCompat.getColor(context, R.color.colorAccent))
        mHeightPercent = typedArray.getFloat(R.styleable.TriangleView_heightPercent, 1f)
        typedArray.recycle()
        mBgPaint.color = bgColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = setMeasureSize(widthMeasureSpec, 40f)
        val height = setMeasureSize(heightMeasureSpec, 40f)
        mBgPath.moveTo(0f, height.toFloat())
        mBgPath.lineTo(width.toFloat(), height.toFloat())
        mBgPath.lineTo(width.toFloat() / 2, (1 - mHeightPercent) * height)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(mBgPath, mBgPaint)
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