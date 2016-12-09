package com.github.yueliang;

import android.os.Debug;
import android.util.Log;

/**
 * Created by c_yiguoc on 16-12-5.
 */

public class Logcat {
    public static void d(String className, String msg) {
        if(Debug.isDebuggerConnected())
            Log.d(className, msg);
    }
}
