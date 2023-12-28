package com.johnnyelachi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.johnnyelachi.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import me.leolin.shortcutbadger.ShortcutBadger

const val channelId = "notification_channel"
const val channelName = "com.example.myapplication"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
            incrementBadgeCount()
        }
    }


    fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews("com.example.myapplication", R.layout.notification)
        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.eezeechatapplogo)
        return remoteView
    }

    fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.eezeechatapplogo) // Replace with a valid icon resource
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setContent(getRemoteView(title, message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(getNextNotificationId(), builder.build())
    }

    private fun getNextNotificationId(): Int {
        // Get the current notification ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val currentNotificationId = sharedPreferences.getInt("notification_id", 0)

        // Increment the notification ID for the next notification
        val nextNotificationId = currentNotificationId + 1

        // Store the updated notification ID back in SharedPreferences
        sharedPreferences.edit().putInt("notification_id", nextNotificationId).apply()

        return nextNotificationId
    }

    private fun incrementBadgeCount() {

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val currentBadgeCount = sharedPreferences.getInt("badge_count", 0)

        val nextBadgeCount = currentBadgeCount + 1

        sharedPreferences.edit().putInt("badge_count", nextBadgeCount).apply()

        ShortcutBadger.applyCount(applicationContext, nextBadgeCount)
    }
}
