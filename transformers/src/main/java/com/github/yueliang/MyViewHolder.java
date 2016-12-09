package com.github.yueliang;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by yue_liang on 2016/12/4.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {
    public MyViewHolder(View itemView) {
        super(itemView);
    }

    public int getTitleResourceId() {
        return R.id.title;
    }
}