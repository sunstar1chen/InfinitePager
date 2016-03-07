package com.edwin.infinitepager;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.edwin.infinitepager.transformer.AbsTransformer;
import com.edwin.infinitepager.transformer.RightToLeftOverride;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * created at 2016/01/26
 *
 * @author 陈学玉
 */
public class InfinitePager extends FrameLayout {

    public enum SlideMode {
        TOP_TO_BOTTOM, BOTTOM_TO_TOP,
        VERTICAL, LEFT_TO_RIGHT, RIGHT_TO_LEFT, HORIZONTAL
    }

    private static final int DEFAULT_FLING_TRIGGER = 400;
    public static final int SLIDE_MODE_BOTTOM_TO_TOP = 0x0000001;
    public static final int SLIDE_MODE_TOP_TO_BOTTOM = 0x0000010;
    public static final int SLIDE_MODE_LEFT_TO_RIGHT = 0x0000100;
    public static final int SLIDE_MODE_RIGHT_TO_LEFT = 0x0001000;
    public static final int CONTAINER_NO_VIEW_POS = -1;


    private RelativeLayout current, topLeft, bottomRight;
    private Rect topLeftPort = new Rect();  // previous view source rect
    private Rect bottomRightPort = new Rect(); // next view source rect
    private Rect currentPort = new Rect(); // current view source rect

    /**
     * root view id map to position of page view adapter
     */
    Map<Integer, Integer> containerPosMap = new HashMap<Integer, Integer>();
    private Context context;
    private LayoutInflater inflater;
    private PageViewAdapter pageAdapter;
    private VelocityTracker touchTracker;
    private View emptyView;
    private volatile float downX, downY;  // the first point user touch down
    private volatile boolean isContainerReversed = false;
    /**
     * true this will be a infinite view pager
     */
    private volatile boolean isLoopMode = true;
    /**
     * the pre-condition of loop
     */
    private volatile int minmumLoopCount;


    /**
     * animator construct by {@link AbsTransformer#doBacwardAnimation(View, Rect, View, Rect, boolean)}
     * or {@link AbsTransformer#doForwardAnimation(InfinitePager, View, Rect, View, Rect, boolean)}
     */
    private Animator animator;  // animator construct by
    protected AbsTransformer transformer = new RightToLeftOverride();

    /**
     * slide mode is one of SLIDE_MODE_BOTTOM_TO_TOP,SLIDE_MODE_TOP_TO_BOTTOM,
     * SLIDE_MODE_TOP_TO_BOTTOM | SLIDE_MODE_BOTTOM_TO_TOP ,
     * SLIDE_MODE_LEFT_TO_RIGHT , SLIDE_MODE_RIGHT_TO_LEFT,
     * SLIDE_MODE_RIGHT_TO_LEFT | SLIDE_MODE_LEFT_TO_RIGHT;
     */
    private int internalSlideMode = SLIDE_MODE_RIGHT_TO_LEFT;
    private SlideMode slideMode;

    /**
     * intercept touch event
     * if child has consumed the event then disable scroll, otherwise enable scroll.
     */
    private volatile boolean isEnableScroll = false;
    private final int[] childLocation = new int[2];
    private final LinkedList<View> childrenQueue = new LinkedList<View>();
    private int touchSlop;

    public InfinitePager(Context context) {
        super(context);
        initView(context);
    }

