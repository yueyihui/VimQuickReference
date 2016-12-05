package com.github.lyuecszhang.vimquickreference;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.util.Log;
import android.view.View;

/**
 * Created by c_yiguoc on 16-12-5.
 */

public class SpaceItemDecoration extends ItemDecoration {
    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        view.setElevation(2);
        if(isNotReachedBottom(parent, view)) {
            Logcat.d("SpaceItemDecoration", "is not reached the bottom");
            outRect.top = space;
            outRect.right = space;
            outRect.left = space;
        } else {
            Logcat.d("SpaceItemDecoration", "reached the bottom");
            outRect.top = space;
            outRect.bottom = space;
            outRect.right = space;
            outRect.left = space;
        }
    }

    private boolean isNotReachedBottom(RecyclerView rc, View child) {
        return rc.getChildAdapterPosition(child) < rc.getAdapter().getItemCount() - 1;
    }
}
