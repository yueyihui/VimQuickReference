package com.github.yueliang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Debug;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

/**
 * Created by c_yiguoc on 16-12-12.
 */

public class Transformer {
    private Activity mActivity;
    private boolean mExtended;
    private boolean mIsAnimating;
    private float mCardOffsetDistance;
    private int mSelectedChildPosition;
    private float mMoveUp;
    private static int DROP_RISE_DURING = 500;
    private static int CIRCULAR_REVEAL_DURING = 300;
    private String TAG = Transformer.class.getName();

    Transformer(Activity activity) {
        mActivity = activity;
    }

    void activateAwareMotion(View target, int selectedChildPosition) {
        mSelectedChildPosition = selectedChildPosition;

        final CardView topCardView = (CardView) mActivity.findViewById(R.id.top);
        final RecyclerView recyclerView = ((RecyclerView) target.getParent());

        // Coordinates of circle initial point
        final ViewGroup parent = (ViewGroup) topCardView.getParent();
        final Rect targetBounds = new Rect();
        final Rect topCardInitBounds = new Rect();
        target.getDrawingRect(targetBounds);
        Logcat.d(TAG, "before offsetDescendantRectToMyCoords targetBounds = " + targetBounds
                .flattenToString());
        topCardView.getDrawingRect(topCardInitBounds);
        parent.offsetDescendantRectToMyCoords(target, targetBounds);
        parent.offsetDescendantRectToMyCoords(topCardView, topCardInitBounds);
        Logcat.d(TAG, "after offsetDescendantRectToMyCoords targetBounds = " + targetBounds
                .flattenToString());
        mCardOffsetDistance = targetBounds.centerY() - topCardInitBounds.centerY();
        Logcat.d(TAG, "activateAwareMotion mCardOffsetDistance = " + mCardOffsetDistance);

        float a = (float) Math.hypot(topCardInitBounds.width() * .5f,
                topCardInitBounds.height() * .5f);//√（x²+y²）
        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(topCardView,
                        topCardInitBounds.centerX(),
                        topCardInitBounds.centerY(),
                        target.getHeight() / 2,
                        a);

        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mCardOffsetDistance, 0);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "activateAwareMotion ValueAnimator offsetY = " + offsetY);
                topCardView.setTranslationY(offsetY);
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(circularReveal, pathAnimator);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.setDuration(CIRCULAR_REVEAL_DURING);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
                if (!Debug.isDebuggerConnected()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                topCardView.setVisibility(View.VISIBLE);
                topCardView.setTranslationY(mCardOffsetDistance);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (Debug.isDebuggerConnected()) {
                    topCardView.setVisibility(View.INVISIBLE);
                }
                dropDownRecyclerView(topCardInitBounds, recyclerView);
            }
        });
        if (!isAnimating()) {
            set.start();
        }
    }

    private void dropDownRecyclerView(final Rect topCardRect, final View recyclerView) {
        Logcat.d("TAG", "transformChildRecyclerView param topCardRect = " +
                topCardRect.flattenToString());
        //recyclerView was defined android:layout_height="match_parent",
        //that's mean is that it only drawing the Rect equivalent to sum of Rect of it's child view.
        //so we using height to calculate how long need to rising up.
        mMoveUp = topCardRect.bottom - recyclerView.getHeight();
        if (Debug.isDebuggerConnected()) {
            recyclerView.setTranslationY(mMoveUp);
            Logcat.d(TAG, "recyclerView move up = " + mMoveUp);
            recyclerView.setVisibility(View.VISIBLE);
        }
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mMoveUp, topCardRect.bottom);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "activateAwareMotion ValueAnimator offsetY = " + offsetY);
                recyclerView.setTranslationY(offsetY);
            }
        });
        if (Debug.isDebuggerConnected()) {
            pathAnimator.setStartDelay(3000);
            pathAnimator.setDuration(3000);
            pathAnimator.start();
        } else {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(pathAnimator);
            set.setInterpolator(new FastOutSlowInInterpolator());
            set.setDuration(DROP_RISE_DURING);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    recyclerView.setTranslationY(mMoveUp);
                    Logcat.d(TAG, "recyclerView move up = " + mMoveUp);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsAnimating = false;
                    mExtended = true;
                    ViewGroup.LayoutParams origin = recyclerView.getLayoutParams(); //重设置高度
                    origin.height = -(int) mMoveUp;
                    recyclerView.setLayoutParams(origin);
                }
            });
            set.start();
        }
    }

    public void reset() {
        final RecyclerView recyclerView = (RecyclerView) mActivity.
                findViewById(R.id.main_page_recycler_view);

        final CardView topCardView = (CardView) mActivity.findViewById(R.id.top);

        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(topCardView.getBottom(), mMoveUp);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                recyclerView.setTranslationY(offsetY);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.playTogether(pathAnimator);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.setDuration(DROP_RISE_DURING);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                recyclerView.setLayoutParams(params);
                recyclerView.setTranslationY(params.height);
                resetTopCardView(topCardView, recyclerView);
            }
        });
        if (!isAnimating()) {
            set.start();
        }
    }

    private void resetTopCardView(final View topCardView, final View recyclerView) {
        Rect topCardBounds = new Rect();
        topCardView.getDrawingRect(topCardBounds);

        float a = (float) Math.hypot(topCardBounds.width() * .5f,
                topCardBounds.height() * .5f);//√（x²+y²）
        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(topCardView,
                        topCardBounds.centerX(),
                        topCardBounds.centerY(),
                        a,
                        0);

        Logcat.d(TAG, "reset mCardOffsetDistance = " + mCardOffsetDistance);
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(0, mCardOffsetDistance);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "raiseUpRecyclerView ValueAnimator offsetY = " + offsetY);
                topCardView.setTranslationY(offsetY);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.playTogether(circularReveal, pathAnimator);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.setDuration(CIRCULAR_REVEAL_DURING);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
                mExtended = false;
                topCardView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
        set.start();
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }

    public boolean getExtendState() {
        return mExtended;
    }
}
