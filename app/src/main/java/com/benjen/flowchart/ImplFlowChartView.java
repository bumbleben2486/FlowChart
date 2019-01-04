package com.benjen.flowchart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import core.base.utils.ABFileUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import static com.benjen.flowchart.Appconstant.START_CIRCLE;

/**
 * @Author Benjen Masters
 * @Date 2019/1/4
 */
public class ImplFlowChartView extends BaseFlowChartView {

    private FlowDataBean flowDataBean;

    public ImplFlowChartView(Context context) {
        super(context);
    }

    public ImplFlowChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("CheckResult")
    public ImplFlowChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        flowDataBean = new FlowDataBean();
        try {
            String json = ABFileUtil.readFileFromAssets(getContext(), "flow_chart.json");
            Gson gson = new Gson();
            flowDataBean = gson.fromJson(json, FlowDataBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        Collections.sort(flowDataBean.getNodePositions(), (o1, o2) -> Integer.valueOf(o1.getY()).compareTo(o2.getY()));
        Observable observable = Observable.defer(() -> Observable.fromArray(flowDataBean.getNodePositions()));
        observable.filter((Predicate<FlowDataBean.NodePositionsBean>) nodePositionsBean ->
//                nodePositionsBean.getNodeKey().contains(START_CIRCLE))
                nodePositionsBean != null)
                .subscribe();
//                .subscribe(new Consumer<List<FlowDataBean.NodePositionsBean>>() {
//                               @Override
//                               public void accept(List<FlowDataBean.NodePositionsBean> nodePositionsBeans) throws Exception {
//                               }
//                           }
//                );

        Observable.create((ObservableEmitter<FlowDataBean.NodePositionsBean> emitter) -> {
            for (FlowDataBean.NodePositionsBean nodePosition :
                    flowDataBean.getNodePositions()) {
                emitter.onNext(nodePosition);
            }
        }).subscribe(nodePosition -> {
//            if(nodePosition.getNodeKey().contains("end")){
//            }
            Log.d("helloTAG", nodePosition.toString());

        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final List<FlowDataBean.NodesBean> nodesBeans = flowDataBean.getNodes();
        int leftXLoc = getWidth() / 2;
        int rWidth = getResources().getInteger(R.integer.rectangle_width);
        int rHeight = getResources().getInteger(R.integer.rectangle_height);
/*        for (FlowDataBean.NodesBean node :
                nodesBeans) {
            switch (node.getType()) {
                case START_CIRCLE:
                    drawCirclePoint(canvas, leftXLoc, 0, rWidth / 2, rWidth / 2);
                    break;
                case END_CIRCLE:
                    drawCirclePoint(canvas, leftXLoc, 0, rWidth / 2, rWidth / 2);
                    break;
                case RECTANGLE:
                    break;
                case CONDITION_CHART:
                    break;
                default:
                    break;
            }
        }*/


//2----------------------
/*        Observable.fromArray(nodesBeans)
                .flatMap(new Function<List<FlowDataBean.NodesBean>, ObservableSource<List<FlowDataBean.NodesBean>>>() {
                    @Override
                    public ObservableSource<List<FlowDataBean.NodesBean>> apply(List<FlowDataBean.NodesBean> nodesBeans) throws Exception {
                        return Observable.fromArray(nodesBeans);
                    }
                })
                .flatMap(new Function<List<FlowDataBean.NodesBean>, ObservableSource<List<FlowDataBean.NodesBean>>>() {
                    @Override
                    public ObservableSource<List<FlowDataBean.NodesBean>> apply(List<FlowDataBean.NodesBean> nodesBeans) throws Exception {
                        return Observable.fromArray(nodesBeans);
                    }
                })
                .flatMap(new Function<List<FlowDataBean.NodesBean>, ObservableSource<List<FlowDataBean.NodesBean>>>() {
                    @Override
                    public ObservableSource<List<FlowDataBean.NodesBean>> apply(List<FlowDataBean.NodesBean> nodesBeans) throws Exception {
                        return null;
                    }
                })
                .flatMap(new Function<List<FlowDataBean.NodesBean>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(final List<FlowDataBean.NodesBean> nodesBeans) throws Exception {

                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                emitter.onNext(nodesBeans.toString());
                                emitter.onNext(nodesBeans.toString());
                            }
                        });
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d("helloTAG", s);
                    }

                });*/

//3--------------------------
/*        Observable.create(new ObservableOnSubscribe<FlowDataBean.NodesBean>() {
            @Override
            public void subscribe(ObservableEmitter<FlowDataBean.NodesBean> emitter) throws Exception {
                for (FlowDataBean.NodesBean nodesBean :
                        nodesBeans) {
                    emitter.onNext(nodesBean);
                }
            }
        }).flatMap(new Function<FlowDataBean.NodesBean, ObservableSource<FlowDataBean.NodesBean>>() {
            @Override
            public ObservableSource<FlowDataBean.NodesBean> apply(final FlowDataBean.NodesBean nodesBean) throws Exception {
                return Observable.create(new ObservableOnSubscribe<FlowDataBean.NodesBean>() {
                    @Override
                    public void subscribe(ObservableEmitter<FlowDataBean.NodesBean> emitter) throws Exception {
                        emitter.onNext(nodesBean);
                        emitter.onNext(nodesBean);
                }
                });
            }
        }).subscribe(new Consumer<FlowDataBean.NodesBean>() {
            @Override
            public void accept(FlowDataBean.NodesBean nodesBean) throws Exception {
                Log.d("helloTAG", nodesBean.getType());
            }

        });*/

    }

    private void drawCirclePoint(Canvas canvas, int leftXLoc, int dx, int y, int radius) {
        paint.setColor(Color.parseColor("#666666"));
        canvas.drawCircle(leftXLoc + dx, y, radius, paint);
    }
}
