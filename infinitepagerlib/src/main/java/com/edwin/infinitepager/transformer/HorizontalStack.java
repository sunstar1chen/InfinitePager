package com.edwin.infinitepager.transformer;

import android.animation.Animator;
import android.graphics.Rect;
import android.view.View;

import com.edwin.infinitepager.InfinitePager;

/**
 * Created by chen xue yu  on 2016/1/28.
 * @author 陈学玉
 */
public class HorizontalStack extends AbsTransformer {
    private RightToLeftStack rtls;
    private LeftToRightStack ltrs;

    public HorizontalStack() {
        this(DEFAULT_ANIMATION_DURATION);
    }

    public HorizontalStack(int duration) {
        super(duration);
        rtls = new RightToLeftStack(animDuration);
        ltrs = new LeftToRightStack(animDuration);
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
    public Animator doForwardAnimation(final InfinitePager pager, View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topLeft) {
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
