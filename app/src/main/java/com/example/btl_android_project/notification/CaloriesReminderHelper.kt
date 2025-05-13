package com.example.btl_android_project.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.util.Calendar
import javax.inject.Inject

//class CaloriesReminderHelper {
//    companion object {
//        private const val CHANNEL_ID = "calories_reminder_channel"
//        private const val CHANNEL_NAME = "Calories Reminder"
//        private const val CHANNEL_DESC = "Channel for calories reminder notifications"
//
//        private const val MORNING_NOTIFICATION_ID = 1
//        private const val NOON_NOTIFICATION_ID = 2
//        private const val EVENING_NOTIFICATION_ID = 3
//
//        private const val MORNING_REMINDER_CODE = 101
//        private const val NOON_REMINDER_CODE = 102
//        private const val EVENING_REMINDER_CODE = 103
//
//        fun setupReminderNotifications(context: Context) {
//            createNotificationChannel(context)
//            setupMorningReminder(context)
//            setupNoonReminder(context)
//            setupEveningReminder(context)
//        }
//
//        private fun createNotificationChannel(context: Context) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val importance = NotificationManager.IMPORTANCE_HIGH
//                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
//                    description = CHANNEL_DESC
//                }
//
//                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                notificationManager.createNotificationChannel(channel)
//            }
//        }
//
//
//        private fun setupMorningReminder(context: Context) {
//            val intent = Intent(context, CaloriesReminderReceiver::class.java).apply {
//                putExtra("notification_id", MORNING_NOTIFICATION_ID)
//                putExtra("title", "Breakfast")
//                putExtra("message", "Don't forget to log your breakfast calories!")
//            }
//
//            val pendingIntent = PendingIntent.getBroadcast(
//                context,
//                MORNING_REMINDER_CODE,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//
//            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            val calendar = Calendar.getInstance().apply {
//                set(Calendar.HOUR_OF_DAY, 7)
//                set(Calendar.MINUTE, 0)
//                set(Calendar.SECOND, 0)
//
//                if (timeInMillis <= System.currentTimeMillis()) {
//                    add(Calendar.DAY_OF_YEAR, 1)
//                }
//            }
//
//            alarmManager.setRepeating(
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                AlarmManager.INTERVAL_DAY,
//                pendingIntent
//            )
//        }
//
//
//        private fun setupNoonReminder(context: Context) {
//            val intent = Intent(context, CaloriesReminderReceiver::class.java).apply {
//                putExtra("notification_id", NOON_NOTIFICATION_ID)
//                putExtra("title", "Lunch")
//                putExtra("message", "Don't forget to log your lunch calories!")
//            }
//
//            val pendingIntent = PendingIntent.getBroadcast(
//                context,
//                NOON_REMINDER_CODE,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//
//            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            val calendar = Calendar.getInstance().apply {
//                set(Calendar.HOUR_OF_DAY, 12)
//                set(Calendar.MINUTE, 0)
//                set(Calendar.SECOND, 0)
//
//                if (timeInMillis <= System.currentTimeMillis()) {
//                    add(Calendar.DAY_OF_YEAR, 1)
//                }
//            }
//
//            alarmManager.setRepeating(
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                AlarmManager.INTERVAL_DAY,
//                pendingIntent
//            )
//        }
//
//        private fun setupEveningReminder(context: Context) {
//            val intent = Intent(context, CaloriesReminderReceiver::class.java).apply {
//                putExtra("notification_id", EVENING_NOTIFICATION_ID)
//                putExtra("title", "Dinner")
//                putExtra("message", "Don't forget to log your dinner calories!")
//            }
//
//            val pendingIntent = PendingIntent.getBroadcast(
//                context,
//                EVENING_REMINDER_CODE,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//
//            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            val calendar = Calendar.getInstance().apply {
//                set(Calendar.HOUR_OF_DAY, 17)
//                set(Calendar.MINUTE, 0)
//                set(Calendar.SECOND, 0)
//
//                if (timeInMillis <= System.currentTimeMillis()) {
//                    add(Calendar.DAY_OF_YEAR, 1)
//                }
//            }
//
//            alarmManager.setRepeating(
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                AlarmManager.INTERVAL_DAY,
//                pendingIntent
//            )
//        }
//
//        fun cancelAllReminders(context: Context) {
//            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//            val morningIntent = Intent(context, CaloriesReminderReceiver::class.java)
//            val morningPendingIntent = PendingIntent.getBroadcast(
//                context,
//                MORNING_REMINDER_CODE,
//                morningIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//            alarmManager.cancel(morningPendingIntent)
//
//            val noonIntent = Intent(context, CaloriesReminderReceiver::class.java)
//            val noonPendingIntent = PendingIntent.getBroadcast(
//                context,
//                NOON_REMINDER_CODE,
//                noonIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//            alarmManager.cancel(noonPendingIntent)
//
//            val eveningIntent = Intent(context, CaloriesReminderReceiver::class.java)
//            val eveningPendingIntent = PendingIntent.getBroadcast(
//                context,
//                EVENING_REMINDER_CODE,
//                eveningIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//            alarmManager.cancel(eveningPendingIntent)
//        }
//    }
//}

