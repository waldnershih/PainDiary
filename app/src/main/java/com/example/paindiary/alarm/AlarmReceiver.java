package com.example.paindiary.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.paindiary.notification.NotificationHelper;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());
    }
}

/**
 * Reference
 * https://www.youtube.com/watch?v=yrpimdBRk5Q&list=PLt_kGICKCFVPJ_rBwCMVb-9KdigyXaecU&index=3&t=39s&ab_channel=CodinginFlow
 */