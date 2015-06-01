package com.thenewcircle.timenotifierclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


public class TimeUpdateActivity extends Activity {
    protected static final String       ACTION_TOD_UPDATE = "com.thenewcircle.timenotifier.ACTION_TOD";
    protected static final String       EXTRA_TOD_MS = "time-update-tod-ms";

    private TextView            mStatusText;
    private TextView            mTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Inflate and setup the Activity's content view.
        setContentView(R.layout.activity_time_update);

        //  Get the instances of our status and time update text views
        mStatusText = (TextView) findViewById(R.id.status_info);
        mTimeText = (TextView) findViewById(R.id.timestamp);

        //  On MAIN intents, try to start the service just in case it
        //  is not already up and running.
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MAIN)) {
            mStatusText.setText(R.string.connecting);

            Intent srvIntent = new Intent(Intent.ACTION_MAIN);
            ComponentName srvComp =
                    new ComponentName("com.thenewcircle.timenotifier",
                                      "com.thenewcircle.timenotifier.TimeNotifierService");
            srvIntent.setComponent(srvComp);

            //  LAB: Verify the Activity crashes after the Service is updated to
            //  require a permission to start/bind it.
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
