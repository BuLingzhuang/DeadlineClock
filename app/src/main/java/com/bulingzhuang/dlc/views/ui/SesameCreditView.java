package com.bulingzhuang.dlc.views.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.bulingzhuang.dlc.R;

/**
 * Created by bulingzhuang
 * on 2016/11/30
 * E-mail:bulingzhuang@foxmail.com
 */

public class SesameCreditView extends View {

    private static int WIDTH = 24;

    private int mMaxNum, mStartAngle;
    private int mSweepOutWidth;
    private Paint mPaint, mPaint_1, mPaint_2, mPaint_3, mPaint_Bg;
    private int mWidth, mHeight;
    private int mRadius;
    private Context mContext;
    private NumChangeListener mListener;
    private boolean isEdit = true;//是否是可编辑模式

    private int[] indicatorColor = {Color.WHITE, ContextCompat.getColor(getContext(), R.color.colorAccent)};
    private int[] indicatorColorBg = {ContextCompat.getColor(getContext(), R.color.colorPrimary), ContextCompat.getColor(getContext(), R.color.colorAccentAlpha)};

    private int currentNum, secondNum;//当前数值，设置好之后的最大数值(第二层数值)
    private SweepGradient mSweepGradient;
    private int mW;//圆环和点宽度
    private float mIndicatorX;
    private float mIndicatorY;
    private RectF mRectF;

    interface NumChangeListener {
        void numChange(boolean isEdit,int cNum);
    }

    public void setListener(NumChangeListener listener) {
        mListener = listener;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public void setCurrentNum(int cNum) {
        if (currentNum > 270 * 20 && cNum < 180 * 20) {
            currentNum = 7200;
        } else if (currentNum < 90 * 20 && cNum > 180 * 20) {
            currentNum = 0;
        } else {
            currentNum = cNum;
        }
        if (mListener != null) {
            mListener.numChange(isEdit,currentNum);
        }
        if (isEdit) {
            secondNum = currentNum;
        }
        invalidate();
    }

    public void setCurrentNumAnim(int num) {
        float duration = (float) Math.abs(num - currentNum) / mMaxNum * 1500 + 500;
        ObjectAnimator anim = ObjectAnimator.ofInt(this, "currentNum", num);
        anim.setDuration((long) Math.min(duration, 2000));
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    public SesameCreditView(Context context) {
        this(context, null);
    }

    public SesameCreditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        //对当前控件不使用硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initPaint();
        initAttr(context, attrs);
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPaint_1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint_1.setStrokeCap(Paint.Cap.ROUND);
        mPaint_2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint_3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint_Bg = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint_1.setStyle(Paint.Style.STROKE);
        mPaint_2.setStyle(Paint.Style.FILL);
        mPaint_2.setColor(0xffffffff);
        mPaint_2.setMaskFilter(new BlurMaskFilter(dp2px(mContext, 4), BlurMaskFilter.Blur.SOLID));
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SesameCreditView);
        mMaxNum = typedArray.getInt(R.styleable.SesameCreditView_maxNum, 7200);
        mStartAngle = typedArray.getInt(R.styleable.SesameCreditView_startAngle, 90);
        currentNum = typedArray.getInt(R.styleable.SesameCreditView_currentNum, 7200);
        secondNum = currentNum;
        int backgroundColor = typedArray.getColor(R.styleable.SesameCreditView_scvBackground, ContextCompat.getColor(context, R.color.colorPrimary));
        mPaint_Bg.setColor(backgroundColor);
        typedArray.recycle();

        SweepGradient sweepGradient = new SweepGradient(0, 0, indicatorColorBg, null);
        Matrix matrix_0 = new Matrix();
        matrix_0.setRotate(mStartAngle);
        sweepGradient.setLocalMatrix(matrix_0);
        mPaint.setShader(sweepGradient);

        mSweepGradient = new SweepGradient(0, 0, indicatorColor, null);
        Matrix matrix = new Matrix();
        matrix.setRotate(mStartAngle);
        mSweepGradient.setLocalMatrix(matrix);
        mW = dp2px(mContext, WIDTH);

        //圆环弧度的宽度
        mSweepOutWidth = dp2px(context, 5);
        mPaint.setStrokeWidth(mSweepOutWidth * 0.88f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        RadialGradient shader = new RadialGradient(mWidth / 2, mHeight / 2, Math.min(mWidth / 2, mHeight / 2), mPaint_Bg.getColor(), mPaint_Bg.getColor(), Shader.TileMode.CLAMP);
        mRadius = w / 4;
        mRectF = new RectF(-mRadius - mW, -mRadius - mW, mRadius + mW, mRadius + mW);
        mPaint_Bg.setShader(shader);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            mWidth = dp2px(mContext, 300);
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            mHeight = dp2px(mContext, 400);
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius * 1.5f, mPaint_Bg);
        canvas.translate(mWidth / 2, mHeight / 2);
        drawRound(canvas);//画内外圆弧
//        drawScale(canvas);//画刻度
        drawIndicator(canvas);//画当前进度值
        drawCenterText(canvas);//画中间文字
        canvas.restore();
    }

