package com.example.waterreminderapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class WaterReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Check for POST_NOTIFICATIONS permission if the device is running Android 6.0 (API 23) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

                // Show notification when alarm triggers
                val channelId = "water_reminder_channel"
                val notificationId = 1

                // Create Notification Channel for Android 8.0 and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name = "Water Reminder"
                    val descriptionText = "Time to drink water!"
                    val importance = NotificationManager.IMPORTANCE_HIGH
                    val channel = NotificationChannel(channelId, name, importance).apply {
                        description = descriptionText
                    }

                    val notificationManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)
                }

                // Build and show the notification
                val builder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Water Reminder")
                    .setContentText("Don't forget to drink water!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                with(NotificationManagerCompat.from(context)) {
                    notify(notificationId, builder.build())
                }

            } else {
                // Handle case when permission is not granted, if needed
                // You could log a message, or handle it another way if necessary
            }
        } else {
            // If the device is running below API 23, no need for permission check, as it's not required
            // Proceed with sending the notification without the permission check
            val channelId = "water_reminder_channel"
            val notificationId = 1

            // Create Notification Channel for Android 8.0 and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Water Reminder"
                val descriptionText = "Time to drink water!"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }

                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            // Build and show the notification
            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Water Reminder")
                .setContentText("Don't forget to drink water!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        }
    }
}