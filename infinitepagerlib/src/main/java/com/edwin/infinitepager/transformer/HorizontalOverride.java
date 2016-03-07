package com.edwin.infinitepager.transformer;

import android.animation.Animator;
import android.graphics.Rect;
import android.view.View;

import com.edwin.infinitepager.InfinitePager;

/**
 * Created by chen xue yu  on 2016/1/27.
 * @author 陈学玉
 */
public class HorizontalOverride extends AbsTransformer {
    private LeftToRightOverride ltro;
    private RightToLeftOverride rtlo;

    public HorizontalOverride() {
        this(DEFAULT_ANIMATION_DURATION);
    }

    public HorizontalOverride(int duration) {
        super(duration);
        ltro = new LeftToRightOverride(animDuration);
        rtlo = new RightToLeftOverride(animDuration);
    }


    @Override
    public void doTransform(View current, Rect currentSrcRect, View next, Rect nextSrcRect, float dx, float dy, boolean topLeft) {
        if (topLeft) {
            ltro.doTransform(current, currentSrcRect, next, nextSrcRect, dx, dy, topLeft);
        } else {
            rtlo.doTransform(current, currentSrcRect, next, nextSrcRect, dx, dy, topLeft);
        }
    }

    @Override
    public Animator doForwardAnimation(InfinitePager pager, View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topLeft) {
        if (topLeft) {
            return ltro.doForwardAnimation(pager, current, currentSrcRect, next, nextSrcRect, topLeft);
        } else {
            return rtlo.doForwardAnimation(pager, current, currentSrcRect, next, nextSrcRect, topLeft);
        }
    }

    @Override
    public Animator doBacwardAnimation(View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topLeft) {
        if (topLeft) {
            return ltro.doBacwardAnimation(current, currentSrcRect, next, nextSrcRect, topLeft);
        } else {
            return rtlo.doBacwardAnimation(current, currentSrcRect, next, nextSrcRect, topLeft);
        }
    }
}
