package com.github.yueliang;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by c_yiguoc on 16-12-12.
 */

public class TransformRecyclerView extends RecyclerView{
    private Transformer mTransformer;
    public TransformRecyclerView(Context context) {
        super(context);
        mTransformer = new Transformer((Activity) context);
    }

    public TransformRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTransformer = new Transformer((Activity) context);
    }

    public TransformRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTransformer = new Transformer((Activity) context);
    }

    public Transformer getTransformer() {
        return mTransformer;
    }
}
