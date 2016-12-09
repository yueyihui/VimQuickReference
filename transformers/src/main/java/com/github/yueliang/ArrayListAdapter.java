package com.github.yueliang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yue_liang on 2016/12/4.
 */

public class ArrayListAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Activity mActivity;
    private List<String> mDataList;
    private String[] mDataString;
    private String TAG = ArrayListAdapter.class.getName();
    private boolean mExtended;
    private boolean mIsAnimating;
    private float mCardOffsetDistance;
    private int selectedChildPosition;

    public ArrayListAdapter(Activity activity, List<String> data) {
        mActivity = activity;
        mDataList = data;
    }

    public ArrayListAdapter(Activity activity, String[] data) {
        mActivity = activity;
        mDataString = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mActivity).
                inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Spanned spanned = Html.fromHtml(mDataString[position]);
        Spanned str = Html.fromHtml(spanned.toString());
        TextView textView = (TextView) holder.itemView.findViewById(holder.getTitleResourceId());
        textView.setText(str);
        textView.setTag(position);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.performClick();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedChildPosition = position;
                activateAwareMotion(v);
            }
        });
    }

    private void activateAwareMotion(View target) {
        final CardView topCardView = (CardView) mActivity.findViewById(R.id.top);
        final RecyclerView recyclerView = ((RecyclerView) target.getParent());

        // Coordinates of circle initial point
        final ViewGroup parent = (ViewGroup) topCardView.getParent();
        final Rect targetBounds = new Rect();
        final Rect topCardInitBounds = new Rect();
        target.getDrawingRect(targetBounds);
        topCardView.getDrawingRect(topCardInitBounds);
        parent.offsetDescendantRectToMyCoords(target, targetBounds);
        parent.offsetDescendantRectToMyCoords(topCardView, topCardInitBounds);

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
        set.setDuration(300);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
                recyclerView.setVisibility(View.INVISIBLE);
                topCardView.setVisibility(View.VISIBLE);
                topCardView.setTranslationY(mCardOffsetDistance);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
                mExtended = true;
            }
        });
        if (!isAnimating()) {
            set.start();
        }
    }

    public void reset() {
        final CardView topCardView = (CardView) mActivity.findViewById(R.id.top);
        final RecyclerView recyclerView = (RecyclerView) mActivity.
                findViewById(R.id.main_page_recycler_view);

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
                Logcat.d(TAG, "reset ValueAnimator offsetY = " + offsetY);
                topCardView.setTranslationY(offsetY);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.playTogether(circularReveal, pathAnimator);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.setDuration(300);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
                mExtended = false;
                topCardView.setVisibility(View.INVISIBLE);
            }
        });
        if (!isAnimating()) {
            set.start();
        }
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }

    public boolean getExtendState() {
        return mExtended;
    }

    @Override
    public int getItemCount() {
        return mDataString.length;
    }
}
