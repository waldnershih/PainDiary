package com.example.paindiary.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.paindiary.R;

public class NotificationHelper extends ContextWrapper {
    public static final String CHANNEL_ID = "channelId";
    public static final String CHANNEL_NAME = "channel";

    private NotificationManager nManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel() {
        NotificationChannel channel  = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.colorPrimary);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Alarm")
                .setContentText("Time to keep your pain diary !")
                .setSmallIcon(R.drawable.ic_alert);
    }

    public NotificationManager getManager() {
        if (nManager == null) {
            nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return nManager;
    }

}

/**
 * Reference
 * https://www.youtube.com/watch?v=yrpimdBRk5Q&list=PLt_kGICKCFVPJ_rBwCMVb-9KdigyXaecU&index=3&t=39s&ab_channel=CodinginFlow
 */