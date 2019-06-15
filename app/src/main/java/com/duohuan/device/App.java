package com.duohuan.device;

import android.app.Application;
import android.util.Log;

import com.duohuan.device.activity.ErrorActivity;
import com.duohuan.device.mode.DeviceSocketMode;
import com.duohuan.device.mode.WebSocketMode;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * 　　　　　　　┏┛┻━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 ████━████ ┃+
 * 　　　　　　　┃　　　　　　　┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　             ┃　　　　　　　┃ + +
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃ + + + +
 * 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　　┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 * 创建人: 杜
 * 日期: 2019/5/13
 * 时间: 14:59
 */
public class App extends Application {
    private static final String TAG = "APP";

    private static class Listener implements CustomActivityOnCrash.EventListener {
        @Override
        public void onLaunchErrorActivity() {
            Log.e(TAG, "onLaunchErrorActivity");
        }

        @Override
        public void onRestartAppFromErrorActivity() {
            Log.e(TAG, "onRestartAppFromErrorActivity");
        }

        @Override
        public void onCloseAppFromErrorActivity() {
            Log.e(TAG, "onCloseAppFromErrorActivity");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        debugInit();
        DeviceSocketMode.getInstance();
        WebSocketMode.getInstance();
    }

    private void debugInit() {
        CaocConfig.Builder.create()
                //Customizes what to do when the app crashes while it is in background. Possible values:
                //BackgroundMode.BACKGROUND_MODE_SHOW_CUSTOM: launch the error activity when the app is in background,
                //BackgroundMode.BACKGROUND_MODE_CRASH: launch the default system error when the app is in background,
                //BackgroundMode.BACKGROUND_MODE_SILENT: crash silently when the app is in background,
//                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                //This disables the interception of crashes. Use it to disable CustomActivityOnCrash (for example, depending on your buildType).
//                .enabled(false)
                //This hides the "error details" button in the error activity, thus hiding the stack trace
//                .showErrorDetails(false)
                //This avoids the app from using the "Restart app" button and displaying a "Close app" button directly.
                //Even with restart app enabled, the Close app can still be displayed if your app has no launch activity.
//                .showRestartButton(false)
                //This makes the library track the activites visited by the user and their lifecycle calls.
                //Use it if you want that info in the error details screen shown on the error activity.
//                .trackActivities(true)
                //This hides the additional log shown when the error activity is launched.
                //It is shown by default because the Android Studio Logcat view by default only shows
                //the current process output, and this makes the stack trace more obvious to find.
//                .logErrorOnRestart(false)
                //Defines the time that must pass between app crashes to determine that we are not in a crash loop.
                //If a crash has occurred less that this time ago, the error activity will not be launched
                //and the system crash screen will be invoked.
//                .minTimeBetweenCrashesMs(2000)
                //This shows a different image on the error activity, instead of the default upside-down bug.
                //You may use a drawable or a mipmap.
//                .errorDrawable(R.mipmap.ic_launcher)
                //This sets the restart activity.
                //If you set this, this will be used. However, you can also set it with an intent-filter:
                //  <action android:name="cat.ereza.customactivityoncrash.RESTART" />
                //If none are set, the default launch activity will be used.
                .restartActivity(MainActivity.class)
                .errorActivity(ErrorActivity.class)
                .eventListener(new Listener())
                .apply();
    }
}