    private void drawCenterText(Canvas canvas) {
        canvas.save();
        mPaint_3.setStyle(Paint.Style.FILL);
        mPaint_3.setTextSize((float) (mRadius / 1.5));
        mPaint_3.setColor(0xffffffff);
        String text = getTime();
        canvas.drawText(text, -mPaint_3.measureText(text) / 2, 8, mPaint_3);
        mPaint_3.setTextSize(mRadius / 5);
        mPaint_3.setFakeBoldText(false);
        canvas.restore();
    }

    private String getTime() {
        int min = currentNum / 60;
        int sec = currentNum % 60;
        if (sec >= 10) {
            return min + ":" + sec;
        } else {
            return min + ":0" + sec;
        }
    }

    private void drawIndicator(Canvas canvas) {
        canvas.save();
        int sweep;
//        if (currentNum <= mMaxNum) {
//            sweep = (int) ((float) currentNum / (float) mMaxNum * mSweepAngle);
//        } else {
        sweep = 360 * currentNum / mMaxNum;
//        }
        if (sweep < 0) {
            sweep = 0;
        }
        mPaint_1.setStrokeWidth(mSweepOutWidth);
        mPaint_1.setShader(mSweepGradient);
        canvas.drawArc(mRectF, mStartAngle, sweep, false, mPaint_1);

        mIndicatorX = (float) ((mRadius + mW) * Math.cos(Math.toRadians(mStartAngle + sweep)));
        mIndicatorY = (float) ((mRadius + mW) * Math.sin(Math.toRadians(mStartAngle + sweep)));
        canvas.drawCircle(mIndicatorX, mIndicatorY, dp2px(mContext, 6), mPaint_2);
        canvas.restore();
    }

    private void drawRound(Canvas canvas) {
        canvas.save();
        canvas.drawArc(mRectF, mStartAngle, 360 * secondNum / mMaxNum, false, mPaint);
        canvas.restore();
    }

    private boolean canMove = false;//能否拖动进度条(点击范围在圆点内置为true，UP的时候释放)

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();
            float widX = x - mWidth / 2;//表示点到圆心的横向长度
            float widY = y - mHeight / 2;//表示点到圆心的竖向长度

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    double touchRadius = Math.sqrt(widX * widX + widY * widY);
                    double touchMultiple = touchRadius / (mRadius * 1.5);//表示点到圆心的距离是半径距离的倍数
//            Log.e("blz", "x=" + x + ",y=" + y + ",radius=" + mRadius * 1.5 + ",touchRadius=" + touchRadius + ",touchMultiple=" + touchMultiple);
//            Log.e("blz", mBgLeft + "," + mBgTop + "," + mBgRight + "," + mBgBottom);
                    if (touchMultiple <= 1.07) {
                        getParent().requestDisallowInterceptTouchEvent(true);
//                        Log.e("blz", "x=" + x + ",inX=" + (mIndicatorX + mWidth / 2) + ",y=" + y + ",inY=" + (mIndicatorY + mHeight / 2) + ",mW=" + mW);
                        if (Math.abs(x - mIndicatorX - mWidth / 2) < mW / 2 && Math.abs(y - mIndicatorY - mHeight / 2) < mW / 2) {
//                            Log.e("blz", "戳到了D");
                            canMove = true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (canMove && isEdit) {
                        double v = Math.atan2(widY, widX) * 180 / Math.PI + 270;
                        if (v > 360) {
                            v -= 360;
                        }
                        setCurrentNum((int) (v / 15) * 300);
//                        Log.e("blz", "atan=" + v);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    canMove = false;
                    break;
            }
        }

        return true;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
