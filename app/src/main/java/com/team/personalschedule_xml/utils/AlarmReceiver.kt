package com.team.personalschedule_xml.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.team.personalschedule_xml.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver", "Alarm received")
        val scheduleId = intent?.getIntExtra("scheduleId", -1) ?: -1
        val title = intent?.getStringExtra("title") ?: "일정 알림"
        val condition = intent?.getStringExtra("condition") ?: "START"

        val message = when (condition) {
            "시작" -> "$title 시작 시간입니다."
            "10분 전" -> "$title 시작 10분 전입니다."
            "1시간 전" -> "$title 시작 1시간 전입니다."
            else -> "$title 알림"
        }

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (notificationManager == null || context == null) {
            return // context 또는 notificationManager가 null이면 종료
        }

        val channelId = "schedule_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Schedule Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle("일정 알림")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(scheduleId, notification)
    }
}