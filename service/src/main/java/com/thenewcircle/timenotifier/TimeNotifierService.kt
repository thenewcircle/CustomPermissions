package com.thenewcircle.timenotifier

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log

class TimeNotifierService : Service() {
    private var timeReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Created")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID,
                    "Timer Notifier Service", NotificationManager.IMPORTANCE_DEFAULT)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)

            val notification = Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build()

            startForeground(2, notification)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.d(TAG, "Received cmd Intent: Action = " + intent.action!!)

        //  Create our internal BR for time ticks
        if (timeReceiver == null) {
            Log.d(TAG, "Creating TICK receiver")

            timeReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    //  Verify this is the ACTION_TIME_TICK broadcast then turn
                    //  around and send our own Intent
                    Log.d(TAG, "timeReceiver.onReceive for ACTION = " + intent.action!!)

                    if (Intent.ACTION_TIME_TICK == intent.action) {
                        sendCustomTick()
                    }
                }
            }

            val filter = IntentFilter(Intent.ACTION_TIME_TICK)
            registerReceiver(timeReceiver, filter)
            Log.d(TAG, "Registered receiver with filter: " + timeReceiver!!.toString() +
                    ", " + filter.toString())
        }

        //  For new registrations (starts), automatically send a custom
        //  TICK so that clients immediately get "connected"
        sendCustomTick()

        //  Return a value of STICKY indicating that the system should restart
        //  us if we were killed at some point due to memory pressure.
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "Destroyed!")
        unregisterReceiver(timeReceiver)
        timeReceiver = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        //  We do not support bind, just start/stop via Intent
        return null
    }

    private fun sendCustomTick() {
        val repIntent = Intent("com.thenewcircle.timenotifier.ACTION_TICK")

        //  LAB: Revise this so the receiver is *REQUIRED* to hold a permission
        //  before the broadcast will be sent to it.
        Log.d(TAG, "Sending custom ACTION_TICK")
        sendBroadcast(repIntent, "com.thenewcircle.timenotifier.RECEIVE_NOTIFIER_TICK")
    }

    companion object {
        private val TAG = TimeNotifierService::class.java.name
        const val CHANNEL_ID = "channel_01"
    }
}