class CaloriesReminderHelper @Inject constructor() {
    companion object {
        private const val CHANNEL_ID = "calories_reminder_channel"
        private const val CHANNEL_NAME = "Calories Reminder"
        private const val CHANNEL_DESC = "Channel for calories reminder notifications"

        private const val MORNING_NOTIFICATION_ID = 1
        private const val NOON_NOTIFICATION_ID = 2
        private const val EVENING_NOTIFICATION_ID = 3

        private const val MORNING_REMINDER_CODE = 101
        private const val NOON_REMINDER_CODE = 102
        private const val EVENING_REMINDER_CODE = 103
    }

    fun setupReminderNotifications(context: Context) {
        createNotificationChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("CaloriesReminderHelper", "Notification permission not granted")
                return
            }
        }

        setupMorningReminder(context)
        setupNoonReminder(context)
        setupEveningReminder(context)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setupMorningReminder(context: Context) {
        val intent = Intent(context, CaloriesReminderReceiver::class.java).apply {
            action = "MORNING_REMINDER_ACTION"
            putExtra("notification_id", MORNING_NOTIFICATION_ID)
            putExtra("title", "Breakfast")
            putExtra("message", "Don't forget to log your breakfast calories!")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            MORNING_REMINDER_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        Log.d("CaloriesReminderHelper", "Morning reminder set for ${calendar.time}")
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setupNoonReminder(context: Context) {
        val intent = Intent(context, CaloriesReminderReceiver::class.java).apply {
            action = "NOON_REMINDER_ACTION"
            putExtra("notification_id", NOON_NOTIFICATION_ID)
            putExtra("title", "Lunch")
            putExtra("message", "Don't forget to log your lunch calories!")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOON_REMINDER_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        Log.d("CaloriesReminderHelper", "Noon reminder set for ${calendar.time}")
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setupEveningReminder(context: Context) {
        val intent = Intent(context, CaloriesReminderReceiver::class.java).apply {
            action = "EVENING_REMINDER_ACTION"
            putExtra("notification_id", EVENING_NOTIFICATION_ID)
            putExtra("title", "Dinner")
            putExtra("message", "Don't forget to log your dinner calories!")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            EVENING_REMINDER_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 17)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        Log.d("CaloriesReminderHelper", "Evening reminder set for ${calendar.time}")
    }

    fun cancelAllReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val morningIntent = Intent(context, CaloriesReminderReceiver::class.java).apply {
            action = "MORNING_REMINDER_ACTION"
        }
        val morningPendingIntent = PendingIntent.getBroadcast(
            context,
            MORNING_REMINDER_CODE,
            morningIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(morningPendingIntent)
        morningPendingIntent.cancel()

        val noonIntent = Intent(context, CaloriesReminderReceiver::class.java).apply {
            action = "NOON_REMINDER_ACTION"
        }
        val noonPendingIntent = PendingIntent.getBroadcast(
            context,
            NOON_REMINDER_CODE,
            noonIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(noonPendingIntent)
        noonPendingIntent.cancel()

        val eveningIntent = Intent(context, CaloriesReminderReceiver::class.java).apply {
            action = "EVENING_REMINDER_ACTION"
        }
        val eveningPendingIntent = PendingIntent.getBroadcast(
            context,
            EVENING_REMINDER_CODE,
            eveningIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(eveningPendingIntent)
        eveningPendingIntent.cancel()

        Log.d("CaloriesReminderHelper", "All reminders cancelled")
    }
}