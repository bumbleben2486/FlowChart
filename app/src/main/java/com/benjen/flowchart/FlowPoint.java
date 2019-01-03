package com.benjen.flowchart;

import org.intellij.lang.annotations.Flow;

import java.util.ArrayList;
import java.util.List;

import static com.benjen.flowchart.Point.CENTER;

/**
 * @Author Benjen Masters
 * @Date 2019/1/3
 */
public class FlowPoint {
    List<? super Shape> a = new ArrayList<>();

    public FlowPoint() {
        sum();
    }

    private void sum() {
        //1.
        Circle start = new Circle();
        Point startPoint = new Point(0, 100);
        start.bottomPoint = startPoint;
        a.add(start);

        //2.
        Rectangle again = new Rectangle();
        Point aTopPoint = new Point(0, 150);
        Point aRightPoint = new Point(50, 180);
        Point aBottomPoint = new Point(0, 210);
        again.topPoint = aTopPoint;
        again.rightPoint = aRightPoint;
        again.bottomPoint = aBottomPoint;
        a.add(again);

        //3.
        Rectangle still = new Rectangle();
        Point sTopPoint = new Point(0, 260);
        Point sBottomPoint = new Point(0, 320);
        still.topPoint = sTopPoint;
        still.bottomPoint = sBottomPoint;
        a.add(still);

        //4.
        Diamond jude = new Diamond();
        Point jTopPoint = new Point(0, 370);
        Point jLeftPoint = new Point(-50, 410);
        Point jRightPoint = new Point(50, 410);
        Point jBottomPoint = new Point(0, 450);
        jude.topPoint = jTopPoint;
        jude.leftPoint = jLeftPoint;
        jude.rightPoint = jRightPoint;
        jude.bottomPoint = jBottomPoint;
        a.add(jude);

        //5.
        Circle end = new Circle();
        Point eTopPoint = new Point(0, 500);
        Point eLeftPoint = new Point(-50, 550);
        end.topPoint = eTopPoint;
        end.leftPoint = eLeftPoint;
        a.add(end);
    }
}

class Shape {
    protected Point leftPoint, rightPoint, topPoint, bottomPoint;
}

class Circle extends Shape {

}

class Rectangle extends Shape {

}

class Diamond extends Shape {

}

class Point {
    public static final int CENTER = -1;
    private int x;
    private int dx;
    private int y;
    private int dy;

    public Point(int dx, int y) {
        this.dx = dx;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }
}
