package com.github.yueliang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
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
    private MaskView mTopMaskView;
    private RecyclerView mMainRecyclerView;
    private RecyclerView mNextRecyclerView;
    private android.support.v7.widget.Toolbar mV7ToolBar;
    private Rect mToolbarBounds;
    private ViewGroup.LayoutParams mToolbargetLayoutParams;
    private ViewGroup mDecorView;
    private int mOldColor;
    public void setDownAnimationListener (DownAnimationListener downAnimationListener) {
        mDownAnimationListener = downAnimationListener;
    }

    public Transformer(Activity activity) {
        mActivity = activity;
        mV7ToolBar = ((GettingToolbar) activity).getV7Toolbar();
        mToolbargetLayoutParams = mV7ToolBar.getLayoutParams();
        Log.d(TAG, mToolbargetLayoutParams.height + "");
        mDecorView = (ViewGroup) activity.getWindow().getDecorView();
        mTopMaskView = new MaskView(mActivity);
        mDecorView.addView(mTopMaskView);
    }

    public void activateAwareMotion(View target, MainViewHolder holder,
                             int selectedChildPosition) {
        mSelectedChildPosition = selectedChildPosition;

        mTextView = holder.getTitleView();
        mTextView.setVisibility(View.INVISIBLE);
        mMainRecyclerView = (RecyclerView) mActivity.findViewById(R.id.main_page_recycler_view);
        mNextRecyclerView = (RecyclerView) mActivity.findViewById(R.id.next_page_recycler_view);

        final Rect targetBounds = new Rect();
        final Rect topMaskViewInitBounds = new Rect();
        target.getDrawingRect(targetBounds);
        Logcat.d(TAG, "before offsetDescendantRectToMyCoords targetBounds = " + targetBounds
                .flattenToString());
        mTopMaskView.getDrawingRect(topMaskViewInitBounds);
        mDecorView.offsetDescendantRectToMyCoords(target, targetBounds);
        //mLayoutMainParent.offsetDescendantRectToMyCoords(mTopMaskView, topMaskViewInitBounds);
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
        circularReveal.setDuration(1000);

        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mCardOffsetDistance, mToolbarBounds.top);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "activateAwareMotion ValueAnimator offsetY = " + offsetY);
                mTopMaskView.setTranslationY(offsetY);
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
                mOldColor = ((ColorDrawable) mV7ToolBar.getBackground()).getColor();
                mTopMaskView.setVisibility(View.VISIBLE);
                if (!(mTopMaskView.getTranslationY() == mCardOffsetDistance)) {
                    mTopMaskView.setTranslationY(mCardOffsetDistance);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mMainRecyclerView.setVisibility(View.INVISIBLE);
                mTopMaskView.setVisibility(View.INVISIBLE);
                mV7ToolBar.setBackgroundColor(mActivity.getResources().getColor(R.color.color_1));
                riseUpRecyclerView(topMaskViewInitBounds);
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
                ofFloat(0, mMoveUp);
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
                resetTopMaskView();
            }
        });
        if (!isAnimating()) {
            set.start();
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

        Logcat.d(TAG, "reset mCardOffsetDistance = " + mCardOffsetDistance);
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mToolbarBounds.top, mCardOffsetDistance);
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
        set.setDuration(CIRCULAR_REVEAL_DURING);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTopMaskView.setVisibility(View.VISIBLE);
                mV7ToolBar.setBackgroundColor(mOldColor);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
                mExtended = false;
                mTopMaskView.setVisibility(View.INVISIBLE);
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

    private class MaskView extends View {

        public MaskView(Context context) {
            super(context);
            setLayoutParams(mToolbargetLayoutParams);
            setBackgroundColor(context.getResources().getColor(R.color.color_1));
            setVisibility(View.INVISIBLE);
        }
    }
}
