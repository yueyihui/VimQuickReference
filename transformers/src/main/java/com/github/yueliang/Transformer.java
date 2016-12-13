package com.github.yueliang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by c_yuelia on 16-12-12.
 */

public class Transformer {
    public interface DownAnimationListener {
        public void onStart(int selectedPosition);
    }
    private DownAnimationListener mDownAnimationListener;
    private Activity mActivity;
    private boolean mExtended;
    private boolean mIsAnimating;
    private float mCardOffsetDistance;
    private float mStartRadius;
    private float mEndRadius;
    private int mSelectedChildPosition;
    private float mMoveUp;
    private static int DROP_RISE_DURING = 500;
    private static int CIRCULAR_REVEAL_DURING = 300;
    private String TAG = Transformer.class.getName();

    private TextView mTextView;
    private CardView mTopCardView;
    private RecyclerView mMainRecyclerView;
    private RecyclerView mNextRecyclerView;

    public void setDownAnimationListener (DownAnimationListener downAnimationListener) {
        mDownAnimationListener = downAnimationListener;
    }

    public Transformer(Activity activity) {
        mActivity = activity;
    }

    public void activateAwareMotion(View target, MainViewHolder holder,
                             int selectedChildPosition) {
        mSelectedChildPosition = selectedChildPosition;

        mTextView = holder.getTitleView();
        mTextView.setVisibility(View.INVISIBLE);
        mTopCardView = (CardView) mActivity.findViewById(R.id.top);
        mMainRecyclerView = (RecyclerView) mActivity.findViewById(R.id.main_page_recycler_view);
        mNextRecyclerView = (RecyclerView) mActivity.findViewById(R.id.next_page_recycler_view);

        // Coordinates of circle initial point
        final ViewGroup parent = (ViewGroup) mTopCardView.getParent();
        final Rect targetBounds = new Rect();
        final Rect topCardInitBounds = new Rect();
        target.getDrawingRect(targetBounds);
        Logcat.d(TAG, "before offsetDescendantRectToMyCoords targetBounds = " + targetBounds
                .flattenToString());
        mTopCardView.getDrawingRect(topCardInitBounds);
        parent.offsetDescendantRectToMyCoords(target, targetBounds);
        parent.offsetDescendantRectToMyCoords(mTopCardView, topCardInitBounds);
        Logcat.d(TAG, "after offsetDescendantRectToMyCoords targetBounds = " + targetBounds
                .flattenToString());
        mCardOffsetDistance = targetBounds.centerY() - topCardInitBounds.centerY();
        Logcat.d(TAG, "activateAwareMotion mCardOffsetDistance = " + mCardOffsetDistance);

        mEndRadius = (float) Math.hypot(topCardInitBounds.width() * .5f,
                topCardInitBounds.height() * .5f);//√（x²+y²）
//        mStartRadius = (float) Math.hypot(mTextView.getWidth() * .5f,
//                mTextView.getHeight() * .5f);//√（x²+y²）
        mStartRadius = mTextView.getHeight() / 2;
        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(mTopCardView,
                        topCardInitBounds.centerX(),
                        topCardInitBounds.centerY(),
                        mStartRadius,
                        mEndRadius);

        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mCardOffsetDistance, 0);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "activateAwareMotion ValueAnimator offsetY = " + offsetY);
                mTopCardView.setTranslationY(offsetY);
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
                mTopCardView.setVisibility(View.VISIBLE);
                if (!(mTopCardView.getTranslationY() == mCardOffsetDistance)) {
                    mTopCardView.setTranslationY(mCardOffsetDistance);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mMainRecyclerView.setVisibility(View.INVISIBLE);
                dropDownRecyclerView(topCardInitBounds);
            }
        });
        if (!isAnimating()) {
            set.start();
        }
    }

    private void dropDownRecyclerView(final Rect topCardRect) {
        Logcat.d("TAG", "transformChildRecyclerView param topCardRect = " +
                topCardRect.flattenToString());
        //recyclerView was defined android:layout_height="match_parent",
        //that's mean is that it only drawing the Rect equivalent to sum of Rect of it's child view.
        //so we using height to calculate how long need to rising up.
        //NOTICE: in the end the NextRecyclerView will change height of itself,
        //so if use mMoveUp = topCardRect.bottom - mNextRecyclerView.getHeight();
        //will get mMoveUp smaller and smaller
        mMoveUp = topCardRect.bottom - mMainRecyclerView.getHeight();
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mMoveUp, topCardRect.bottom);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "activateAwareMotion ValueAnimator offsetY = " + offsetY);
                mNextRecyclerView.setTranslationY(offsetY);
            }
        });
            AnimatorSet set = new AnimatorSet();
            set.playTogether(pathAnimator);
            set.setInterpolator(new FastOutSlowInInterpolator());
            set.setDuration(DROP_RISE_DURING);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mNextRecyclerView.setTranslationY(mMoveUp);

                    Logcat.d(TAG, "recyclerView move up = " + mMoveUp);
                    ViewGroup.LayoutParams origin = mNextRecyclerView.getLayoutParams();
                    origin.height = -(int) mMoveUp;
                    mNextRecyclerView.setLayoutParams(origin);

                    mNextRecyclerView.setVisibility(View.VISIBLE);
                    if(mDownAnimationListener != null) {
                        mDownAnimationListener.onStart(mSelectedChildPosition);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsAnimating = false;
                    mExtended = true;
                }
            });
            set.start();
    }

    public void reset() {
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mTopCardView.getBottom(), mMoveUp);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                mNextRecyclerView.setTranslationY(offsetY);
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
                mNextRecyclerView.setVisibility(View.GONE);
                mMainRecyclerView.setVisibility(View.VISIBLE);
                resetTopCardView();
            }
        });
        if (!isAnimating()) {
            set.start();
        }
    }

    private void resetTopCardView() {
        Rect topCardBounds = new Rect();
        mTopCardView.getDrawingRect(topCardBounds);

        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(mTopCardView,
                        topCardBounds.centerX(),
                        topCardBounds.centerY(),
                        mEndRadius,
                        mStartRadius);

        Logcat.d(TAG, "reset mCardOffsetDistance = " + mCardOffsetDistance);
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(0, mCardOffsetDistance);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "raiseUpRecyclerView ValueAnimator offsetY = " + offsetY);
                mTopCardView.setTranslationY(offsetY);
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
                mTopCardView.setVisibility(View.INVISIBLE);
                mTextView.setVisibility(View.VISIBLE);
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
}
