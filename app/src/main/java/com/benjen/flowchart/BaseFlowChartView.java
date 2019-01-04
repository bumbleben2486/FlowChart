package com.benjen.flowchart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @Author Benjen Masters
 * @Date 2019/1/4
 */
public class BaseFlowChartView extends View {

    public BaseFlowChartView(Context context) {
        super(context);
    }

    public BaseFlowChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseFlowChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }


    protected int mColor = Color.BLACK;
    protected Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mColor = a.getColor(R.styleable.CircleView_circle_color, Color.BLACK);
        a.recycle();

        paint.setColor(mColor);
    }
}
