package com.thenewcircle.timenotifierclient;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class TimeUpdateTickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //  LAB: Verify this onReceive is NOT called after the Service
        //  is updated to require a permission to receive its broadcast

        //  Send a new Intent to our activity with the TOD in milliseconds
        //  attached as an extra.
        Intent actIntent = new Intent(TimeUpdateActivity.ACTION_TOD_UPDATE);
        actIntent.putExtra(TimeUpdateActivity.EXTRA_TOD_MS, System.currentTimeMillis());
        actIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName(
                TimeUpdateActivity.class.getPackage().getName(),
                TimeUpdateActivity.class.getName());
        actIntent.setComponent(cn);
        context.startActivity(actIntent);
    }
}
