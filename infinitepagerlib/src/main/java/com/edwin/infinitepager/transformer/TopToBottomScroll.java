package com.edwin.infinitepager.transformer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.view.View;

import com.edwin.infinitepager.InfinitePager;

/**
 * Created by chen xue yu on 2016/1/28.
 * @author 陈学玉
 */
public class TopToBottomScroll extends TopToBottomOverride {
    public TopToBottomScroll() {
        super();
    }

    public TopToBottomScroll(int duration) {
        super(duration);
    }

    @Override
    public void doTransform(View current, Rect currentSrcRect,
                            View next, Rect nextSrcRect, float dx, float dy, boolean topLeft) {
        super.doTransform(current, currentSrcRect, next, nextSrcRect, dx, dy, topLeft);
        current.setTranslationY(dy + currentSrcRect.top);
    }

    @Override
    public Animator doForwardAnimation(
            final InfinitePager pager, View current, Rect currentSrcRect,
            View next, Rect nextSrcRect, boolean topleft) {
        AnimatorSet total = new AnimatorSet();
        AnimatorSet forward = new AnimatorSet();
        ObjectAnimator nex = ObjectAnimator.ofFloat(next, View.Y, nextSrcRect.top);
        ObjectAnimator cur = ObjectAnimator.ofFloat(current, View.Y, currentSrcRect.top + currentSrcRect.height());
        forward.playTogether(cur, nex);
        forward.setDuration(animDuration);
        forward.setInterpolator(interpolator);
        forward.addListener(new Animator.AnimatorListener() {
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
        ObjectAnimator curReset = ObjectAnimator.ofFloat(current, View.Y, currentSrcRect.top);
        curReset.setDuration(0);
        total.play(curReset).after(forward);
        return total;
    }

    @Override
    public Animator doBacwardAnimation(View current, Rect currentSrcRect, final View next, Rect nextSrcRect, boolean topLeft) {
        AnimatorSet back = new AnimatorSet();
        ObjectAnimator bttb = ObjectAnimator.ofFloat(next, View.Y, nextSrcRect.top - nextSrcRect.height());
        ObjectAnimator cb = ObjectAnimator.ofFloat(current, View.Y, currentSrcRect.top);
        back.playTogether(bttb, cb);
        back.setDuration(animDuration);
        back.setInterpolator(interpolator);
        return back;
    }
}
