package com.benjen.flowchart

import org.intellij.lang.annotations.Flow

import java.util.ArrayList


/**
 * @Author Benjen Masters
 * @Date 2019/1/3
 */
class FlowPoint {
    internal var a: MutableList<in Shape> = ArrayList()

    public fun sum() {
        //1.
        val start = Circle()
        val startPoint = Point(0, 100)
        start.bottomPoint = startPoint
        a.add(start)


        //3.
        val still = Rectangle()
        val sTopPoint = Point(0, 260)
        val sBottomPoint = Point(0, 320)
        still.topPoint = sTopPoint
        still.bottomPoint = sBottomPoint
        a.add(still)

        //4.
        val jude = Diamond()
        val jTopPoint = Point(0, 370)
        val jLeftPoint = Point(-50, 410)
        val jRightPoint = Point(50, 410)
        val jBottomPoint = Point(0, 450)
        jude.topPoint = jTopPoint
        jude.leftPoint = jLeftPoint
        jude.rightPoint = jRightPoint
        jude.bottomPoint = jBottomPoint
        a.add(jude)

        //5.
        val end = Circle()
        val eTopPoint = Point(0, 500)
        val eLeftPoint = Point(-50, 550)
        end.topPoint = eTopPoint
        end.leftPoint = eLeftPoint
        val fJLeftPoint = Point(-50, 410)
        end.fromPoint = fJLeftPoint
        a.add(end)

        //2.
        val again = Rectangle()
        val aTopPoint = Point(0, 150)
        val aRightPoint = Point(50, 180)
        val aBottomPoint = Point(0, 210)
        val fJRightPoint = Point(50, 410)
//        val fJLeftPoint2 = Point(-50, 410)
        again.topPoint = aTopPoint
        again.rightPoint = aRightPoint
        again.bottomPoint = aBottomPoint
        again.fromPoint = fJRightPoint
        a.add(again)
    }
}

internal open class Shape {
    var leftPoint: Point? = null
    var rightPoint: Point? = null
    var topPoint: Point? = null
    var bottomPoint: Point? = null
    var fromPoint: Point? = null
}

internal class Circle : Shape()

internal class Rectangle : Shape()

internal class Diamond : Shape()

internal class Point(var dx: Int, var y: Int) {
    var x: Int = 0
    private val dy: Int = 0

    companion object {
        val CENTER = -1
    }
}
