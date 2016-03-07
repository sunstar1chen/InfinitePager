package com.edwin.infinitepager.transformer;

import android.animation.Animator;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.edwin.infinitepager.InfinitePager;

/**
 * Created by chen xue yu  on 2016/1/26.
 * @author 陈学玉
 */
public abstract class AbsTransformer {
    public static final int DEFAULT_ANIMATION_DURATION = 300;
    protected final int animDuration;
    protected Interpolator interpolator = new DecelerateInterpolator();

    public void setInterpolator(Interpolator interpolator) {
        if (interpolator == null) {
            interpolator = new DecelerateInterpolator();
        }
        this.interpolator = interpolator;
    }

    protected AbsTransformer() {
        this(DEFAULT_ANIMATION_DURATION);
    }

    protected AbsTransformer(int duration) {
        if (duration < 0) {
            duration = 0;
        }
        animDuration = duration;
    }

    /**

     */

    /**
     * control the current view and the next view transform when user touch on the screen
     *
     * @param current the view is showing
     * @param next    the view will be shown
     * @param dx      the total distance of current touch point and 'touch down point' in X-axis
     * @param dy      the total distance of current touch point and 'touch down point' in Y-axis
     * @param topLeft indicate whether the next view is top-left of current view.
     */
    public abstract void doTransform(View current, Rect currentSrcRect,
                                     View next, Rect nextSrcRect,
                                     float dx, float dy, boolean topLeft);

    /**
     * animate to the next view , method {@link InfinitePager#scrollToNext()}
     * must be called after animation complete and invoke {@link View#setVisibility(int)}
     *
     * @param pager
     * @param current
     * @param currentSrcRect
     * @param next
     * @param nextSrcRect
     * @param topLeft        indicate whether the next view is top-left of current view.
     * @return
     */
    public abstract Animator doForwardAnimation(InfinitePager pager, View current,
                                                Rect currentSrcRect, View next,
                                                Rect nextSrcRect, boolean topLeft);

    /**
     * restore views state
     *
     * @param current
     * @param currentSrcRect
     * @param next
     * @param nextSrcRect
     * @param topLeft        indicate whether the next view is top-left of current view.
     * @return
     */
    public abstract Animator doBacwardAnimation(View current, Rect currentSrcRect,
                                                View next, Rect nextSrcRect, boolean topLeft);
}
