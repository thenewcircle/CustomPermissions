package com.thenewcircle.timenotifierclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;


public class TimeUpdateActivity extends Activity {
    protected static final String ACTION_TOD_UPDATE = "com.thenewcircle.timenotifier.ACTION_TOD";
    protected static final String EXTRA_TOD_MS = "time-update-tod-ms";

    private TextView statusText;
    private TextView timeText;
    private TimeUpdateTickReceiver mTimeUpdateTickReceiver = new TimeUpdateTickReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Inflate and setup the Activity's content view.
        setContentView(R.layout.activity_time_update);

        //  Get the instances of our status and time update text views
        statusText = findViewById(R.id.statusText);
        timeText = findViewById(R.id.timestampText);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register the Broadcast Receiver with the Service Intent Action
        IntentFilter intentFilter = new IntentFilter("com.thenewcircle.timenotifier.ACTION_TICK");
        registerReceiver(mTimeUpdateTickReceiver, intentFilter);

        //  LAB: Verify the Activity crashes after the Service is updated to
        //  require a permission to start/bind it.

        startTimeNotifierService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mTimeUpdateTickReceiver);
    }

    private void startTimeNotifierService() {
        statusText.setText(R.string.connecting);

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
        if (ACTION_TOD_UPDATE.equals(intent.getAction())) {
            long stamp = intent.getLongExtra(EXTRA_TOD_MS, -1);
            statusText.setText(R.string.update_received);
            timeText.setText(Long.toString(stamp));
        }
    }
}
