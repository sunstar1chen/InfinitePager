package com.edwin.infinitepager.transformer;

import android.animation.Animator;
import android.graphics.Rect;
import android.view.View;

import com.edwin.infinitepager.InfinitePager;

/**
 * Created by chen xue yu on 2016/1/28.
 * @author 陈学玉
 */
public class VerticalScroll extends AbsTransformer {
    private TopToBottomScroll ttbs;
    private BottomToTopScroll btts;

    public VerticalScroll() {
        this(DEFAULT_ANIMATION_DURATION);
    }

    public VerticalScroll(int duration) {
        super(duration);
        ttbs = new TopToBottomScroll(animDuration);
        btts = new BottomToTopScroll(animDuration);
    }

    @Override
    public void doTransform(View current, Rect currentSrcRect, View next, Rect nextSrcRect, float dx, float dy, boolean topLeft) {
        if (topLeft) {
            ttbs.doTransform(current, currentSrcRect, next, nextSrcRect, dx, dy, topLeft);
        } else {
            btts.doTransform(current, currentSrcRect, next, nextSrcRect, dx, dy, topLeft);
        }
    }

    @Override
    public Animator doForwardAnimation(InfinitePager pager, View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topLeft) {
        if (topLeft) {
            return ttbs.doForwardAnimation(pager, current, currentSrcRect, next, nextSrcRect, topLeft);
        }
        return btts.doForwardAnimation(pager, current, currentSrcRect, next, nextSrcRect, topLeft);
    }

    @Override
    public Animator doBacwardAnimation(View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topLeft) {
        if (topLeft) {
            return ttbs.doBacwardAnimation(current, currentSrcRect, next, nextSrcRect, topLeft);
        }
        return btts.doBacwardAnimation(current, currentSrcRect, next, nextSrcRect, topLeft);
    }
}