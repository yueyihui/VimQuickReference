package com.github.lyuecszhang.vimquickreference;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

/**
 * Created by yue_liang on 2016/12/4.
 */

public class ArrayListAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context mContext;
    private List<String> mDataList;
    private String[] mDataString;
    private String TAG = ArrayListAdapter.class.getName();

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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Spanned spanned = Html.fromHtml(mDataString[position]);
        Spanned str = Html.fromHtml(spanned.toString());
        TextView textView = (TextView) holder.itemView.findViewById(holder.getTitleResourceId());
        textView.setText(str);
        textView.setTag(position);
        textView.setOnClickListener(new View.OnClickListener() {
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
        final CardView top = (CardView) ((MainActivity) mContext).findViewById(R.id.top);

        Rect bounds = new Rect();
        Rect topBounds = new Rect();
        target.getDrawingRect(bounds);
        top.getDrawingRect(topBounds);

        Logcat.d(TAG, bounds.flattenToString());

        FrameLayout rl = (FrameLayout) ((MainActivity) mContext).
                findViewById(R.id.content_main);

        rl.offsetDescendantRectToMyCoords(target, bounds);
        rl.offsetDescendantRectToMyCoords(target, topBounds);

        top.setCardElevation(0);
        top.setVisibility(View.VISIBLE);
        top.setX(bounds.left - topBounds.centerX());
        top.setY(bounds.top - topBounds.centerY());

        ((MainActivity) mContext).findViewById(R.id.main_page_recycler_view)
                .setVisibility(View.INVISIBLE);

        final int cX = bounds.centerX();
        final int cY = bounds.centerY();

        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(top, cX, cY, target.getWidth() / 2,
                        (float) Math.hypot(topBounds.width() * .5f, topBounds.height() * .5f));

        final float c0X = bounds.centerX() - topBounds.centerX();
        final float c0Y = bounds.centerY() - topBounds.centerY();

        AnimatorPath path = new AnimatorPath();
        path.moveTo(c0X, c0Y);
        path.curveTo(c0X, c0Y, 0, c0Y, 0, 0);

        ObjectAnimator pathAnimator = ObjectAnimator.ofObject(this, "maskLocation", new PathEvaluator(),
                path.getPoints().toArray());

        AnimatorSet set = new AnimatorSet();
        set.playTogether(circularReveal, pathAnimator);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.setDuration(1500);
        set.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                top.setCardElevation(2);
            }
        });
        set.start();
    }
    @Override
    public int getItemCount() {
        return mDataString.length;
    }
}
