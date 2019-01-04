package com.benjen.flowchart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * @Author Benjen April
 * @Date 2019/1/3
 */

class FlexibleFlowChartView : View {

    //流程图的DSL转换后的数据bean
    private var flowDataBean: FlowDataBean? = null
    var flowPoint = FlowPoint()

    private var mColor = Color.BLACK
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : super(context) {
        flowPoint.sum()
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        flowPoint.sum()
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleView)
        mColor = a.getColor(R.styleable.CircleView_circle_color, Color.BLACK)
        a.recycle()
        init(context, attrs)
    }

    //设置流程图的DSL转换后的数据bean
    fun setFlowDataBean(flowDataBean: FlowDataBean) {
        this.flowDataBean = flowDataBean
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val leftXLoc = width / 2
        val rWidth = resources.getInteger(R.integer.rectangle_width)
        val rHeight = resources.getInteger(R.integer.rectangle_height)
        val lineLength = resources.getInteger(R.integer.line_length)
        val lineLandscape = resources.getInteger(R.integer.line_landscape)
        //画开始节点
        for (whichShape in flowPoint.a) {
            initP()
            when (whichShape) {
                is Circle -> {
                    paint.color = mColor
                    if (whichShape.topPoint == null) {
                        drawStartEndPoint(
                            canvas,
                            leftXLoc,
                            0,
                            rWidth / 2,
                            resources.getInteger(R.integer.rectangle_width) / 2
                        )
                    } else {
                        drawStartEndPoint(
                            canvas,
                            leftXLoc,
                            0,
                            whichShape.topPoint!!.y + resources.getInteger(R.integer.rectangle_width) / 2,
                            resources.getInteger(R.integer.rectangle_width) / 2
                        )
//                        drawTopologyLine(whichShape, leftXLoc, lineLandscape, rHeight, canvas)
                    }
                }
                is Rectangle -> {
                    paint.color = mColor
                    val rect = Rect(
                        leftXLoc - rWidth / 2, whichShape.topPoint!!.y, leftXLoc + rWidth / 2,
                        whichShape.topPoint!!.y + rHeight
                    )
                    canvas.drawRect(rect, paint)
                    drawTopologyLine(whichShape, leftXLoc, lineLandscape, rHeight, canvas)

                }
                is Diamond -> {
                    paint.color = mColor
//                    paint.style = Paint.Style.STROKE
//                    paint.strokeWidth = 3f
                    val path = Path();
                    path.moveTo(leftXLoc.toFloat(), whichShape.topPoint!!.y.toFloat())
                    path.lineTo(
                        leftXLoc.toFloat() + whichShape.rightPoint!!.dx,
                        whichShape.rightPoint!!.y.toFloat()
                    )
                    path.lineTo(leftXLoc.toFloat(), whichShape.bottomPoint!!.y.toFloat())
                    path.lineTo(
                        leftXLoc.toFloat() + whichShape.leftPoint!!.dx,
                        whichShape.leftPoint!!.y.toFloat()
                    )
                    path.close()
                    canvas.drawPath(path, paint)
                }
            }
            val point = (whichShape as Shape).bottomPoint
            if (point != null) {
                drawLine(canvas, leftXLoc, point.y, lineLength)
            }
        }
    }

    //这里是画指向线的方法, 出bug了
    private fun drawTopologyLine(
        whichShape: Shape,
        leftXLoc: Int,
        lineLandscape: Int,
        rHeight: Int,
        canvas: Canvas
    ) {
        if (whichShape.fromPoint != null) {
            paint.strokeWidth = 3.0F
            paint.style = Paint.Style.STROKE
            val rect = RectF(
                //左边的点, 利用dx的正负未知, 也可以画出在左右两边的矩形
                (leftXLoc + whichShape.fromPoint!!.dx - lineLandscape).toFloat(),
                //上边的点, 想利用矩形一定有上节点, 来做到通过上节点找到这个点
                (whichShape.topPoint!!.y + rHeight / 2).toFloat(),
                //右边的点, 利用dx的正负未知, 也可以画出在左右两边的矩形
                (leftXLoc + whichShape.fromPoint!!.dx + lineLandscape).toFloat(),
                whichShape.fromPoint!!.y.toFloat()
            )
            val leftPointLine: Int
            //这里就没法通过dx正负未知来画出两边的裁剪保留区域
            if (whichShape.fromPoint!!.dx > 0) {
                leftPointLine = whichShape.fromPoint!!.dx
            } else {
                leftPointLine = whichShape.fromPoint!!.dx - lineLandscape
            }
            val clipRect = Rect(
                //保留线以右的区域左点
//                leftXLoc + 50,
                leftXLoc + leftPointLine,
                whichShape.topPoint!!.y + rHeight / 2,
                //保留线以右的区域右点
//                leftXLoc + 50 + 20,
                leftXLoc + leftPointLine + lineLandscape,
                whichShape.fromPoint!!.y
            )
            //下面这个方法放在上面,就连这个矩形都画不出
            //如果不设置圆角矩形, 那想实现线的矩形就连擦除都失效了
            canvas.drawRoundRect(rect, 10F, 10F, paint)

            //这个API相当于PS里画一块区域, 然后以后出现的描绘, 如果是区域以外的, 就不生效
            canvas.clipRect(clipRect)
        }
    }


    private fun drawStartEndPoint(
        canvas: Canvas,
        leftXLoc: Int,
        dx: Int,
        y: Int,
        radius: Int
    ) {
        paint.color = Color.parseColor("#666666")
        canvas.drawCircle(leftXLoc.toFloat() + dx, y.toFloat(), radius.toFloat(), paint)
    }

    private fun drawLine(canvas: Canvas, x: Int, y: Int, length: Int) {
        canvas.drawLine(
            x.toFloat(),
            y.toFloat(),
            x.toFloat(),
            (y + length).toFloat(),
            paint
        )
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        paint.color = mColor
    }

    private fun initP() {
        paint.style = Paint.Style.FILL
        paint.color = mColor
    }

}
