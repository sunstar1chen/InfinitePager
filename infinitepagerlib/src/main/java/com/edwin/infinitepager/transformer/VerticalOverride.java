package com.edwin.infinitepager.transformer;

import android.animation.Animator;
import android.graphics.Rect;
import android.view.View;

import com.edwin.infinitepager.InfinitePager;

/**
 * Created by chen xue yu on 2016/1/26.
 * @author 陈学玉
 */
public class VerticalOverride extends AbsTransformer {
    private BottomToTopOverride bottomToTopTransformer;
    private TopToBottomOverride topToBottomTransformer;

    public VerticalOverride() {
        this(DEFAULT_ANIMATION_DURATION);
    }

    public VerticalOverride(int duration) {
        super(duration);
        initTransformaer();
    }

    private void initTransformaer() {
        bottomToTopTransformer = new BottomToTopOverride(animDuration);
        topToBottomTransformer = new TopToBottomOverride(animDuration);
    }

    @Override
    public void doTransform(View current, Rect currentSrcRect, View next, Rect nextSrcRect, float dx, float dy, boolean topLeft) {
        // slide down
        if (topLeft) {
            topToBottomTransformer.doTransform(current, currentSrcRect, next, nextSrcRect, dx, dy, topLeft);
        } else { // slide up
            bottomToTopTransformer.doTransform(current, currentSrcRect, next, nextSrcRect, dx, dy, topLeft);
        }
    }

    @Override
    public Animator doForwardAnimation(final InfinitePager pager, View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topleft) {
        if (topleft) {
            return topToBottomTransformer.doForwardAnimation(pager, current, currentSrcRect, next, nextSrcRect, topleft);
        } else {
            return bottomToTopTransformer.doForwardAnimation(pager, current, currentSrcRect, next, nextSrcRect, topleft);
        }
    }

    @Override
    public Animator doBacwardAnimation(View current, Rect currentSrcRect, final View next, Rect nextSrcRect, boolean topleft) {
        if (topleft) {
            return topToBottomTransformer.doBacwardAnimation(current, currentSrcRect, next, nextSrcRect, topleft);
        } else {
            return bottomToTopTransformer.doBacwardAnimation(current, currentSrcRect, next, nextSrcRect, topleft);
        }
    }
}