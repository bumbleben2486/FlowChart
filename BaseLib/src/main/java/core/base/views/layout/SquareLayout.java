package core.base.views.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareLayout extends LinearLayout {

	public SquareLayout(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

		init();

	}

	private void init() {
		//isInEditMode()方法为保证view可视图化
		if (isInEditMode()) {

		}
	}

	public SquareLayout(Context context, AttributeSet attrs) {

		super(context, attrs);

		init();
	}

	public SquareLayout(Context context) {

		super(context);

		init();
	}

	@Override

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

		// 高度和宽度一样

		heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

}