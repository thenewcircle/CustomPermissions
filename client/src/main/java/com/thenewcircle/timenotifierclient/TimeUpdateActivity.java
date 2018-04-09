package com.thenewcircle.timenotifierclient;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;


public class TimeUpdateActivity extends Activity {
    protected static final String ACTION_TOD_UPDATE = "com.thenewcircle.timenotifier.ACTION_TOD";
    protected static final String EXTRA_TOD_MS = "time-update-tod-ms";
    private static final String PERMISSION_USE_TIME_NOTIFIER = "com.thenewcircle.timenotifier.USE_TIME_NOTIFIER";
    private static final int PERMISSION_REQUEST_CODE = 123;

    private TextView mStatusText;
    private TextView mTimeText;
    private TimeUpdateTickReceiver mTimeUpdateTickReceiver = new TimeUpdateTickReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Inflate and setup the Activity's content view.
        setContentView(R.layout.activity_time_update);

        // Register the Broadcast Receiver with the Service Intent Action
        IntentFilter intentFilter = new IntentFilter("com.thenewcircle.timenotifier.ACTION_TICK");
        registerReceiver(mTimeUpdateTickReceiver, intentFilter);

        //  Get the instances of our status and time update text views
        mStatusText = (TextView) findViewById(R.id.status_info);
        mTimeText = (TextView) findViewById(R.id.timestamp);

        //  On MAIN intents, try to start the service just in case it
        //  is not already up and running.
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MAIN)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (checkSelfPermission(PERMISSION_USE_TIME_NOTIFIER)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission
                    requestPermissions(new String[]{PERMISSION_USE_TIME_NOTIFIER},
                            PERMISSION_REQUEST_CODE);
                } else {
                    // Permission has already been granted
                    startTimeNotifierService();
                }
            } else {
                startTimeNotifierService();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mTimeUpdateTickReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTimeNotifierService();
                } else {
                    mStatusText.setText(R.string.permission_request_cancelled);
                }
            }
        }
    }

    private void startTimeNotifierService() {
        mStatusText.setText(R.string.connecting);

        Intent srvIntent = new Intent(Intent.ACTION_MAIN);
        ComponentName srvComp = new ComponentName(
                "com.thenewcircle.timenotifier",
                "com.thenewcircle.timenotifier.TimeNotifierService");
        srvIntent.setComponent(srvComp);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(srvIntent);
        } else {
            startService(srvIntent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //  For UPDATE intents, update the text views with updated information.
        if (intent.getAction().equals(ACTION_TOD_UPDATE)) {
            long stamp = intent.getLongExtra(EXTRA_TOD_MS, -1);
            mStatusText.setText(R.string.update_received);
            mTimeText.setText(Long.toString(stamp));
        }
    }
}
