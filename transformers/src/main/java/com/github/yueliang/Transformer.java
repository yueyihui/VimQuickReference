/**
 * Copyright (C) 2016 yueyihui

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.yueliang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by c_yuelia on 16-12-12.
 */

public class Transformer {
    public interface AnimationListener {
        public void onStart(int selectedPosition);
    }
    private AnimationListener mAnimationListener;
    private Activity mActivity;
    private boolean mExtended;
    private boolean mIsAnimating;
    private float mCardOffsetDistance;
    private float mStartRadius;
    private float mEndRadius;
    private int mSelectedChildPosition;
    private float mMoveUp;
    private static final int LONG_DURING = 800;
    private static final int SHORT_DURING = 300;
    private static final int DELAY = LONG_DURING - SHORT_DURING;
    private static final String TAG = Transformer.class.getName();
    private static final String MAIN_FRAGMENT_TAG = "fragment_main";
    private static final String NEXT_FRAGMENT_TAG = "fragment_next";

    private TextView mTextView;
    private MaskView mTopMaskView;
    private RecyclerView mMainRecyclerView;
    private RecyclerView mNextRecyclerView;
    private android.support.v7.widget.Toolbar mV7ToolBar;
    private Rect mToolbarBounds;
    private ViewGroup.LayoutParams mToolbarLayoutParams;
    private ViewGroup mDecorView;
    private int mOldColor;

    private FragmentManager mFragmentManager;

    private Fragment mMainFragment;
    private Fragment mNextFragment;
    public void setAnimationListener(AnimationListener animationListener) {
        mAnimationListener = animationListener;
    }

    public Transformer(Activity activity) {
        mActivity = activity;
        mV7ToolBar = ((GettingToolbar) activity).getV7Toolbar();
        mToolbarLayoutParams = mV7ToolBar.getLayoutParams();
        mDecorView = (ViewGroup) activity.getWindow().getDecorView();
        mTopMaskView = new MaskView(mActivity);
        mDecorView.addView(mTopMaskView);

        mFragmentManager = mActivity.getFragmentManager();
        mMainFragment = mFragmentManager.findFragmentByTag(MAIN_FRAGMENT_TAG);
        mMainRecyclerView = (RecyclerView) mMainFragment.getView();

        mNextFragment = mFragmentManager.findFragmentByTag(NEXT_FRAGMENT_TAG);
        mNextRecyclerView = (RecyclerView) mNextFragment.getView();
        mFragmentManager.beginTransaction().hide(mNextFragment).commit();
    }

    public RecyclerView getMainRecyclerView() {
        return mMainRecyclerView;
    }

    public RecyclerView getNextRecyclerView() {
        return mNextRecyclerView;
    }

