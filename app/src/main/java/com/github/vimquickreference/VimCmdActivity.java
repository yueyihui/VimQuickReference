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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.yueliang.Logcat;

public class VimCmdActivity extends AppCompatActivity {
    private String TAG = VimCmdActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vim_cmd);
        int requestCode = getIntent().getIntExtra(VimCmdCollections.CURRENT_POSITION, -1);
        Logcat.d(TAG, "get" + requestCode);
        String[] cmd = VimCmdCollections.getVimCmdByPosition(requestCode);
    }
}
