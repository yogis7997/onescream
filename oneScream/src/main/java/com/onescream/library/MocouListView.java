package com.onescream.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class MocouListView extends ListView {

	// true if we can scroll (not locked)
	// false if we cannot scroll (locked)
	private boolean mScrollable = true;

	public MocouListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MocouListView(Context context) {
		super(context);
	}

	public MocouListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setScrollingEnabled(boolean enabled) {
		mScrollable = enabled;
	}

	public boolean isScrollable() {
		return mScrollable;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// if we can scroll pass the event to the superclass
			if (mScrollable)
				return super.onTouchEvent(ev);
			// only continue to handle the touch event if scrolling enabled
			return mScrollable; // mScrollable is always false at this point
		default:
			return super.onTouchEvent(ev);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// Don't do anything with intercepted touch events if
		// we are not scrollable
		if (!mScrollable)
			return false;
		else
			return super.onInterceptTouchEvent(ev);
	}

	// ScrollView와 gridView를 함께 쓰는 경우에는 두개의 scroll이 중첩되므로 onMeasure()를 재정해주어야
	// 한다.
	// ListView의 경우도 마찬가지다.
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}