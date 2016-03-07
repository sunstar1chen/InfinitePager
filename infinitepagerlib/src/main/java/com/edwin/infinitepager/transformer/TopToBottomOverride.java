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
 * Created by chen xue yu on 2016/1/26.
 * @author 陈学玉
 */
public class TopToBottomOverride extends AbsTransformer {
    public TopToBottomOverride() {

    }

    public TopToBottomOverride(int animDuration) {
        super(animDuration);
    }

    @Override
    public void doTransform(View current, Rect currentSrcRect, View next, Rect nextSrcRect, float dx, float dy, boolean topLeft) {
        next.setTranslationY(dy + nextSrcRect.top - nextSrcRect.height());
    }

    @Override
    public Animator doForwardAnimation(final InfinitePager pager, View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topleft) {
        Interpolator interpolator = new DecelerateInterpolator();
        ObjectAnimator topTopBottom = ObjectAnimator.ofFloat(next, View.Y, nextSrcRect.top);
        topTopBottom.setDuration(animDuration);
        topTopBottom.setInterpolator(interpolator);
        topTopBottom.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pager.scrollToPrevious();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return topTopBottom;
    }

    @Override
    public Animator doBacwardAnimation(View current, Rect currentSrcRect, final View next, Rect nextSrcRect, boolean topleft) {
        Interpolator polator = new DecelerateInterpolator();
        ObjectAnimator bttb = ObjectAnimator.ofFloat(next, View.Y, nextSrcRect.top - nextSrcRect.height());
        bttb.setDuration(animDuration);
        bttb.setInterpolator(polator);
        return bttb;
    }
}
