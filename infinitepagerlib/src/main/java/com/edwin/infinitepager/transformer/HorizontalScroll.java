package com.edwin.infinitepager.transformer;

import android.animation.Animator;
import android.graphics.Rect;
import android.view.View;

import com.edwin.infinitepager.InfinitePager;

/**
 * Created by chen xue yu  on 2016/1/28.
 * @author 陈学玉
 */
public class HorizontalScroll extends AbsTransformer {
    private LeftToRightScroll ltrs;
    private RightToLeftScroll rtls;

    public HorizontalScroll() {
        this(DEFAULT_ANIMATION_DURATION);
    }

    public HorizontalScroll(int duration) {
        super(duration);
        ltrs = new LeftToRightScroll(animDuration);
        rtls = new RightToLeftScroll(animDuration);
    }

    @Override
    public void doTransform(View current, Rect currentSrcRect, View next, Rect nextSrcRect, float dx, float dy, boolean topLeft) {
        if (topLeft) {
            ltrs.doTransform(current, currentSrcRect, next, nextSrcRect, dx, dy, topLeft);
        } else {
            rtls.doTransform(current, currentSrcRect, next, nextSrcRect, dx, dy, topLeft);
        }
    }

    @Override
    public Animator doForwardAnimation(InfinitePager pager, View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topLeft) {
        if (topLeft) {
            return ltrs.doForwardAnimation(pager, current, currentSrcRect, next, nextSrcRect, topLeft);
        }
        return rtls.doForwardAnimation(pager, current, currentSrcRect, next, nextSrcRect, topLeft);
    }

    @Override
    public Animator doBacwardAnimation(View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topLeft) {
        if (topLeft) {
            return ltrs.doBacwardAnimation(current, currentSrcRect, next, nextSrcRect, topLeft);
        }
        return rtls.doBacwardAnimation(current, currentSrcRect, next, nextSrcRect, topLeft);
    }
}
