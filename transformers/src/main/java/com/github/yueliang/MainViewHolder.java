package com.github.yueliang;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yue_liang on 2016/12/4.
 */

public class MainViewHolder extends RecyclerView.ViewHolder {
    private TextView mTextView;
    public MainViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(getTitleResourceId());
    }

    public int getTitleResourceId() {
        return R.id.title;
    }

    public TextView getTitleView() {
        return mTextView;
    }
}