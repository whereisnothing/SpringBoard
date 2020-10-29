package com.chenxu.springboard;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("deprecation")
public class SpringBoardPage extends AbsoluteLayout implements OnClickListener {

    private int width;
    private int height;
    public static final int ITEM_WIDTH = 500;
    public static final int ITEM_HEIGHT = 300;
    private static final float SPRING_BOARD_PAGE_VELOCITY_X_THRESHOLD = 800;
    private List<Integer> imageIdList;
    private int columnNumber;
    private int rowNumber;
    private List<CircleImageView> imageViewList;
    private List<LayoutParams> layoutParamList;
    private int horizontalOffset;
    private boolean hasInitialized = false;
    private SpringBoardListener listener;
    private float touchSlop;
    private float downX;
    private float downY;
    private VelocityTracker velocityTracker;
    private int beginIndex;

    public SpringBoardPage(Context context, List<Integer> imageIdList, int beginIndex, SpringBoardListener listener) {
        super(context);
        this.imageIdList = imageIdList;
        this.beginIndex = beginIndex;
        this.listener = listener;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public SpringBoardPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public SpringBoardPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            int col = i % columnNumber;
//            int row = i / columnNumber;
//            int x = (col + 1) * horizontalOffset + col * ITEM_WIDTH;
//            int y = row * ITEM_HEIGHT;
//            CircleImageView civ = (CircleImageView)getChildAt(i);
//            civ.layout(x, y, ITEM_WIDTH, ITEM_HEIGHT);
//            LogUtil.i("chenxu", "onLayout index:"+i+" xy:"+x+" "+y);
//        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//		LogUtil.i("chenxu", "onSizeChanged:w:"+w+" h:"+h);
        if (w != 0 && h != 0) {
            width = w;
            height = h;
            columnNumber = width / ITEM_WIDTH;
            rowNumber = (imageIdList.size() + columnNumber - 1) / columnNumber;
            int maxRowNumber = height / ITEM_HEIGHT;
            rowNumber = Math.min(rowNumber, maxRowNumber);
            horizontalOffset = (width - columnNumber * ITEM_WIDTH)
                    / (columnNumber + 1);
            imageViewList = new ArrayList<CircleImageView>();
            layoutParamList = new ArrayList<LayoutParams>();
            removeAllViews();
            for (int i = 0; i < imageIdList.size(); i++) {
                int imageId = imageIdList.get(i);
                CircleImageView circleImageView = new CircleImageView(
                        getContext(), imageId, ITEM_WIDTH * 2 / 3,
                        ITEM_HEIGHT * 2 / 3, beginIndex + i);
                int col = i % columnNumber;
                int row = i / columnNumber;
                int x = (col + 1) * horizontalOffset + col * ITEM_WIDTH;
                int y = row * ITEM_HEIGHT;
                LayoutParams layoutParams = new LayoutParams(ITEM_WIDTH,
                        ITEM_HEIGHT, x, y);
                circleImageView.setLayoutParams(layoutParams);
//				LogUtil.i("chenxu", "circle image view's x/y:"+x+" "+y);

                imageViewList.add(circleImageView);
                LayoutParams constantLayoutParams = new LayoutParams(ITEM_WIDTH, ITEM_HEIGHT, x, y);
                layoutParamList.add(constantLayoutParams);

                addView(circleImageView);
            }
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//    	// TODO Auto-generated method stub
//    	switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//        case MotionEvent.ACTION_MOVE:
//        case MotionEvent.ACTION_UP:
//            System.out.println("SpringBoardPage.onTouchEvent MotionEventACTION_DOWN .ACTION_MOVE ACTION_UP");
//            ViewParent parent = getParent();
//                SpringBoardContainer container = null;
//                if (parent instanceof SpringBoardContainer) {
//                    container=(SpringBoardContainer)parent;
//                } else {
//
//                }
//                if (container!=null) {
//                    container.onTouchEvent(event);
//                } else {
//
//                }
//                break;
//		default:
//			break;
//		}
//    	return super.onTouchEvent(event);
//    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        // TODO Auto-generated method stub
//        float x = ev.getX();
//        float y=ev.getY();
//        initVelocityTracker(ev);
//        switch (ev.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                downX=x;
//                downY=y;
//                return false;
//            case MotionEvent.ACTION_MOVE:
//                System.out.println("SpringBoardPage.onInterceptTouchEvent MotionEvent.ACTION_MOVE");
//                float xOffsetAbs=Math.abs(x-downX);
//                float yOffsetAbs=Math.abs(y-downY);
//                float xVelocity = getXVelocity();
//                SpringBoardContainer container = (SpringBoardContainer) getParent();
//                if (xOffsetAbs>touchSlop&&xOffsetAbs>yOffsetAbs&&Math.abs(xVelocity)>=SPRING_BOARD_PAGE_VELOCITY_X_THRESHOLD){
//                    container.requestDisallowInterceptTouchEvent(false);
////                	ev.setAction(MotionEvent.ACTION_MOVE);
////                	resetLayout();
//                    return true;
//                } else {
//                    container.requestDisallowInterceptTouchEvent(true);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                recycleVelocityTracker();
//                break;
////            	resetLayout();
////            	return true;
//            default:
//                break;
//        }
//        return false;
//    }

    public void resetLayout() {
        for (int i = 0; i < imageViewList.size(); i++) {
            CircleImageView civ = (CircleImageView) imageViewList.get(i);
            int index = civ.getIndex();
            LayoutParams layoutParams = (LayoutParams) civ.getLayoutParams();
            int row = i / columnNumber;
            int col = i % columnNumber;
            layoutParams.x = (col + 1) * horizontalOffset + col * ITEM_WIDTH;
            layoutParams.y = row * ITEM_HEIGHT;
            civ.setLayoutParams(layoutParams);
        }
    }

    public void initVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }

    public float getXVelocity() {
        velocityTracker.computeCurrentVelocity(1000);
        return velocityTracker.getXVelocity();
    }

    public void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    public ImageView getImageView(int imageId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId);
        ImageView imageView = new ImageView(getContext());
        LayoutParams imageViewLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0, 0);
        imageViewLayoutParams.x = 0;
        imageViewLayoutParams.y = 0;
        imageView.setLayoutParams(imageViewLayoutParams);
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ScaleType.CENTER);
        return imageView;
    }

    public class CircleImageView extends ImageView implements OnTouchListener {
        private static final float CIRCLE_IMAGE_VIEW_VELOCITY_X_THRESHOLD = 200;
        private int constantIndex;
        private int imageId;
        private int imageWidth;
        private int imageHeight;
        private int index;
        private float initialX;
        private float initialY;
        private float lastX;
        private float lastY;
        private float originalRawX;
        private float originalRawY;
        private int imageViewOriginalX;
        private int imageViewOriginalY;
        private float lastRawX;
        private float lastRawY;
        private VelocityTracker circleImageViewVelocityTracker;
        private float onTouchLastDownX;
        private float onTouchLastDownY;

        public CircleImageView(Context context, int imageId, int imageWidth,
                               int imageHeight, int index) {
            super(context);
            this.imageId = imageId;
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
            this.index = index;
            this.constantIndex = index;

            setPadding(18, 18, 18, 18);
            Bitmap bitmap = getCircleBitmap();
            setImageBitmap(bitmap);
            setScaleType(ScaleType.FIT_XY);

            setOnClickListener(SpringBoardPage.this);
            setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            initVelocityTracker(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onTouchLastDownX = event.getX();
                    onTouchLastDownY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE: {
                    float velocityX = getXVelocity();
                    if (velocityX > CIRCLE_IMAGE_VIEW_VELOCITY_X_THRESHOLD) {
                        ViewParent parent = getParent().getParent();
                        SpringBoardContainer container = null;
                        if (parent instanceof SpringBoardContainer) {
                            container = (SpringBoardContainer) parent;
                            container.requestDisallowInterceptTouchEvent(false);
                            return true;
                        } else {
                            container.requestDisallowInterceptTouchEvent(true);
                            return false;
                        }

                    } else if (velocityX < -CIRCLE_IMAGE_VIEW_VELOCITY_X_THRESHOLD) {
                        ViewParent parent = getParent().getParent();
                        SpringBoardContainer container = null;
                        if (parent instanceof SpringBoardContainer) {
                            container = (SpringBoardContainer) parent;
                            container.requestDisallowInterceptTouchEvent(false);
                            return true;
                        } else {
                            container.requestDisallowInterceptTouchEvent(true);
                            return false;
                        }

                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    float velocityX = getXVelocity();
                    if (velocityX > CIRCLE_IMAGE_VIEW_VELOCITY_X_THRESHOLD) {
                        ViewParent parent = getParent().getParent();
                        SpringBoardContainer container = null;
                        if (parent instanceof SpringBoardContainer) {
                            container = (SpringBoardContainer) parent;
                            container.requestDisallowInterceptTouchEvent(false);
                            container.scrollToPage(container.currentPage - 1);
                            return true;
                        } else {
                            container.requestDisallowInterceptTouchEvent(true);
                            return false;
                        }

                    } else if (velocityX < -CIRCLE_IMAGE_VIEW_VELOCITY_X_THRESHOLD) {
                        ViewParent parent = getParent().getParent();
                        SpringBoardContainer container = null;
                        if (parent instanceof SpringBoardContainer) {
                            container = (SpringBoardContainer) parent;
                            container.requestDisallowInterceptTouchEvent(false);
                            container.scrollToPage(container.currentPage + 1);
                            return true;
                        } else {
                            container.requestDisallowInterceptTouchEvent(true);
                            return false;
                        }

                    }
                    recycleVelocityTracker();
                    break;
                }
                default:
                    break;
            }
            return false;
        }

        public void initVelocityTracker(MotionEvent event) {
            if (circleImageViewVelocityTracker == null) {
                circleImageViewVelocityTracker = VelocityTracker.obtain();
            }
            circleImageViewVelocityTracker.addMovement(event);
        }

        public float getXVelocity() {
            circleImageViewVelocityTracker.computeCurrentVelocity(1000);
            return circleImageViewVelocityTracker.getXVelocity();
        }

        public void recycleVelocityTracker() {
            if (circleImageViewVelocityTracker != null) {
                circleImageViewVelocityTracker.recycle();
                circleImageViewVelocityTracker = null;
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            float left = getLeft();
            float top = getTop();
            float xRelativeToParent = x + left;
            float yRelativeToParent = y + top;
            initVelocityTracker(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = x;
                    initialY = y;
                    lastX = x;
                    lastY = y;
                    originalRawX = rawX;
                    originalRawY = rawY;
                    lastRawX = rawX;
                    lastRawY = rawY;
                    LayoutParams originalLayoutParams = (LayoutParams) getLayoutParams();
                    imageViewOriginalX = originalLayoutParams.x;
                    imageViewOriginalY = originalLayoutParams.y;
                    break;
                case MotionEvent.ACTION_MOVE:

//				float xOffset = x - lastX;
//				float yOffset = y - lastY;
//				float xOffset = rawX-originalRawX;
//				float yOffset = rawY-originalRawY;
                    float xOffset = rawX - lastRawX;
                    float yOffset = rawY - lastRawY;
                    LayoutParams layoutParams = (LayoutParams) getLayoutParams();
                    LayoutParams newLayoutParams = new LayoutParams(layoutParams.width, layoutParams.height, layoutParams.x, layoutParams.y);
//				layoutParams.x =(int) (layoutParams.x+xOffset);
//				layoutParams.y =(int) (layoutParams.y+yOffset);
                    newLayoutParams.x += (int) xOffset;
                    newLayoutParams.y += (int) yOffset;
//				setLayoutParams(layoutParams);
                    setLayoutParams(newLayoutParams);
//				requestLayout();
//				invalidate();
//				LogUtil.i("chenxu", "action move:x:"+x+" y:"+y);
//				LogUtil.i("chenxu", "action move:lastX:"+lastX+" lastY:"+lastY);
//				LogUtil.i("chenxu", "action move:xOffset:"+xOffset+" yOffset:"+yOffset);
//				lastX = x;
//				lastY = y;
//				float xVelocity = getXVelocity();
//				if (Math.abs(xVelocity)>=CIRCLE_IMAGE_VIEW_VELOCITY_X_THRESHOLD) {
//					event.setAction(MotionEvent.ACTION_CANCEL);
//					return false;
//				} else {
//
//				}

                    lastRawX = rawX;
                    lastRawY = rawY;
                    break;
                case MotionEvent.ACTION_UP:

                    if (Math.abs(rawX - originalRawX) <= 3 && Math.abs(rawY - originalRawY) <= 3) {
                        this.performClick();
                    } else {
                        adjustChildren(xRelativeToParent, yRelativeToParent);

                        int dstIndex = 0;
                        int xIndex = (int) (xRelativeToParent / (horizontalOffset + ITEM_WIDTH));
                        int yIndex = (int) (yRelativeToParent / ITEM_HEIGHT);
                        LogUtil.i("chenxu", "raw xIndex:" + xIndex + " yIndex:" + yIndex);
                        xIndex = Math.min(columnNumber - 1, Math.max(0, xIndex));
                        yIndex = Math.min((imageIdList.size() + columnNumber - 1)
                                / columnNumber - 1, Math.max(0, yIndex));
                        dstIndex = yIndex * columnNumber + xIndex;
                        LogUtil.i("chenxu", "actionup/cancel calculated xIndex:" + xIndex + " yIndex:" + yIndex + " dstIndex:" + dstIndex);
                        dstIndex = Math.min(imageIdList.size() - 1, Math.max(0, dstIndex));
                        LogUtil.i("chenxu", "actionup/cancel adjusted dstIndex:" + dstIndex);
                        LayoutParams dstLayoutParams = layoutParamList.get(dstIndex);
                        LayoutParams newDstLayoutParams = new LayoutParams(dstLayoutParams.width, dstLayoutParams.height, dstLayoutParams.x, dstLayoutParams.y);
//					LayoutParams newDstLayoutParams = (LayoutParams) getLayoutParams();
                        copyValuesToLayoutParams(newDstLayoutParams, dstLayoutParams);
//					Collections.swap(imageViewList, index, dstIndex);
//					imageViewList.set(dstIndex, this);
                        imageViewList.remove(index - beginIndex);
                        imageViewList.add(dstIndex, this);
                        this.index = dstIndex + beginIndex;
                        setLayoutParams(newDstLayoutParams);
                        bringToFront();
//					dumpAllImageId();
//					invalidate();

//					LogUtil.i("chenxu", "child view count:"+SpringBoardPage.this.getChildCount());

                    }
                    recycleVelocityTracker();
                    break;
                default:
                    break;
            }
            return true;
        }

        public void dumpAllImageId() {
            for (int i = 0; i < imageViewList.size(); i++) {
                CircleImageView civ = imageViewList.get(i);
//				LogUtil.i("chenxu", "index:"+i+" imageId:"+Integer.toHexString(civ.imageId));
            }
        }

        public void adjustChildren(float x, float y) {
            int dstIndex = 0;
            int xIndex = (int) (x / (horizontalOffset + ITEM_WIDTH));
            int yIndex = (int) (y / ITEM_HEIGHT);
            xIndex = Math.min(columnNumber - 1, Math.max(0, xIndex));
            yIndex = Math.min((imageIdList.size() + columnNumber - 1)
                    / columnNumber - 1, Math.max(0, yIndex));
            dstIndex = yIndex * columnNumber + xIndex;
            dstIndex = Math.min(dstIndex, imageIdList.size() - 1);
            dstIndex = Math.max(0, dstIndex);
            dstIndex += beginIndex;
            if (dstIndex != index) {
                if (dstIndex < index) {
                    for (int i = index - 1; i >= dstIndex; i--) {
                        final CircleImageView circleImageView = imageViewList
                                .get(i - beginIndex);
                        LayoutParams startLayoutParams = layoutParamList
                                .get(circleImageView.index - beginIndex);
                        final LayoutParams endLayoutParams = layoutParamList
                                .get(circleImageView.index - beginIndex + 1);
                        ValueAnimator animator = ValueAnimator.ofObject(
                                new LayoutParamEvaluator(), startLayoutParams,
                                endLayoutParams);
                        animator.addUpdateListener(new AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(
                                    ValueAnimator animation) {
                                LayoutParams layoutParams = (LayoutParams) animation
                                        .getAnimatedValue();
                                circleImageView.setLayoutParams(layoutParams);
//								requestLayout();
                            }
                        });
                        animator.addListener(new AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                // TODO Auto-generated method stub
                                animation.cancel();
                                LayoutParams endParams = new LayoutParams(0, 0, 0, 0);
                                copyValuesToLayoutParams(endParams, endLayoutParams);
                                circleImageView.setLayoutParams(endParams);
                                circleImageView.index += 1;
                                bringToFront();
//								requestLayout();
//								invalidate();
//								SpringBoardPage.this.invalidate();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                // TODO Auto-generated method stub

                            }
                        });
                        animator.setDuration(500);
                        animator.start();
                    }
                } else {
                    dstIndex -= beginIndex;
                    if (dstIndex >= imageViewList.size() - 1 && index - beginIndex == imageViewList.size() - 1) {
                        dstIndex = imageViewList.size() - 1;
                        return;
                    }
                    dstIndex += beginIndex;
                    for (int i = index + 1; i <= dstIndex; i++) {
                        final CircleImageView circleImageView = imageViewList
                                .get(i - beginIndex);
                        LayoutParams startLayoutParams = layoutParamList
                                .get(circleImageView.index - beginIndex);
                        int tempIndex = circleImageView.index - beginIndex - 1;
                        if (tempIndex < 0 || tempIndex > layoutParamList.size() - 1) {
                            LogUtil.i("chenxu", "invalid index:" + tempIndex);
                        }
                        final LayoutParams endLayoutParams = layoutParamList
                                .get(circleImageView.index - beginIndex - 1);
                        ValueAnimator animator = ValueAnimator.ofObject(
                                new LayoutParamEvaluator(), startLayoutParams,
                                endLayoutParams);
                        animator.addUpdateListener(new AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(
                                    ValueAnimator animation) {
                                LayoutParams layoutParams = (LayoutParams) animation
                                        .getAnimatedValue();
                                circleImageView.setLayoutParams(layoutParams);
//								requestLayout();
                            }
                        });
                        animator.addListener(new AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                // TODO Auto-generated method stub
                                animation.cancel();
                                LayoutParams dstLayoutParams = new LayoutParams(0, 0, 0, 0);
                                copyValuesToLayoutParams(dstLayoutParams, endLayoutParams);
                                circleImageView.setLayoutParams(dstLayoutParams);
                                circleImageView.index -= 1;
                                bringToFront();
//								requestLayout();
//								invalidate();
//								SpringBoardPage.this.invalidate();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                // TODO Auto-generated method stub

                            }
                        });
                        animator.setDuration(500);
                        animator.start();
                    }

                }

