package com.bulingzhuang.dlc.views.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.bulingzhuang.dlc.R;
import com.bulingzhuang.dlc.views.ui.widget.RenderView;

public class WaveView extends RenderView {

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setStrokeWidth(dp2px(context,2.5f));
    }

    /*绘图*/

    private final Paint paint = new Paint();

    private int regionStartColor = getResources().getColor(R.color.regionStartColor);
    private int regionEndColor = getResources().getColor(R.color.regionEndColor);

    {
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
    }

    private final Path firstPath = new Path();
    private final Path secondPath = new Path();

    /**
     * 采样点的数量，越高越精细，
     * 但高于一定限度后人眼察觉不出。
     */
    private static final int SAMPLING_SIZE = 128;
    /**
     * 采样点的X
     */
    private float[] samplingX;
    /**
     * 采样点位置均匀映射到[-2,2]的X
     */
    private float[] mapX;

    /**
     * 画布宽高
     */
    private int width;
    /**
     * 画布中心的高度
     */
    private int centerHeight;
    /**
     * 振幅
     */
    private int amplitude;

    /**
     * 波峰和两条路径交叉点的记录，包括起点和终点，用于绘制渐变。
     * 通过日志可知其数量范围为7~9个，故这里size取9。
     * <p>
     * 每个元素都是一个float[2]，用于保存xy值
     */
    private final float[][] crestAndCrossPints = new float[9][];

    {//直接分配内存
        for (int i = 0; i < 9; i++) {
            crestAndCrossPints[i] = new float[2];
        }
    }

    @Override
    protected void onRender(Canvas canvas, long millisPassed) {
        if (samplingX == null) {//首次初始化
            //赋值基本参数
            width = canvas.getWidth();
            int height = canvas.getHeight();
            centerHeight = height >> 1;
            amplitude = width / 10;//振幅为宽度的1/10

            //初始化采样点和映射
            samplingX = new float[SAMPLING_SIZE + 1];//因为包括起点和终点所以需要+1个位置
            mapX = new float[SAMPLING_SIZE + 1];//同上
            float gap = width / (float) SAMPLING_SIZE;//确定采样点之间的间距
            float x;
            for (int i = 0; i <= SAMPLING_SIZE; i++) {
                x = i * gap;
                samplingX[i] = x;
                mapX[i] = (x / (float) width) * 4 - 2;//将x映射到[-2,2]的区间上
            }
        }

        canvas.drawColor(Color.WHITE);


        //重置所有path并移动到起点
        firstPath.rewind();
        secondPath.rewind();
        firstPath.moveTo(0, centerHeight);
        secondPath.moveTo(0, centerHeight);

        //当前时间的偏移量，通过该偏移量使得每次绘图都向右偏移，让画面动起来
        //如果希望速度快一点，可以调小分母
        float offset = millisPassed / 2333F;

        //提前申明各种临时参数
        float x;
        float[] xy;

        //波形函数的值，包括上一点，当前点和下一点
        float lastV, curV = 0, nextV = (float) (amplitude * calcValue(mapX[0], offset));
        //波形函数的绝对值，用于筛选波峰和交错点
        float absLastV, absCurV, absNextV;
        //上一个筛选出的点是波峰还是交错点
        boolean lastIsCrest = false;
        //筛选出的波峰和交叉点的数量，包括起点和终点
        int crestAndCrossCount = 0;

        //遍历所有采样点
        for (int i = 0; i <= SAMPLING_SIZE; i++) {
            //计算采样点的位置
            x = samplingX[i];
            lastV = curV;
            curV = nextV;
            //提前算出下一采样点的值
            nextV = i < SAMPLING_SIZE ? (float) (amplitude * calcValue(mapX[i + 1], offset)) : 0;

            //连接路径
            firstPath.lineTo(x, centerHeight + curV);
            secondPath.lineTo(x, centerHeight - curV);

            //记录极值点
            absLastV = Math.abs(lastV);
            absCurV = Math.abs(curV);
            absNextV = Math.abs(nextV);
            if (i == 0 || i == SAMPLING_SIZE/*起点终点*/ || (lastIsCrest && absCurV < absLastV && absCurV < absNextV)/*上一个点为波峰，且该点是极小值点*/) {
                xy = crestAndCrossPints[crestAndCrossCount++];
                xy[0] = x;
                xy[1] = 0;
                lastIsCrest = false;
            } else if (!lastIsCrest && absCurV > absLastV && absCurV > absNextV) {/*上一点是交叉点，且该点极大值*/
                xy = crestAndCrossPints[crestAndCrossCount++];
                xy[0] = x;
                xy[1] = curV;
                lastIsCrest = true;
            }
        }
        //连接所有路径到终点
        firstPath.lineTo(width, centerHeight);
        secondPath.lineTo(width, centerHeight);

        //绘制上弦线
        paint.setColor(regionStartColor);
        canvas.drawPath(firstPath, paint);

        //绘制下弦线
        paint.setColor(regionEndColor);
        canvas.drawPath(secondPath, paint);
    }

    /**
     * 计算波形函数中x对应的y值
     *
     * @param mapX   换算到[-2,2]之间的x值
     * @param offset 偏移量
     * @return
     */
    private double calcValue(float mapX, float offset) {
        int keyX = (int) (mapX * 1000);
        offset %= 2;
        double sinFunc = Math.sin(0.75 * Math.PI * mapX - offset * Math.PI);
        double recessionFunc;
        if (recessionFuncs.indexOfKey(keyX) >= 0) {
            recessionFunc = recessionFuncs.get(keyX);
        } else {
            recessionFunc = Math.pow(4 / (4 + Math.pow(mapX, 4)), 2.5);
            recessionFuncs.put(keyX, recessionFunc);
        }
        return sinFunc * recessionFunc;
    }

    SparseArray<Double> recessionFuncs = new SparseArray<>();

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
