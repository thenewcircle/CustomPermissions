package com.thenewcircle.timenotifierclient

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView


class TimeUpdateActivity : AppCompatActivity() {

    private var mStatusText: TextView? = null
    private var mTimeText: TextView? = null
    private val mTimeUpdateTickReceiver = TimeUpdateTickReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  Inflate and setup the Activity's content view.
        setContentView(R.layout.activity_time_update)

        //  Get the instances of our status and time update text views
        mStatusText = findViewById(R.id.status_info)
        mTimeText = findViewById(R.id.timestamp)
    }

    override fun onResume() {
        super.onResume()

        // Register the Broadcast Receiver with the Service Intent Action
        val intentFilter = IntentFilter("com.thenewcircle.timenotifier.ACTION_TICK")
        registerReceiver(mTimeUpdateTickReceiver, intentFilter)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(PERMISSION_USE_TIME_NOTIFIER) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                requestPermissions(arrayOf(PERMISSION_USE_TIME_NOTIFIER),
                        PERMISSION_USE_NOTIFIER_REQUEST_CODE)
            } else {
                // Permission has already been granted
                startTimeNotifierService()
            }
        } else {
            startTimeNotifierService()
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mTimeUpdateTickReceiver)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_USE_NOTIFIER_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTimeNotifierService()
                } else {
                    mStatusText!!.setText(R.string.permission_request_cancelled)
                }
            }
        }
    }

    private fun startTimeNotifierService() {
        mStatusText!!.setText(R.string.connecting)

        val srvIntent = Intent(Intent.ACTION_MAIN)
        val srvComp = ComponentName(
                "com.thenewcircle.timenotifier",
                "com.thenewcircle.timenotifier.TimeNotifierService")
        srvIntent.component = srvComp

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(srvIntent)
        } else {
            startService(srvIntent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        //  For UPDATE intents, update the text views with updated information.
        if (ACTION_TOD_UPDATE == intent.action) {
            val stamp = intent.getLongExtra(EXTRA_TOD_MS, -1)
            mStatusText!!.setText(R.string.update_received)
            mTimeText!!.text = java.lang.Long.toString(stamp)
        }
    }

    companion object {
        const val ACTION_TOD_UPDATE = "com.thenewcircle.timenotifier.ACTION_TOD"
        const val EXTRA_TOD_MS = "time-update-tod-ms"
        private const val PERMISSION_USE_TIME_NOTIFIER = "com.thenewcircle.timenotifier.USE_TIME_NOTIFIER"
        private const val PERMISSION_USE_NOTIFIER_REQUEST_CODE = 123
    }
}
