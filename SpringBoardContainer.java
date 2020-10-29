package com.chenxu.springboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpringBoardContainer extends ViewGroup implements
        SpringBoardPage.SpringBoardListener {

	private static final int VELOCITY_THRESHOLD = 300;
	private static final float INTERCEPT_VELOCITY_THRESHOLD = 300;
	private int touchSlop;
	private int width;
	private int height;
	private int columnNumber;
	private int rowNumber;
	private List<Integer> imageIdList;
	private int itemsNumberPerPage;
	private ItemClickListener listener;
	private List<View> viewList;
	private float lastRawX;
	private VelocityTracker velocityTracker;
	public int currentPage;
	private int pageCount;
	private Scroller scroller;
	private boolean isScrollToNext = false;
	private float downX;
	private float downY;
	private Set<PageChangeListener> listeners;

	public SpringBoardContainer(Context context, List<Integer> imageIdList,
			ItemClickListener listener) {
		super(context);
		// TODO Auto-generated constructor stub
		this.imageIdList = imageIdList;
		this.listener = listener;

		viewList = new ArrayList<View>();
		scroller = new Scroller(context);

		currentPage = 0;
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		
		listeners=new HashSet<PageChangeListener>();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int childrenCount = getChildCount();
		for (int i = 0; i < childrenCount; i++) {
			View childView = getChildAt(i);
			childView.layout(i * width, 0, (i + 1) * width, height);
//			LogUtil.i("chenxu",
//					"onLayout childview's size:" + childView.getMeasuredWidth()
//							+ " " + childView.getMeasuredHeight());
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		if (w != 0 && h != 0) {
			width = w;
			height = h;

			columnNumber = width / SpringBoardPage.ITEM_WIDTH;
			rowNumber = height / SpringBoardPage.ITEM_HEIGHT;
			itemsNumberPerPage = columnNumber * rowNumber;

			int remainingNumber = imageIdList.size();
			int beginIndex = 0;
			viewList.clear();
			while (remainingNumber > 0) {
				int count = Math.min(remainingNumber, itemsNumberPerPage);
				List<Integer> sublist = new ArrayList<Integer>();
				for (int i = beginIndex; i < beginIndex + count; i++) {
					int imageId = imageIdList.get(i);
					sublist.add(imageId);
				}
				SpringBoardPage page = new SpringBoardPage(getContext(),
						sublist, beginIndex, this);
				LinearLayout.LayoutParams pageLayoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				page.setLayoutParams(pageLayoutParams);
				viewList.add(page);

				addView(page);
				remainingNumber -= count;
				beginIndex += count;
			}
			pageCount = viewList.size();

		}

	}
	
	public void addListener(PageChangeListener listener) {
		listeners.add(listener);
	}

	public LayoutParams rootLayoutParams = new LayoutParams(
			LayoutParams.MATCH_PARENT,
			LayoutParams.MATCH_PARENT);
	private VelocityTracker interceptVelocityTracker;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		if (!scroller.isFinished()) {
			return true;
		}
		float rawX = event.getRawX();
		initVelocityTracker(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastRawX = rawX;
			return true;
		case MotionEvent.ACTION_MOVE:

            int xOffset = (int) (rawX - lastRawX);
			 scrollBy(-xOffset, 0);
//			moveByXOffset(xOffset);
			lastRawX = rawX;
			// LogUtil.i("chenxu", "currentPage:"+currentPage);
			break;
		case MotionEvent.ACTION_UP:

//			LogUtil.i("chenxu", "multipageScrollLayout's size:"
//					+ getMeasuredWidth() + " " + getMeasuredHeight());
			int childrenCount = getChildCount();
//			for (int i = 0; i < childrenCount; i++) {
//				View childView = getChildAt(i);
//				// childView.layout(i * width, 0, (i + 1) * width, height);
//				LogUtil.i(
//						"chenxu",
//						"action_up childview's size:"
//								+ childView.getMeasuredWidth() + " "
//								+ childView.getMeasuredHeight());
//			}

            for (int i = 0; i < getChildCount(); i++) {
                SpringBoardPage page = (SpringBoardPage)getChildAt(i);
                page.resetLayout();
            }
			int xVelocity = getVelocityX();
			if (xVelocity <= -VELOCITY_THRESHOLD) {
//				scrollToNext();
				if (currentPage<pageCount-1) {
					scrollToPage(currentPage+1);
				}else {
					scrollBack();
				}
			} else if (xVelocity >= VELOCITY_THRESHOLD) {
//				scrollToPrevious();
				if (currentPage>0) {
					scrollToPage(currentPage-1);
				}else {
					scrollBack();
				}
			} else {
				scrollBack();
			}

            recycleVelocityTracker();

            break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		// TODO Auto-generated method stub
//		float x = ev.getX();
//		float y = ev.getY();
//		initInterceptVelocityTracker(ev);
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			downX = x;
//			downY = y;
//			return true;
//		case MotionEvent.ACTION_MOVE:
//			float xOffsetAbs = Math.abs(x - downX);
//			float yOffsetAbs = Math.abs(y - downY);
//			float xVelocity = getInterceptVelocityX();
//			if (xOffsetAbs > touchSlop && xOffsetAbs > yOffsetAbs&&
//					Math.abs(xVelocity)>INTERCEPT_VELOCITY_THRESHOLD) {
//				return true;
//			}
//			break;
//		case MotionEvent.ACTION_UP:
//			recycleInterceptVelocityTracker();
//			break;
//		default:
//			break;
//		}
//		return super.onInterceptTouchEvent(ev);
//	}
	
	public void initInterceptVelocityTracker(MotionEvent event) {
		if (interceptVelocityTracker==null) {
			interceptVelocityTracker=VelocityTracker.obtain();
		}
		interceptVelocityTracker.addMovement(event);
	}
	
	public float getInterceptVelocityX() {
		interceptVelocityTracker.computeCurrentVelocity(1000);
		return interceptVelocityTracker.getXVelocity();
	}
	
	public void recycleInterceptVelocityTracker() {
		if (interceptVelocityTracker!=null) {
			interceptVelocityTracker.recycle();
			interceptVelocityTracker=null;
		}
	}

	public SpringBoardContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SpringBoardContainer(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		// TODO Auto-generated method stub
//		return super.dispatchTouchEvent(ev);
//	}

	public void moveByXOffset(int xOffset) {
		int scrollX = getScrollX();
		xOffset = -xOffset;
		if (xOffset > 0 && scrollX >= (pageCount - 1) * width) {
			return;
		}
		if (xOffset < 0 && scrollX <= 0) {
			return;
		}
		if (xOffset > 0 && scrollX + xOffset > (pageCount - 1) * width) {
			xOffset = (pageCount - 1) * width - scrollX;
		} else if (xOffset < 0 && scrollX + xOffset < 0) {
			xOffset = -scrollX;
		}
		scrollBy(xOffset, 0);
	}

	public void scrollToNext() {
		if (currentPage < pageCount - 1) {
			int finalScrollX = (currentPage + 1) * width;
			int dx = finalScrollX - getScrollX();
			int duration = (int) ((500.0f / width) * Math.abs(dx));
			isScrollToNext = true;
			scroller.startScroll(getScrollX(), getScrollY(), dx, 0, duration);
			postInvalidate();
			++currentPage;
		} else {
			scrollBack();
		}
	}

	public void scrollToPrevious() {
		if (currentPage > 0) {
			int finalScrollX = (currentPage - 1) * width;
			int dx = finalScrollX - getScrollX();
			int duration = (int) ((500.0f / width) * Math.abs(dx));
			isScrollToNext = false;
			scroller.startScroll(getScrollX(), getScrollY(), dx, 0, duration);
			postInvalidate();
			--currentPage;
		} else {
			scrollBack();
		}
	}

	public void scrollBack() {
		int scrollX = getScrollX();
		int index = (scrollX + width / 2) / width;
//		int leftScrollX = index * width;
//		int rightScrollX = (index + 1) * width;
//		int leftAbs = Math.abs(scrollX - leftScrollX);
//		int rightAbs = Math.abs(rightScrollX - scrollX);
//		int dx = 0;
//		if (leftAbs <= rightAbs) {
//			dx = scrollX - leftScrollX;
//		} else {
//			dx = -(rightScrollX - scrollX);
//		}
//		int duration = (int) ((500.0f / width) * Math.abs(dx));
//		scroller.startScroll(getScrollX(), getScrollY(), dx, 0, duration);
//		postInvalidate();
		scrollToPage(index);
	}
	
	public void scrollToPage(int page) {
		int lastPage = currentPage;
		page=Math.max(0, Math.min(pageCount-1, page));
		if (Math.abs(page-currentPage)==1) {
			if (getScrollX()!=page*width) {
				int delta=page*width-getScrollX();
				scroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta));
				currentPage=page;
				postInvalidate();
			}
		} else {
			currentPage=page;
			scrollTo(page*width, 0);
			postInvalidate();
		}
		
		for (PageChangeListener listener : listeners) {
			listener.pageDidChange(lastPage, page);
		}
	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
		} else {
			// if (isScrollToNext) {
			// ++currentPage;
			// } else {
			// --currentPage;
			// }
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		// if (widthMode != MeasureSpec.EXACTLY) {
		// throw new IllegalStateException(
		// "ScrollLayout only canmCurScreen run at EXACTLY mode!");
		// }
		//
		// final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		// if (heightMode != MeasureSpec.EXACTLY) {
		// throw new IllegalStateException(
		// "ScrollLayout only can run at EXACTLY mode!");
		// }

		// The children are given the same width and height as the scrollLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		// scrollTo(0 * width, 0);
	}

	public LayoutParams matchParentLayoutParams = new LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

	public TextView getTextView(String title) {
		TextView textView = new TextView(getContext());
		LayoutParams textViewLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		textView.setLayoutParams(textViewLayoutParams);
		textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		textView.setText(title);
		textView.setTextSize(22);
		textView.setTextColor(Color.BLACK);
		return textView;
	}

	public ImageView getImageView(int imageId) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inSampleSize = 1;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId,
				options);
		ImageView imageView = new ImageView(getContext());
		LayoutParams imageViewLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		imageView.setLayoutParams(imageViewLayoutParams);
		imageView.setImageBitmap(bitmap);
		imageView.setScaleType(ScaleType.CENTER);
		return imageView;
	}

	public void initVelocityTracker(MotionEvent event) {
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(event);
	}

	public int getVelocityX() {
		velocityTracker.computeCurrentVelocity(1000);
		return (int) velocityTracker.getXVelocity();
	}

	public void recycleVelocityTracker() {
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}

	@Override
	public void springBoardDidClick(int position) {
		// TODO Auto-generated method stub
		if (listener != null) {
			listener.itemDidClick(position);
		}
	}

	public interface ItemClickListener {
		public void itemDidClick(int position);
	}
	
	public interface PageChangeListener {
		public void pageDidChange(int lastPage, int currentPage);
	}
}