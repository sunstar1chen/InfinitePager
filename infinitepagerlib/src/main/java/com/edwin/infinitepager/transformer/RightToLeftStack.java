package com.edwin.infinitepager.transformer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.view.View;

import com.edwin.infinitepager.InfinitePager;
import com.edwin.infinitepager.transformer.AbsTransformer;

/**
 * Created by chen xue yu on 2016/1/28.
 * @author 陈学玉
 */
public class RightToLeftStack extends AbsTransformer {
    public RightToLeftStack() {
    }

    public RightToLeftStack(int duration) {
        super(duration);
    }

    @Override
    public void doTransform(View current, Rect currentSrcRect, View next, Rect nextSrcRect, float dx, float dy, boolean topLeft) {
        current.setTranslationX(dx + currentSrcRect.left);
        float scale = 0.5f - dx / current.getWidth() / 2;
        next.setScaleX(scale);
        next.setScaleY(scale);
    }

    @Override
    public Animator doForwardAnimation(final InfinitePager pager, View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topLeft) {
        AnimatorSet scaleAndMove = new AnimatorSet();
        ObjectAnimator nexScalX = ObjectAnimator.ofFloat(next, View.SCALE_X, 1);
        ObjectAnimator nexScalY = ObjectAnimator.ofFloat(next, View.SCALE_Y, 1);
        ObjectAnimator cur = ObjectAnimator.ofFloat(current, View.X, currentSrcRect.left - currentSrcRect.right);
        scaleAndMove.playTogether(nexScalX, nexScalY, cur);
        scaleAndMove.setDuration(animDuration);
        scaleAndMove.setInterpolator(interpolator);
        scaleAndMove.addListener(new Animator.AnimatorListener() {
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
        return scaleAndMove;
    }

    @Override
    public Animator doBacwardAnimation(View current, Rect currentSrcRect, View next, Rect nextSrcRect, boolean topLeft) {
        AnimatorSet scaleAndMove = new AnimatorSet();
        ObjectAnimator nexScalX = ObjectAnimator.ofFloat(next, View.SCALE_X, 0.3f);
        ObjectAnimator nexScalY = ObjectAnimator.ofFloat(next, View.SCALE_Y, 0.3f);
        ObjectAnimator cur = ObjectAnimator.ofFloat(current, View.X, currentSrcRect.left);
        scaleAndMove.playTogether(nexScalX, nexScalY, cur);
        scaleAndMove.setDuration(animDuration);
        scaleAndMove.setInterpolator(interpolator);
        return scaleAndMove;
    }
}