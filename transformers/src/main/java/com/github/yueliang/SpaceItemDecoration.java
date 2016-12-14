/**
 * Copyright (C) 2016 yueyihui

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.yueliang;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.View;

/**
 * Created by c_yuelia on 16-12-5.
 */

public class SpaceItemDecoration extends ItemDecoration {
    private int space;
    private String TAG = SpaceItemDecoration.class.getName();
    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        view.setElevation(2);
        if(isNotReachedBottom(parent, view)) {
            Logcat.d(TAG, "is not reached the bottom");
            outRect.top = space;
            outRect.right = space;
            outRect.left = space;
        } else {
            Logcat.d(TAG, "reached the bottom");
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