//				SpringBoardPage.this.requestLayout();
//				SpringBoardPage.this.invalidate();
            }
        }

        public void copyValuesToLayoutParams(LayoutParams dst, LayoutParams src) {
            dst.width = src.width;
            dst.height = src.height;
            dst.x = src.x;
            dst.y = src.y;
        }


        public class LayoutParamEvaluator implements TypeEvaluator {

            @Override
            public Object evaluate(float fraction, Object startValue,
                                   Object endValue) {
                LayoutParams startLayoutParams = (LayoutParams) startValue;
                LayoutParams endLayoutParams = (LayoutParams) endValue;
                float x = startLayoutParams.x
                        + fraction
                        * (endLayoutParams.x - startLayoutParams.x);
                float y = startLayoutParams.y
                        + fraction
                        * (endLayoutParams.y - startLayoutParams.y);
                LayoutParams result = new LayoutParams(startLayoutParams.width,
                        startLayoutParams.height, (int) x, (int) y);
                return result;
            }

        }

        public Bitmap getCircleBitmap() {
            Bitmap originalBitmap = BitmapFactory.decodeResource(
                    getResources(), imageId);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap,
                    imageWidth, imageHeight, true);
            int diameter = Math.min(imageWidth, imageHeight);
            Bitmap resultBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
                    Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Style.FILL);
            canvas.drawCircle(imageWidth / 2, imageHeight / 2, diameter / 2,
                    paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(scaledBitmap, 0, 0, paint);
            paint.setXfermode(null);
            return resultBitmap;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public interface SpringBoardListener {
        public void springBoardDidClick(int position);
    }

    public SpringBoardListener getListener() {
        return listener;
    }

    public void setListener(SpringBoardListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v instanceof CircleImageView) {
            CircleImageView circleImageView = (CircleImageView) v;
            if (listener != null) {
                listener.springBoardDidClick(circleImageView.constantIndex);
            }
        }
    }

}