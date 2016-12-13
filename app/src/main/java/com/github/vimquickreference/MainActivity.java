package com.github.vimquickreference;

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
import com.github.yueliang.MainArrayListAdapterAdapter;
import com.github.yueliang.NextArrayListAdapterAdapter;
import com.github.yueliang.SpaceItemDecoration;
import com.github.yueliang.TransformTool;
import com.github.yueliang.Transformer;


public class MainActivity extends AppCompatActivity implements TransformTool {
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initRecyclerView() {
        transformer = new Transformer(this);
        int spacingInPixels = getResources().
                getDimensionPixelSize(R.dimen.recycler_view_item_view_space);
        mMainRecyclerView = (RecyclerView) findViewById(R.id.main_page_recycler_view);
        mMainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMainRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        mMainRecyclerView.setAdapter(new MainArrayListAdapterAdapter(this,
                VimCmdCollections.getVimTitle()));

        mNextRecyclerView = (RecyclerView) findViewById(R.id.next_page_recycler_view);
        mNextRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNextRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        mNextRecyclerView.setAdapter(new NextArrayListAdapterAdapter(MainActivity.this));

        transformer.setDownAnimationListener(new Transformer.DownAnimationListener() {
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
}
