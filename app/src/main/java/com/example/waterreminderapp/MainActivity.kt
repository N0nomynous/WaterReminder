package com.example.waterreminderapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    private val channelId = "water_reminder_channel"

    private lateinit var addWaterButton: Button
    private lateinit var waterProgressBar: ProgressBar
    private lateinit var progressText: TextView

    private var currentWaterIntake = 0
    private val dailyGoal = 2000  // in milliliters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        scheduleReminder()

        // Check for POST_NOTIFICATIONS permission if the device is running Android 13 (API 33) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        // Connect UI elements
        addWaterButton = findViewById(R.id.addWaterButton)
        waterProgressBar = findViewById(R.id.waterProgressBar)
        progressText = findViewById(R.id.progressText)

        // Restore saved state
        if (savedInstanceState != null) {
            currentWaterIntake = savedInstanceState.getInt("currentWaterIntake")
            updateUI()
        }

        // Set up button click
        addWaterButton.setOnClickListener {
            if (currentWaterIntake < dailyGoal) {
                currentWaterIntake += 250
                if (currentWaterIntake > dailyGoal) currentWaterIntake = dailyGoal
                updateUI()
            }
        }
    }

    // checks whether the user granted permission to post notifications
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Permission Granted")
                .setContentText("You will now receive water reminders!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(this)) {
                notify(1001, builder.build())
            }
        }
    }

    // Save data when screen rotates
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentWaterIntake", currentWaterIntake)
    }

    // Update UI
    private fun updateUI() {
        waterProgressBar.progress = currentWaterIntake
        progressText.text = getString(R.string.water_progress, currentWaterIntake, dailyGoal)
    }

    // Creates a notification channel required for sending notifications on Android 8.0 (API 26) and above.
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Water Reminder"
            val descriptionText = "Channel for water reminder notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleReminder() {
        val intent = Intent(this, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intervalMillis = AlarmManager.INTERVAL_HOUR * 2  // every 2 hours

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis,
            pendingIntent
        )
    }
}