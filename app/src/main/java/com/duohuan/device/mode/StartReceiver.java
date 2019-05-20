package com.duohuan.device.mode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.duohuan.device.MainActivity;

public class StartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent it = new Intent(context, MainActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
    }
}