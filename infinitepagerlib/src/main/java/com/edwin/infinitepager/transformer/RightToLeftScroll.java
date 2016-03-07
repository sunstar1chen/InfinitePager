package com.edwin.infinitepager.transformer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.edwin.infinitepager.InfinitePager;

/**
 * Created by chen xue yu on 2016/1/28.
 * @author 陈学玉
 */
public class RightToLeftScroll extends RightToLeftOverride {
    public RightToLeftScroll() {
        super();
    }

    public RightToLeftScroll(int duration) {
        super(duration);
    }

    @Override
    public void doTransform(View current, Rect currentSrcRect, View next, Rect nextSrcRect, float dx, float dy, boolean topLeft) {
        super.doTransform(current, currentSrcRect, next, nextSrcRect, dx, dy, topLeft);
        current.setTranslationX(currentSrcRect.left + dx);
    }

    @Override
    public Animator doForwardAnimation(final InfinitePager pager, View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topleft) {
        Interpolator interpolator = new DecelerateInterpolator();
        AnimatorSet leftToRight = new AnimatorSet();
        leftToRight.setDuration(animDuration);
        leftToRight.setInterpolator(interpolator);

        ObjectAnimator nex = ObjectAnimator.ofFloat(next, View.X, nextSrcRect.left);
        ObjectAnimator cur = ObjectAnimator.ofFloat(current, View.X, currentSrcRect.left - currentSrcRect.width());
        AnimatorSet total = new AnimatorSet();
        leftToRight.playTogether(nex, cur);
        total.play(leftToRight);
        total.addListener(new Animator.AnimatorListener() {
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
        return total;
    }

    @Override
    public Animator doBacwardAnimation(View current, Rect currentSrcRect, final View next, Rect nextSrcRect, boolean topLeft) {
        Interpolator interpolator = new DecelerateInterpolator();
        AnimatorSet leftToRight = new AnimatorSet();
        leftToRight.setDuration(animDuration);
        leftToRight.setInterpolator(interpolator);

        ObjectAnimator pre = ObjectAnimator.ofFloat(next, View.X, nextSrcRect.right);
        ObjectAnimator cur = ObjectAnimator.ofFloat(current, View.X, currentSrcRect.left);
        leftToRight.playTogether(pre, cur);
        return leftToRight;
    }
}