package com.github.lyuecszhang.vimquickreference;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yue_liang on 2016/12/4.
 */

public class ArrayListAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context mContext;
    private List<String> mData;
    public ArrayListAdapter(Context context, List<String> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Spanned spanned = Html.fromHtml(mData.get(position));
        Spanned str = Html.fromHtml(spanned.toString());
        ((TextView) holder.itemView.findViewById(holder.getTitleResourceId()))
                .setText(str);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
