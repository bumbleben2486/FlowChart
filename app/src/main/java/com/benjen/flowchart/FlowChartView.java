package com.benjen.flowchart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.qiniu.android.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import core.base.utils.AssetsUtil;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.util.LinkedArrayList;

/**
 * @Author Benjen April
 * @Date 2019/1/2
 */
public class FlowChartView extends View {


    public FlowChartView(Context context) {
        this(context, null);
    }


    public FlowChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private FlowBean flowBean;

    public FlowChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mColor = a.getColor(R.styleable.CircleView_circle_color, Color.BLACK);
        a.recycle();
        flowBean = new Gson().fromJson(AssetsUtil.readText(context, "proc.json"),
                FlowBean.class);
        init();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int lpoint = width / 2;
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

/*        //画开始节点
        canvas.drawCircle(width / 2, 50, 50, paint);

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
        canvas.drawRoundRect(rect, 10, 10, paint);*/


        Map<String, Node> nodeMaps = new HashMap<>();
        try {
            Flowable.fromIterable(nodes)
                    .collect(new Callable<LinkedList<Node>>() {
                        @Override
                        public LinkedList<Node> call() throws Exception {
                            return new LinkedList<>();
                        }
                    }, new BiConsumer<LinkedList<Node>, Node>() {
                        @Override
                        public void accept(LinkedList<Node> nodes, Node node) throws Exception {
                            if (node.getKey().contains("start")) {
                                node.setDx("0");
                                node.setY("0");
                            } else {
                                node.setDx("0");
                                Node lastNode = nodes.getLast();
                                node.setY(StringUtil.string2Integer(lastNode.getY()) + (lastNode.getKey().contains("zy") ? 100 : 150) + "");
                            }
                            nodes.add(node);
                        }
                    }).subscribe(new Consumer<LinkedList<Node>>() {
                @Override
                public void accept(LinkedList<Node> nodes) throws Exception {
                    Log.d("helloTAG", nodes.toString());
                    for (Node node :
                            nodes) {
                        nodeMaps.put(node.getKey(), node);
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Log.d("helloTAG", throwable.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (FlowBean.NodesBean nodesBean :
                flowBean.getNodes()) {
            Node node = nodeMaps.get(nodesBean.getNodeKey());
            switch (nodesBean.getType()) {
                case "E":
//                    canvas.drawCircle(lpoint + StringUtil.string2Integer(node.getDx()), StringUtil.string2Integer(node.getY() + 50),
//                            50, paint);
                    break;
                case "EG":
                    break;
                case "UT":
                    break;
                case "S":
                    canvas.drawCircle(lpoint + StringUtil.string2Integer(node.getDx()), StringUtil.string2Integer(node.getY() + 50),
                            50, paint);
                    break;
                default:
                    break;

            }
        }


    }

    private int mColor = Color.BLACK;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<Node> nodes;

    private void init() {
        paint.setColor(mColor);
        nodes = new ArrayList<>();
        Node node1 = new Node("test_start", "335", "97");
        Node node2 = new Node("test_kfzy", "335", "225");
        Node node3 = new Node("test_dzzy", "335", "351");
        Node node4 = new Node("test_eg", "335", "497");
        Node node5 = new Node("test_end", "335", "638");
        nodes.add(node1);
        nodes.add(node2);
        nodes.add(node3);
        nodes.add(node4);
        nodes.add(node5);
    }
}

class Node {
    private String key;
    private String dx;
    private String y;

    public Node(String key, String dx, String y) {
        this.key = key;
        this.dx = dx;
        this.y = y;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDx() {
        return dx;
    }

    public void setDx(String dx) {
        this.dx = dx;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Node{" +
                "key='" + key + '\'' +
                ", dx='" + dx + '\'' +
                ", y='" + y + '\'' +
                '}';
    }
}
