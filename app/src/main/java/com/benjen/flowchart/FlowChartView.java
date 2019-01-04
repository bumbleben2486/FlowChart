package com.benjen.flowchart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @Author Benjen Masters
 * @Date 2019/1/2
 */
public class FlowChartView extends View {


    public FlowChartView(Context context) {
        this(context, null);
    }


    public FlowChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mColor = a.getColor(R.styleable.CircleView_circle_color, Color.BLACK);
        a.recycle();
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int lpoint = width / 2;
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        //画开始节点
        canvas.drawCircle(width / 2, 50, 50, paint);
        //画文字
        String test = "特定节点";
        paint.setStrokeWidth(3);
        paint.setTextSize(24f);
        paint.setColor(Color.RED);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect bounds = new Rect();
//        paint.getTextBounds(test, 0, test.length(), bounds);
        canvas.drawText(test, lpoint, 50, paint);

        paint.setColor(Color.parseColor("#666666"));


        //画结束节点
        canvas.drawCircle(lpoint, 450, 50, paint);

        //画矩形
        canvas.drawRect(width / 2 - 70, 150, width / 2 + 70, 200, paint);

        //画菱形
        Path path = new Path();
        path.moveTo(width / 2, 250);
        path.lineTo(width / 2 + 70, 300);
        path.lineTo(lpoint, 350);
        path.lineTo(lpoint - 70, 300);
        path.close();
        canvas.drawPath(path, paint);

        //画线
        //直线
        canvas.drawLine(width / 2, 100, width / 2, 150, paint);
        canvas.drawLine(lpoint, 200, lpoint, 250, paint);
        canvas.drawLine(lpoint, 350, lpoint, 400, paint);
        //曲线
        RectF rect = new RectF(lpoint - 70 - 25, 300, lpoint - 70 + 25, 450);
        paint.setStrokeWidth(3.0f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.clipRect(lpoint - 70 - 25, 300, lpoint - 70, 450);
        canvas.drawRoundRect(rect, 10, 10, paint);


    }

    private int mColor = Color.BLACK;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private void init() {
        paint.setColor(mColor);
    }
}
