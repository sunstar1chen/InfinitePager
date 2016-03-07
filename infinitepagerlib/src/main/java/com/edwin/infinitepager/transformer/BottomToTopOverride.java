package com.edwin.infinitepager.transformer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.view.View;

import com.edwin.infinitepager.InfinitePager;

/**
 * Created by chen xue yu  on 2016/1/26.
 * @author 陈学玉
 */
public class BottomToTopOverride extends AbsTransformer {

    public BottomToTopOverride() {
        super();
    }

    public BottomToTopOverride(int duration) {
        super(duration);
    }

    @Override
    public void doTransform(View current, Rect currentSrcRect,
                            View next, Rect nextSrcRect, float dx, float dy, boolean topLeft) {
        next.setTranslationY(dy + nextSrcRect.top + nextSrcRect.height());
    }

    @Override
    public Animator doForwardAnimation(
            final InfinitePager pager, View current, Rect currentSrcRect,
            View next, Rect nextSrcRect, boolean topleft) {
        ObjectAnimator bottomToTop = ObjectAnimator.ofFloat(next, View.Y, nextSrcRect.top);
        bottomToTop.setDuration(animDuration);
        bottomToTop.setInterpolator(interpolator);
        bottomToTop.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pager.scrollToNext();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return bottomToTop;
    }

    @Override
    public Animator doBacwardAnimation(View current, Rect currentSrcRect, final View next, Rect nextSrcRect, boolean topLeft) {
        ObjectAnimator bttb = ObjectAnimator.ofFloat(next, View.Y, nextSrcRect.top + nextSrcRect.height());
        bttb.setDuration(animDuration);
        bttb.setInterpolator(interpolator);
        return bttb;
    }
}
