package com.benjen.flowchart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @Author Benjen April
 * @Date 2019/1/3
 */

public class FlexibleFlowChartView extends View {

    public FlexibleFlowChartView(Context context) {
        super(context);
    }

    public FlexibleFlowChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexibleFlowChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    //设置流程图的DSL转换后的数据bean
    public void setFlowDataBean(FlowDataBean flowDataBean) {
        this.flowDataBean = flowDataBean;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int leftXLoc = getWidth() / 2;
        //画开始节点
        drawStartPoint(canvas, leftXLoc, getResources().getInteger(R.integer.rectangle_width) / 2);
        for (Object whichShape :
                flowPoint.a) {

        }
    }

    private void drawStartPoint(Canvas canvas, int leftXLoc, int radius) {
        paint.setColor(Color.parseColor("#666666"));
        canvas.drawCircle(leftXLoc, radius, radius, paint);
    }

    //流程图的DSL转换后的数据bean
    private FlowDataBean flowDataBean;
    public FlowPoint flowPoint = new FlowPoint();

    private int mColor = Color.BLACK;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mColor = a.getColor(R.styleable.CircleView_circle_color, Color.BLACK);
        a.recycle();

        paint.setColor(mColor);
    }

}
