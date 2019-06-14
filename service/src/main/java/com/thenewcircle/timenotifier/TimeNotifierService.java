package com.thenewcircle.timenotifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class TimeNotifierService extends Service {
    private static final String TAG = TimeNotifierService.class.getName();

    private BroadcastReceiver mTimeReceiver;

    public TimeNotifierService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Created");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Timer Notifier Service", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager =
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));

            notificationManager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(2, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "Received cmd Intent: Action = " + intent.getAction());

        //  Create our internal BR for time ticks
        if (mTimeReceiver == null) {
            Log.d(TAG, "Creating TICK receiver");

            mTimeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //  Verify this is the ACTION_TIME_TICK broadcast then turn
                    //  around and send our own Intent
                    Log.d(TAG, "mTimeReceiver.onReceive for ACTION = " + intent.getAction());

                    if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                        sendCustomTick();
                    }
                }
            };

            IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
            registerReceiver(mTimeReceiver, filter);
            Log.d(TAG, "Registered receiver with filter: " + mTimeReceiver.toString() +
                    ", " + filter.toString());
        }

        //  For new registrations (starts), automatically send a custom
        //  TICK so that clients immediately get "connected"
        sendCustomTick();

        //  Return a value of STICKY indicating that the system should restart
        //  us if we were killed at some point due to memory pressure.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroyed!");
        unregisterReceiver(mTimeReceiver);
        mTimeReceiver = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //  We do not support bind, just start/stop via Intent
        return null;
    }

    private void sendCustomTick() {
        Intent repIntent = new Intent("com.thenewcircle.timenotifier.ACTION_TICK");

        //  LAB: Revise this so the receiver is *REQUIRED* to hold a permission
        //  before the broadcast will be sent to it.
        Log.d(TAG, "Sending custom ACTION_TICK");
        sendBroadcast(repIntent);
    }
}
