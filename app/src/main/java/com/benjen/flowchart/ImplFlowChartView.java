package com.benjen.flowchart;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import core.base.utils.ABFileUtil;
import io.reactivex.internal.operators.single.SingleDelayWithCompletable;

import java.util.List;

import static com.benjen.flowchart.Appconstant.*;

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

    public ImplFlowChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        flowDataBean = new FlowDataBean(ABFileUtil.readFileFromAssets(getContext(), "flow_chart.json"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<FlowDataBean.NodesBean> nodesBeans = flowDataBean.getNodes();
        for (FlowDataBean.NodesBean node :
                nodesBeans) {
            switch (node.getType()) {
                case START_CIRCLE:
                    break;
                case END_CIRCLE:
                    break;
                case RECTANGLE:
                    break;
                case CONDITION_CHART:
                    break;
                default:
                    break;
            }
        }
    }

}
