package com.thenewcircle.timenotifierclient

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class TimeUpdateTickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //  LAB: Verify this onReceive is NOT called after the Service
        //  is updated to require a permission to receive its broadcast

        //  Send a new Intent to our activity with the TOD in milliseconds
        //  attached as an extra.
        val actIntent = Intent(TimeUpdateActivity.ACTION_TOD_UPDATE)
        actIntent.putExtra(TimeUpdateActivity.EXTRA_TOD_MS, System.currentTimeMillis())
        actIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val cn = ComponentName(
                TimeUpdateActivity::class.java.getPackage()!!.name,
                TimeUpdateActivity::class.java.name)
        actIntent.component = cn
        context.startActivity(actIntent)
    }
}
