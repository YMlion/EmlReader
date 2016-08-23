package com.xyxg.android.emlreader;

import android.app.Application;

import com.android.emailcommon.TempDirectory;

/**
 * @author ymlion
 * @date 2016/8/22
 */

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TempDirectory.setTempDirectory(this);
    }
}
