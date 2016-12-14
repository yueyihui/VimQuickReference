package com.github.yueliang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
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
    private static final int LONG_DURING = 800;
    private static final int SHORT_DURING = 300;
    private static final int DELAY = LONG_DURING - SHORT_DURING;
    private static final String TAG = Transformer.class.getName();

    private TextView mTextView;
    private MaskView mTopMaskView;
    private RecyclerView mMainRecyclerView;
    private RecyclerView mNextRecyclerView;
    private android.support.v7.widget.Toolbar mV7ToolBar;
    private Rect mToolbarBounds;
    private ViewGroup.LayoutParams mToolbarLayoutParams;
    private ViewGroup mDecorView;
    private int mOldColor;
    public void setDownAnimationListener (DownAnimationListener downAnimationListener) {
        mDownAnimationListener = downAnimationListener;
    }

    public Transformer(Activity activity) {
        mActivity = activity;
        mV7ToolBar = ((GettingToolbar) activity).getV7Toolbar();
        mToolbarLayoutParams = mV7ToolBar.getLayoutParams();
        mDecorView = (ViewGroup) activity.getWindow().getDecorView();
        mTopMaskView = new MaskView(mActivity);
        mDecorView.addView(mTopMaskView);
        mMainRecyclerView = (RecyclerView) mActivity.findViewById(R.id.main_page_recycler_view);
        mNextRecyclerView = (RecyclerView) mActivity.findViewById(R.id.next_page_recycler_view);
    }

    public RecyclerView getMainRecyclerView() {
        return mMainRecyclerView;
    }

    public RecyclerView getNextRecyclerView() {
        return mNextRecyclerView;
    }

    public void activateAwareMotion(View target, MainViewHolder holder,
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
                mMainRecyclerView.setVisibility(View.INVISIBLE);
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
                mNextRecyclerView.setVisibility(View.VISIBLE);
                if(mDownAnimationListener != null) {
                    mDownAnimationListener.onStart(mSelectedChildPosition);
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
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mNextRecyclerView.setVisibility(View.GONE);
                mMainRecyclerView.setVisibility(View.VISIBLE);
                resetTopMaskView();
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
