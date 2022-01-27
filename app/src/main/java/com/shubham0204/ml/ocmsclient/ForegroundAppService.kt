package com.shubham0204.ml.ocmsclient

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class ForegroundAppService() : Service() {

    private val handler = Handler( Looper.getMainLooper() )
    private lateinit var onScreenAppListener : OnScreenAppListener

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun scheduleCheck() {
        handler.postDelayed( runnable , 5000 )
    }

    private val runnable = Runnable() {
        onScreenAppListener.getForegroundApp()
        scheduleCheck()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel( "my-id" , "name", NotificationManager.IMPORTANCE_DEFAULT )
            val notificationManager = getSystemService( Context.NOTIFICATION_SERVICE ) as NotificationManager
            notificationManager.createNotificationChannel( notificationChannel )
            val notification: Notification = Notification.Builder(this, "my-id" )
                .setContentTitle( "Title" )
                .setContentText( "Message" )
                .setContentIntent(pendingIntent)
                .build()
            startForeground( 100 , notification )
        }
        else {
            startService( Intent( this, ForegroundAppService::class.java) )
        }


        onScreenAppListener = OnScreenAppListener( this )
        scheduleCheck()
        return START_STICKY
    }



}