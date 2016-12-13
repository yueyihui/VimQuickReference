package com.github.yueliang;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by c_yuelia on 16-12-12.
 */

public class NextArrayListAdapterAdapter extends RecyclerView.Adapter<NextViewHolder>
        implements ArrayListAdapter {
    private Activity mActivity;
    private List<String> mDataList;
    private String[] mLeftDataString;
    private String[] mRightDataString;
    private String TAG = NextArrayListAdapterAdapter.class.getName();
    private Transformer mTransformer;

    public NextArrayListAdapterAdapter(Activity activity) {
        mActivity = activity;
        mTransformer = ((TransformTool) activity).getTransformer();
        mLeftDataString = new String[0];
        mRightDataString = new String[0];
        mDataList = new ArrayList<String>();
    }

    public NextArrayListAdapterAdapter(Activity activity, List<String> data) {
        mActivity = activity;
        mDataList = data;
        mTransformer = ((TransformTool) activity).getTransformer();
    }

    public NextArrayListAdapterAdapter(Activity activity, String[] dataLeft, String[] dataRight) {
        mActivity = activity;
        mLeftDataString = dataLeft;
        mRightDataString = dataRight;
        mTransformer = ((TransformTool) activity).getTransformer();
    }

    @Override
    public void changeData(String[] data) {

    }

    @Override
    public void changeData(String[] dataLeft, String[] dataRight) {
        mLeftDataString = dataLeft;
        mRightDataString = dataRight;
        this.notifyDataSetChanged();
    }

    @Override
    public void changeData(List<String> dataList) {
        mDataList = dataList;
        this.notifyDataSetChanged();
    }

    @Override
    public NextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NextViewHolder(LayoutInflater.from(mActivity).
                inflate(R.layout.next_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final NextViewHolder holder, final int position) {
        Spanned spannedLeft = Html.fromHtml(mLeftDataString[position]);
        Spanned parsedLeft = Html.fromHtml(spannedLeft.toString());
        TextView textViewLeft = holder.getLeftTextView();
        textViewLeft.setText(parsedLeft);

        Spanned spannedRight = Html.fromHtml(mRightDataString[position]);
        Spanned parsedRight = Html.fromHtml(spannedRight.toString());
        TextView textViewRight = holder.getRightTextView();
        textViewRight.setText(parsedRight);
    }

    @Override
    public int getItemCount() {
        if (mLeftDataString != null) {
            return mLeftDataString.length;
        } else if (mRightDataString != null) {
            return mRightDataString.length;
        } else {
            return 0;
        }
    }
}
