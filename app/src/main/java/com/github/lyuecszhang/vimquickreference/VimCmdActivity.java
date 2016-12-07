package com.github.lyuecszhang.vimquickreference;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class VimCmdActivity extends AppCompatActivity {
    private String TAG = VimCmdActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vim_cmd);
        int requestCode = getIntent().getIntExtra(VimCmdCollections.CURRENT_POSITION, -1);
        Logcat.d(TAG, "requestCode = " + requestCode);
        String[] cmd = VimCmdCollections.getVimCmdByPosition(requestCode);
    }
}
