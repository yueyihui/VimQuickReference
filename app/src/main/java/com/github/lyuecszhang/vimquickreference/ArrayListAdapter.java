package com.github.lyuecszhang.vimquickreference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                Intent intent = new Intent(mContext, VimCmdActivity.class);
                intent.putExtra(VimCmdCollections.CURRENT_POSITION, position);
                ((MainActivity) mContext).startActivityForResult(intent, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataString.length;
    }
}