    public InfinitePager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public InfinitePager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InfinitePager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        setFocusable(true);
        this.context = context;
        inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.pager_root, this);
        setSlideMode(SlideMode.RIGHT_TO_LEFT);
        reset();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public AbsTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(AbsTransformer transformer) {
        this.transformer = transformer;
    }

    public SlideMode getSlideMode() {
        return slideMode;
    }

    public void setSlideMode(SlideMode mode) {
        if (mode == null) {
            mode = SlideMode.RIGHT_TO_LEFT;
        }
        slideMode = mode;
        if (slideMode == SlideMode.RIGHT_TO_LEFT) {
            internalSlideMode = SLIDE_MODE_RIGHT_TO_LEFT;
        } else if (slideMode == SlideMode.LEFT_TO_RIGHT) {
            internalSlideMode = SLIDE_MODE_LEFT_TO_RIGHT;
        } else if (slideMode == SlideMode.HORIZONTAL) {
            internalSlideMode = SLIDE_MODE_LEFT_TO_RIGHT | SLIDE_MODE_RIGHT_TO_LEFT;
        } else if (slideMode == SlideMode.TOP_TO_BOTTOM) {
            internalSlideMode = SLIDE_MODE_TOP_TO_BOTTOM;
        } else if (slideMode == SlideMode.BOTTOM_TO_TOP) {
            internalSlideMode = SLIDE_MODE_BOTTOM_TO_TOP;
        } else if (slideMode == SlideMode.VERTICAL) {
            internalSlideMode = SLIDE_MODE_TOP_TO_BOTTOM | SLIDE_MODE_BOTTOM_TO_TOP;
        }
    }

    public void scrollToNext() {
        setCurrentItem(pageAdapter.getCurrent() + 1);
    }

    public void scrollToPrevious() {
        setCurrentItem(pageAdapter.getCurrent() - 1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        topLeftPort.set(topLeft.getLeft(),
                topLeft.getTop(),
                topLeft.getLeft() + topLeft.getMeasuredWidth(),
                topLeft.getTop() + topLeft.getMeasuredHeight());
        bottomRightPort.set(bottomRight.getLeft(),
                bottomRight.getTop(),
                bottomRight.getLeft() + bottomRight.getMeasuredWidth(),
                bottomRight.getTop() + bottomRight.getMeasuredHeight());
        currentPort.set(current.getLeft(), current.getTop(),
                current.getLeft() + current.getMeasuredWidth(),
                current.getTop() + current.getMeasuredHeight());
    }

    public <T extends PageViewAdapter> void setPageAdapter(T adapter) {
        if (pageAdapter != null) {
            pageAdapter.unregisterObservable(adapterDataObserver);
        }
        reset();
        pageAdapter = adapter;
        if (pageAdapter != null) {
            pageAdapter.registerObservable(adapterDataObserver);
        }
        adapterDataObserver.onChanged();
    }

    private void reset() {
        if (isContainerReversed) {
            current = (RelativeLayout) findViewById(R.id.top_container);
            topLeft = (RelativeLayout) findViewById(R.id.center_container);
            bottomRight = (RelativeLayout) findViewById(R.id.bottom_container);
        } else {
            current = (RelativeLayout) findViewById(R.id.bottom_container);
            topLeft = (RelativeLayout) findViewById(R.id.center_container);
            bottomRight = (RelativeLayout) findViewById(R.id.top_container);
        }
        containerPosMap.clear();
        current.removeAllViews();
        topLeft.removeAllViews();
        bottomRight.removeAllViews();

        current.setScaleX(1);
        current.setScaleY(1);
        topLeft.setScaleX(1);
        topLeft.setScaleY(1);
        bottomRight.setScaleX(1);
        bottomRight.setScaleY(1);

        current.setX(0);
        current.setY(0);
        topLeft.setX(0);
        topLeft.setY(0);
        bottomRight.setX(0);
        bottomRight.setY(0);

        containerPosMap.put(current.getId(), CONTAINER_NO_VIEW_POS);
        containerPosMap.put(bottomRight.getId(), CONTAINER_NO_VIEW_POS);
        containerPosMap.put(topLeft.getId(), CONTAINER_NO_VIEW_POS);
        if (emptyView != null) {
            current.addView(emptyView);
        }
    }

    private void setCurrentItem(int pos) {
        if (pageAdapter != null) {
            pageAdapter.setCurent(pos);
            pageAdapter.notifyDataSetchanged();
        }
    }

    private final DataSetObserver adapterDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            if (pageAdapter != null) {
                int pageLen = pageAdapter.getCount();
                if (pageLen == 0) {   // there are no data in the adapter
                    return;
                }
                int cur = pageAdapter.getCurrent();
                int pre = (cur - 1 + pageLen) % pageLen;
                int next = (cur + 1) % pageLen;
                int mapCur = containerPosMap.get(current.getId());
                containerPosMap.put(topLeft.getId(), pre);
                containerPosMap.put(bottomRight.getId(), next);
                containerPosMap.put(current.getId(), cur);
                initViewPosition();
                if (cur == mapCur) {  // current view in cache
                    if (containerPosMap.get(topLeft.getId()) != pre) {
                        loadViewToRoot(pre, topLeft);
                    }
                    if (containerPosMap.get(bottomRight.getId()) != next) {
                        loadViewToRoot(next, bottomRight);
                    }
                    return;
                } else if (mapCur == pre) {
                    // we are sure that the left top  and bottom right view has created .
                    View tmpBr = bottomRight.getChildAt(0);
                    View tmpCc = current.getChildAt(0);
                    current.removeAllViews();
                    bottomRight.removeAllViews();
                    current.addView(tmpBr);
                    bottomRight.addView(tmpCc);
                } else if (mapCur == next) {
                    View tmpTl = topLeft.getChildAt(0);
                    View tmpCc = current.getChildAt(0);
                    current.removeAllViews();
                    topLeft.removeAllViews();
                    current.addView(tmpTl);
                    topLeft.addView(tmpCc);
                } else {
                    loadViewToRoot(cur, current);
                }
                loadViewToRoot(pre, topLeft);
                loadViewToRoot(next, bottomRight);
            }
        }

        private void loadViewToRoot(int pos, RelativeLayout to) {
            if (pageAdapter != null) {
                View convert = null;
                if (to.getChildCount() > 0) {
                    convert = to.getChildAt(0);
                }
                View tmp = pageAdapter.getView(convert, pos);
                if (convert == null || convert != tmp) {
                    if (convert != null) {
                        Log.d("mmmm", "loadViewToRoot ------> remove views ");
                        to.removeAllViews();
                    }
                    to.addView(tmp, new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                }
            }
        }


        @Override
        public void onInvalidated() {
        }
    };


    private void initViewPosition() {
        topLeft.setVisibility(INVISIBLE);
        bottomRight.setVisibility(INVISIBLE);
        current.setVisibility(VISIBLE);
        bottomRight.setY(bottomRightPort.top);
        bottomRight.setX(bottomRightPort.left);
        topLeft.setX(topLeftPort.left);
        topLeft.setY(topLeftPort.top);
        current.setX(currentPort.left);
        current.setY(currentPort.top);
    }


    public boolean isLoopMode() {
        return isLoopMode;
    }

    /**
     * set true if you want this is a infinite pager
     * It is effective only the adapter's count bigger than
     * the minimum loop count {@link #setMinmumLoopCount(int)}
     *
     * @param isLoopMode
     */
    public void setIsLoopMode(boolean isLoopMode) {
        if (isIntervalLoop) {
            return; // loop mode always true , if interval Loop was opened
        }
        this.isLoopMode = isLoopMode;
    }

    /**
     * The minimum loop count expected
     * There is no loop effect if the adapter's count
     * less than the minimum loop count even though
     * the loop mode {@link #setIsLoopMode(boolean)} set to true
     *
     * @param minmumLoopCount
     */
    public void setMinmumLoopCount(int minmumLoopCount) {
        this.minmumLoopCount = minmumLoopCount;
    }

    public int getMinmumLoopCount() {
        return minmumLoopCount;
    }

    private boolean isLoopEnabled() {
        return pageAdapter != null && pageAdapter.getCount() > minmumLoopCount && isLoopMode;
    }

    private boolean isPagerLastItem() {
        return pageAdapter != null && pageAdapter.getCurrent() == pageAdapter.getCount() - 1;
    }

    private boolean isPagerFirstItem() {
        return pageAdapter != null && pageAdapter.getCurrent() == 0;
    }

    private void visibleTopLeft(boolean visible) {
        if (visible) {
            if (topLeft.getVisibility() != VISIBLE) {
                topLeft.setVisibility(VISIBLE);
            }
            if (bottomRight.getVisibility() != INVISIBLE) {
                bottomRight.setVisibility(INVISIBLE);
            }
        } else {
            if (topLeft.getVisibility() != INVISIBLE) {
                topLeft.setVisibility(INVISIBLE);
            }
            if (bottomRight.getVisibility() != VISIBLE) {
                bottomRight.setVisibility(VISIBLE);
            }
        }
    }

    private void slideBottomToTop(float dx, float dy) {
        if (isLoopEnabled() || (!isLoopEnabled() && !isPagerLastItem())) {
            visibleTopLeft(false);
            transformer.doTransform(current, currentPort, bottomRight, bottomRightPort, dx, dy, false);
        }
    }

    private void slideTopToBottom(float dx, float dy) {
        if (isLoopEnabled() || (!isLoopEnabled() && !isPagerFirstItem())) {
            visibleTopLeft(true);
            transformer.doTransform(current, currentPort, topLeft, topLeftPort, dx, dy, true);
        }
    }

    private void slideLeftToRight(float dx, float dy) {
        if (isLoopEnabled() || (!isLoopEnabled() && !isPagerFirstItem())) {
            visibleTopLeft(true);
            transformer.doTransform(current, currentPort, topLeft, topLeftPort, dx, dy, true);
        }
    }

    private void slideRightToLeft(float dx, float dy) {
        if (isLoopEnabled() || (!isLoopEnabled() && !isPagerLastItem())) {
            visibleTopLeft(false);
            transformer.doTransform(current, currentPort, bottomRight, bottomRightPort, dx, dy, false);
        }
    }

    private void handleMotionEvent(float dx, float dy) {
        if (transformer == null) {
            return;
        }
        if ((internalSlideMode & SLIDE_MODE_BOTTOM_TO_TOP) != 0 && dy < 0) {
            slideBottomToTop(dx, dy);
        }
        if ((internalSlideMode & SLIDE_MODE_TOP_TO_BOTTOM) != 0 && dy > 0) {
            slideTopToBottom(dx, dy);
        }
        if ((internalSlideMode & SLIDE_MODE_LEFT_TO_RIGHT) != 0 && dx > 0) {
            slideLeftToRight(dx, dy);
        }
        if ((internalSlideMode & SLIDE_MODE_RIGHT_TO_LEFT) != 0 && dx < 0) {
            slideRightToLeft(dx, dy);
        }
    }

    private void animBottomToTop(float dy, float vy) {
        // filter out invalid animation request
        if (dy > 0 || isViewInSrcPos(bottomRight)) {
            return;
        }
        animator = null; // reset animator
        if ((isLoopEnabled() || (!isLoopEnabled() && !isPagerLastItem()))
                && (vy < -DEFAULT_FLING_TRIGGER || Math.abs(dy) >= getHeight() / 2)) {
            animator = transformer.doForwardAnimation(this, current,
                    currentPort, bottomRight, bottomRightPort, false);
        } else {
            animator = transformer.doBacwardAnimation(current,
                    currentPort, bottomRight, bottomRightPort, false);
        }
        playAnimation(animator);
    }

    private void animTopToBottom(float dy, float vy) {
        if (dy < 0 || isViewInSrcPos(topLeft)) {
            return;
        }
        animator = null;
        if ((isLoopEnabled() || (!isLoopEnabled() && !isPagerFirstItem()))
                && (vy > DEFAULT_FLING_TRIGGER || dy >= getHeight() / 2)) {
            animator = transformer.doForwardAnimation(this, current,
                    currentPort, topLeft, topLeftPort, true);
        } else {
            animator = transformer.doBacwardAnimation(current,
                    currentPort, topLeft, topLeftPort, true);
        }
        playAnimation(animator);
    }

    private void playAnimation(Animator anim) {
        if (anim != null) {
            anim.start();
        }
    }

    private void animLeftToRight(float dx, float vx) {
        if (dx < 0 || isViewInSrcPos(topLeft)) {
            return;
        }
        animator = null;
        if ((isLoopEnabled() || (!isLoopEnabled() && !isPagerFirstItem()))
                && (vx > DEFAULT_FLING_TRIGGER || Math.abs(dx) >= getWidth() / 2)) {
            animator = transformer.doForwardAnimation(this, current,
                    currentPort, topLeft, topLeftPort, true);
        } else {
            animator = transformer.doBacwardAnimation(current,
                    currentPort, topLeft, topLeftPort, true);
        }
        playAnimation(animator);
    }

    private void animRightToLeft(float dx, float vx) {
        // filter out invalid animation request
        if (dx > 0 || isViewInSrcPos(bottomRight)) {
            return;
        }
        animator = null; // reset animator
        if ((isLoopEnabled() || (!isLoopEnabled() && !isPagerLastItem()))
                && (vx < -DEFAULT_FLING_TRIGGER || Math.abs(dx) >= getWidth() / 2)) {
            animator = transformer.doForwardAnimation(this, current,
                    currentPort, bottomRight, bottomRightPort, false);
        } else {
            animator = transformer.doBacwardAnimation(current,
                    currentPort, bottomRight, bottomRightPort, false);
        }
        playAnimation(animator);
    }

    private void handleFlingEvent(float dx, float dy, float vx, float vy) {
        if (transformer == null) {
            return;
        }
        if ((internalSlideMode & SLIDE_MODE_BOTTOM_TO_TOP) != 0) {
            animBottomToTop(dy, vy);
        }
        if ((internalSlideMode & SLIDE_MODE_TOP_TO_BOTTOM) != 0) {
            animTopToBottom(dy, vy);
        }
        if ((internalSlideMode & SLIDE_MODE_LEFT_TO_RIGHT) != 0) {
            animLeftToRight(dx, vx);
        }
        if ((internalSlideMode & SLIDE_MODE_RIGHT_TO_LEFT) != 0) {
            animRightToLeft(dx, vx);
        }
    }

    private boolean isViewInSrcPos(View v) {
        boolean src = v.getTranslationY() == 0
                && v.getTranslationX() == 0
                && (1 - v.getScaleX() <= 0.00005f)
                && (1 - v.getScaleX() <= 0.00005f);
        return src;
    }

    private boolean isTouchPointConsumed(MotionEvent ev) {
        boolean consumed = false;
        childrenQueue.clear();
        childrenQueue.add(current);
        View tmp;
        while ((tmp = childrenQueue.poll()) != null) {
            consumed = isTouchPointConsumed(tmp, ev);
            if (consumed) {
                break;
            }
            if (tmp instanceof ViewGroup) {
                for (int i = 0, len = ((ViewGroup) tmp).getChildCount(); i < len; i++) {
                    childrenQueue.add(((ViewGroup) tmp).getChildAt(i));
                }
            }
        }
        return consumed;
    }

    private boolean isTouchPointConsumed(View v, MotionEvent ev) {
        boolean consumed = false;
        if (v.isEnabled() && (v.isPressed() || v.isClickable() || v.isLongClickable())) {
            v.getLocationOnScreen(childLocation);
            int x = Math.round(ev.getRawX());
            int y = Math.round(ev.getRawY());
            if (x >= childLocation[0]
                    && x <= childLocation[0] + v.getMeasuredWidth()
                    && y >= childLocation[1]
                    && y <= childLocation[1] + v.getMeasuredHeight()) {
                consumed = true;
            } else {
                consumed = false;
            }
        }
        return consumed;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        if (pageAdapter == null || pageAdapter.getCount() == 0 || !isEnableScroll) {
            return false;
        }
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        if (touchTracker == null) {
            touchTracker = VelocityTracker.obtain();
        }
        float tx = ev.getX();
        float ty = ev.getY();
        touchTracker.addMovement(ev);
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downX = tx;
                downY = ty;
                initViewPosition();
                break;
            case MotionEvent.ACTION_MOVE:
                handleMotionEvent(tx - downX, ty - downY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchTracker.computeCurrentVelocity(1000);
                isEnableScroll = false;
                handleFlingEvent(tx - downX, ty - downY, touchTracker.getXVelocity(), touchTracker.getYVelocity());
                if (isIntervalLoop) {
                    enableIntervalLoop(intervalTime);
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        autoLoopHandler.removeCallbacks(transformTask);
        float tx = ev.getX();
        float ty = ev.getY();
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downX = tx;
                downY = ty;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isIntervalLoop) {
                    enableIntervalLoop(intervalTime);
                }
                break;
        }
        if (isEnableScroll) {
            downX = ev.getX();
            downY = ev.getY();
            initViewPosition();
            return true;
        }
        isEnableScroll = !isTouchPointConsumed(ev)
                //enable scroll in touchable children views
                || ((((internalSlideMode & SLIDE_MODE_BOTTOM_TO_TOP) != 0)
                || ((internalSlideMode & SLIDE_MODE_TOP_TO_BOTTOM) != 0))
                && Math.abs(ty - downY) > touchSlop)
                || ((((internalSlideMode & SLIDE_MODE_LEFT_TO_RIGHT) != 0)
                || ((internalSlideMode & SLIDE_MODE_RIGHT_TO_LEFT) != 0))
                && Math.abs(tx - downX) > touchSlop);
        if (isEnableScroll) {
            ViewParent vp = getParent();
            if (vp != null) {
                vp.requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * true will reverse the current from bottom to top
     *
     * @param reverse
     */
    public void reverseContainer(boolean reverse) {
        if (isContainerReversed == reverse) {
            return;
        }
        isContainerReversed = reverse;
        reset();
        if (pageAdapter != null && pageAdapter.getCount() > 0) {
            pageAdapter.notifyDataSetchanged();
        }
    }

    private volatile boolean preLoopMode, isIntervalLoop;
    private long intervalTime;
    private final Handler autoLoopHandler = new Handler();
    Runnable transformTask = new Runnable() {
        @Override
        public void run() {
            float d = -16, v = -1000;
            handleMotionEvent(d, d);
            handleFlingEvent(d, d, v, v);
            autoLoopHandler.postDelayed(this, intervalTime);
        }
    };

    /**
     * This method will enable internal infinite loop ,
     * until {@link #disableIntervalLoop()} called .
     *
     * @param time
     */
    public void enableIntervalLoop(long time) {
        if (time < AbsTransformer.DEFAULT_ANIMATION_DURATION) {
            time = AbsTransformer.DEFAULT_ANIMATION_DURATION;
        }
        intervalTime = time;
        preLoopMode = isLoopMode();
        isIntervalLoop = true;
        setIsLoopMode(true);
        autoLoopHandler.postDelayed(transformTask, intervalTime);
    }

    public void disableIntervalLoop() {
        if (isIntervalLoop) {
            isIntervalLoop = false;
            setIsLoopMode(preLoopMode);
            autoLoopHandler.removeCallbacks(transformTask);
        }
    }
}

