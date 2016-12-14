package com.github.yueliang;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yue_liang on 2016/12/4.
 */

public class MainArrayListAdapterAdapter extends RecyclerView.Adapter<MainViewHolder>
        implements ArrayListAdapter {
    private Activity mActivity;
    private List<String> mDataList;
    private String[] mDataString;
    private String TAG = MainArrayListAdapterAdapter.class.getName();
    private Transformer mTransformer;

    public MainArrayListAdapterAdapter(Activity activity) {
        mActivity = activity;
        mTransformer = ((TransformTool) activity).getTransformer();
    }

    public MainArrayListAdapterAdapter(Activity activity, List<String> data) {
        mActivity = activity;
        mDataList = data;
        mTransformer = ((TransformTool) activity).getTransformer();
    }

    public MainArrayListAdapterAdapter(Activity activity, String[] data) {
        mActivity = activity;
        mDataString = data;
        mTransformer = ((TransformTool) activity).getTransformer();
    }

    @Override
    public void changeData(String[] data) {
        mDataString = data;
        this.notifyDataSetChanged();
    }

    @Override
    public void changeData(String[] left, String[] right) {

    }

    @Override
    public void changeData(List<String> dataList) {
        mDataList = dataList;
        this.notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(mActivity).
                inflate(com.github.yueliang.R.layout.main_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        Spanned spanned = Html.fromHtml(mDataString[position]);
        Spanned str = Html.fromHtml(spanned.toString());
        TextView textView = holder.getTitleView();
        textView.setText(str);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.performClick();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTransformer.isAnimating() && !mTransformer.isExtended()) {
                    mTransformer.activateAwareMotion(v, holder, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataString.length;
    }

}
