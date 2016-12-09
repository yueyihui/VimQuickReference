package com.github.lyuecszhang.vimquickreference;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.github.yueliang.ArrayListAdapter;
import com.github.yueliang.SpaceItemDecoration;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        VimCmdCollections.loadVimCmds(this.getResources());

        initRecyclerView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_page_recycler_view);
        ArrayListAdapter adapter = (ArrayListAdapter) recyclerView.getAdapter();
        if (adapter.getExtendState()) {
            adapter.reset();
        } else if(adapter.isAnimating()) {
            //do nothing, if remove else if, will encounter exit activity when animating
        } else {
            super.onBackPressed();
        }
    }

    private void initRecyclerView() {
        int spacingInPixels = getResources().
                getDimensionPixelSize(R.dimen.recycler_view_item_view_space);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_page_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        recyclerView.setAdapter(new ArrayListAdapter(this, VimCmdCollections.getVimTitle()));
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
}
