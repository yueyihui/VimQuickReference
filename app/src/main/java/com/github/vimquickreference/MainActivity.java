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
package com.github.vimquickreference;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.yueliang.GettingToolbar;
import com.github.yueliang.MainArrayListAdapterAdapter;
import com.github.yueliang.NextArrayListAdapterAdapter;
import com.github.yueliang.SpaceItemDecoration;
import com.github.yueliang.TransformTool;
import com.github.yueliang.Transformer;


public class MainActivity extends AppCompatActivity implements TransformTool, GettingToolbar {
    private RecyclerView mMainRecyclerView;
    private RecyclerView mNextRecyclerView;
    private Transformer transformer;
    private static final String TAG = MainActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        VimCmdCollections.loadVimCmdsAndDescrip(this.getResources());

        initRecyclerView();
    }

    private void initRecyclerView() {
        transformer = new Transformer(this);
        int spacingInPixels = getResources().
                getDimensionPixelSize(R.dimen.recycler_view_item_view_space);
        mMainRecyclerView = transformer.getMainRecyclerView();
        mMainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMainRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        mMainRecyclerView.setAdapter(new MainArrayListAdapterAdapter(this,
                VimCmdCollections.getVimTitle()));

        mNextRecyclerView = transformer.getNextRecyclerView();
        mNextRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNextRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        mNextRecyclerView.setAdapter(new NextArrayListAdapterAdapter(MainActivity.this));

        transformer.setAnimationListener(new Transformer.AnimationListener() {
            @Override
            public void onStart(int selectedPosition) {
                ((NextArrayListAdapterAdapter) mNextRecyclerView.getAdapter()).
                        changeData(VimCmdCollections.getVimCmdDesByPosition(selectedPosition),
                                VimCmdCollections.getVimCmdByPosition(selectedPosition));
            }
        });
    }

    @Override
    public Transformer getTransformer() {
        return transformer;
    }

    @Override
    public void onBackPressed() {
        if (!transformer.isAnimating() &&
                transformer.isExtended()) {
            transformer.reset();
        } else if(transformer.isAnimating()) {
            //do nothing, if remove else if, will encounter exit activity when animating
        } else {
            super.onBackPressed();
        }
    }

    @Override //depends on ArrayListAdapater::onBindViewHolder
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Toolbar getV7Toolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }
}