    void activateAwareMotion(View target, MainViewHolder holder,
                             int selectedChildPosition) {
        mSelectedChildPosition = selectedChildPosition;

        mTextView = holder.getTitleView();
        mTextView.setVisibility(View.INVISIBLE);

        final Rect targetBounds = new Rect();
        final Rect topMaskViewInitBounds = new Rect();
        target.getDrawingRect(targetBounds);
        Logcat.d(TAG, "before offsetDescendantRectToMyCoords targetBounds = " + targetBounds
                .flattenToString());
        mTopMaskView.getDrawingRect(topMaskViewInitBounds);
        mDecorView.offsetDescendantRectToMyCoords(target, targetBounds);

        Logcat.d(TAG, "after offsetDescendantRectToMyCoords targetBounds = " + targetBounds
                .flattenToString());
        mCardOffsetDistance = targetBounds.centerY() - topMaskViewInitBounds.centerY();
        Logcat.d(TAG, "activateAwareMotion mCardOffsetDistance = " + mCardOffsetDistance);

        mToolbarBounds = new Rect();
        mV7ToolBar.getDrawingRect(mToolbarBounds);
        mDecorView.offsetDescendantRectToMyCoords(mV7ToolBar, mToolbarBounds);

        mEndRadius = (float) Math.hypot(topMaskViewInitBounds.width() * .5f,
                topMaskViewInitBounds.height() * .5f);//√（x²+y²）
//        mStartRadius = (float) Math.hypot(mTextView.getWidth() * .5f,
//                mTextView.getHeight() * .5f);//√（x²+y²）
        mStartRadius = mTextView.getHeight() / 2;
        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(mTopMaskView,
                        topMaskViewInitBounds.centerX(),
                        topMaskViewInitBounds.centerY(),
                        mStartRadius,
                        mEndRadius);
        circularReveal.setDuration(LONG_DURING);

        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mCardOffsetDistance, mToolbarBounds.top);
        pathAnimator.setDuration(SHORT_DURING);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "activateAwareMotion ValueAnimator offsetY = " + offsetY);
                mTopMaskView.setTranslationY(offsetY);
            }
        });
        pathAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFragmentManager.beginTransaction().hide(mMainFragment).commit();
                riseUpRecyclerView(topMaskViewInitBounds);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(circularReveal, pathAnimator);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
                mOldColor = ((ColorDrawable) mV7ToolBar.getBackground()).getColor();
                mTopMaskView.setVisibility(View.VISIBLE);
                if (!(mTopMaskView.getTranslationY() == mCardOffsetDistance)) {
                    mTopMaskView.setTranslationY(mCardOffsetDistance);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTopMaskView.setVisibility(View.INVISIBLE);
                mV7ToolBar.setBackgroundColor(mActivity.getResources().getColor(R.color.color_1));
                mIsAnimating = false;
                mExtended = true;
            }
        });
        if (!isAnimating()) {
            set.start();
        }
    }

    private void riseUpRecyclerView(final Rect topMaskViewInitBounds) {
        Logcat.d("TAG", "transformChildRecyclerView param topCardRect = " +
                topMaskViewInitBounds.flattenToString());
        //recyclerView was defined android:layout_height="match_parent",
        //that's mean is that it only drawing the Rect equivalent to sum of Rect of it's child view.
        //so we using height to calculate how long need to rising up.
        //NOTICE: in the end the NextRecyclerView will change height of itself,
        //so if use mMoveUp = topCardRect.bottom - mNextRecyclerView.getHeight();
        //will get mMoveUp smaller and smaller
        mMoveUp = mMainRecyclerView.getHeight();
        Logcat.d(TAG, "recyclerView move up = " + mMoveUp);
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mMoveUp, 0);
        pathAnimator.setInterpolator(new FastOutSlowInInterpolator());
        pathAnimator.setDuration(SHORT_DURING);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "activateAwareMotion ValueAnimator offsetY = " + offsetY);
                mNextRecyclerView.setTranslationY(offsetY);
            }
        });
        pathAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFragmentManager.beginTransaction().show(mNextFragment).commit();
                if(mAnimationListener != null) {
                    mAnimationListener.onStart(mSelectedChildPosition);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        pathAnimator.start();
    }

    public void reset() {
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(0, mMoveUp);
        pathAnimator.setDuration(SHORT_DURING);
        pathAnimator.setInterpolator(new FastOutSlowInInterpolator());
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                mNextRecyclerView.setTranslationY(offsetY);
            }
        });
        pathAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
                resetTopMaskView();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFragmentManager.beginTransaction().hide(mNextFragment).commit();
                mFragmentManager.beginTransaction().show(mMainFragment).commit();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if (!isAnimating()) {
            pathAnimator.start();
        }
    }

    private void resetTopMaskView() {
        Rect topMaskViewBounds = new Rect();
        mTopMaskView.getDrawingRect(topMaskViewBounds);

        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(mTopMaskView,
                        topMaskViewBounds.centerX(),
                        topMaskViewBounds.centerY(),
                        mEndRadius,
                        mStartRadius);
        circularReveal.setDuration(LONG_DURING);

        Logcat.d(TAG, "reset mCardOffsetDistance = " + mCardOffsetDistance);
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mToolbarBounds.top, mCardOffsetDistance);
        pathAnimator.setDuration(SHORT_DURING);
        pathAnimator.setStartDelay(DELAY);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "raiseUpRecyclerView ValueAnimator offsetY = " + offsetY);
                mTopMaskView.setTranslationY(offsetY);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.playTogether(circularReveal, pathAnimator);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTopMaskView.setVisibility(View.VISIBLE);
                mV7ToolBar.setBackgroundColor(mOldColor);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTopMaskView.setVisibility(View.INVISIBLE);
                mTextView.setVisibility(View.VISIBLE);
                mIsAnimating = false;
                mExtended = false;
            }
        });
        set.start();
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }

    public boolean isExtended() {
        return mExtended;
    }

    public int getSelectedChildPosition() {
        return mSelectedChildPosition;
    }

    private class MaskView extends View {

        public MaskView(Context context) {
            super(context);
            setLayoutParams(mToolbarLayoutParams);
            setBackgroundColor(context.getResources().getColor(R.color.color_1));
            setVisibility(View.INVISIBLE);
        }
    }
}
