package com.github.yueliang;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by c_yiguoc on 16-12-12.
 */

public class NextViewHolder extends RecyclerView.ViewHolder {
    private TextView mLeftTextView;
    private TextView mRightTextView;
    public NextViewHolder(View itemView) {
        super(itemView);
        mLeftTextView = (TextView) itemView.findViewById(getLeftResourceId());
        mRightTextView = (TextView) itemView.findViewById(getRightResourceId());
    }
    private int getLeftResourceId() {
        return R.id.left;
    }
    private int getRightResourceId() {
        return R.id.right;
    }

    public TextView getLeftTextView() {
        return mLeftTextView;
    }
    public TextView getRightTextView() {
        return mRightTextView;
    }
}
