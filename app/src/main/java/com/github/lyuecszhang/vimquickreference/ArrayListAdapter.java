package com.github.lyuecszhang.vimquickreference;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.RectEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import static android.view.View.LAYER_TYPE_HARDWARE;

/**
 * Created by yue_liang on 2016/12/4.
 */

public class ArrayListAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context mContext;
    private List<String> mDataList;
    private String[] mDataString;
    private String TAG = ArrayListAdapter.class.getName();
    private float maskElevation;
    private boolean mExtended;

    public ArrayListAdapter(Context context, List<String> data) {
        mContext = context;
        mDataList = data;
    }

    public ArrayListAdapter(Context context, String[] data) {
        mContext = context;
        mDataString = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).
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
//                Intent intent = new Intent(mContext, VimCmdActivity.class);
//                intent.putExtra(VimCmdCollections.CURRENT_POSITION, position);
//                ((MainActivity) mContext).startActivityForResult(intent, position);
                activateAwareMotion(v);
            }
        });
    }

    private void activateAwareMotion(View target) {
        final CardView topCardView = (CardView) ((MainActivity) mContext).findViewById(R.id.top);

        //target.setEnabled(false);

        // Coordinates of circle initial point
        final ViewGroup parent = (ViewGroup) topCardView.getParent();
        final Rect targetBounds = new Rect();
        final Rect topCardInitBounds = new Rect();
        target.getDrawingRect(targetBounds);
        topCardView.getDrawingRect(topCardInitBounds);
        parent.offsetDescendantRectToMyCoords(target, targetBounds);
        parent.offsetDescendantRectToMyCoords(topCardView, topCardInitBounds);

        // Put Mask view at circle initial points
        maskElevation = topCardView.getCardElevation();
        topCardView.setVisibility(View.VISIBLE);
        topCardView.setTranslationY(targetBounds.centerY() - topCardInitBounds.centerY());

        ((RecyclerView) target.getParent()).setVisibility(View.INVISIBLE);

        float a = (float) Math.hypot(topCardInitBounds.width() * .5f,
                topCardInitBounds.height() * .5f);//√（x²+y²）
        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(topCardView,
                        topCardInitBounds.centerX(),
                        topCardInitBounds.centerY(),
                        target.getHeight() / 2,
                        a);
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(targetBounds.centerY() - topCardInitBounds.centerY(), 1).setDuration(1000);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                topCardView.setTranslationY((float) animation.getAnimatedValue());
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(circularReveal, pathAnimator);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.setDuration(1000);
        set.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                mExtended = true;
                topCardView.setTranslationY(0);
            }
        });
        set.start();
    }

    @Override
    public int getItemCount() {
        return mDataString.length;
    }
}
