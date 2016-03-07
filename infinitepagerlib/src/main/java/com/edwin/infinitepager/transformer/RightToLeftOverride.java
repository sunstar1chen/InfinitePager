package com.edwin.infinitepager.transformer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.edwin.infinitepager.InfinitePager;
import com.edwin.infinitepager.transformer.AbsTransformer;

/**
 * Created by chen xue yu on 2016/1/27.
 * @author 陈学玉
 */
public class RightToLeftOverride extends AbsTransformer {

    public RightToLeftOverride() {
        super();
    }

    public RightToLeftOverride(int duration) {
        super(duration);
    }

    @Override
    public void doTransform(View current, Rect currentSrcRect, View next, Rect nextSrcRect, float dx, float dy, boolean topLeft) {
        next.setTranslationX(dx + nextSrcRect.left + nextSrcRect.width());
    }

    @Override
    public Animator doForwardAnimation(final InfinitePager pager, View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topleft) {
        Interpolator interpolator = new DecelerateInterpolator();
        ObjectAnimator bottomToTop = ObjectAnimator.ofFloat(next, View.X, nextSrcRect.left);
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
        Interpolator polator = new DecelerateInterpolator();
        ObjectAnimator bttb = ObjectAnimator.ofFloat(next, View.X, nextSrcRect.left + nextSrcRect.width());
        bttb.setDuration(animDuration);
        bttb.setInterpolator(polator);
        return bttb;
    }
}