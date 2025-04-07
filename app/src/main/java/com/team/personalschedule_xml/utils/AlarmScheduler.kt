package com.team.personalschedule_xml.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.utils.constants.AlarmConstants
import com.team.personalschedule_xml.utils.receiver.AlarmReceiver
import java.time.ZoneId

object AlarmScheduler {

    /**
     * 스케줄에 설정된 알림 조건에 따라 정확한 알람을 예약합니다.
     * - Android 12 이상에서는 SCHEDULE_EXACT_ALARM 권한이 필요한지 확인
     * - 스케줄에 설정된 알림 문자열을 파싱하여 알람 시간 계산
     * - 동일한 scheduleId + 조건 조합으로 PendingIntent 생성
     */
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarms(context: Context, scheduleId: Int, schedule: Schedule) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Android 12 이상: 정확한 알람 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.e("AlarmScheduler", "SCHEDULE_EXACT_ALARM permission not granted")
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = android.net.Uri.parse("package:${context.packageName}")
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            return
        }

        if (schedule.startDateTime == null) {
            Log.e("AlarmScheduler", "startDateTime is empty for schedule [$scheduleId]")
            return
        }

        val startDateTime = schedule.startDateTime

        // 알림 조건이 없거나 "알림 없음"이면 무시
        if (schedule.alarm.isBlank() || schedule.alarm == AlarmConstants.NO_ALARM) {
            Log.d("AlarmScheduler", "No alarms to set for schedule [$scheduleId]")
            return
        }

        // 알림 조건 파싱
        val alarmConditions = schedule.alarm.split(",").map { it.trim() }.filter { it.isNotBlank() }
        if (alarmConditions.isEmpty()) {
            Log.d("AlarmScheduler", "No valid alarm conditions for schedule [$scheduleId]")
            return
        }

        alarmConditions.forEach { condition ->
            val cleanedCondition = when {
                condition.contains(AlarmConstants.AT_START) -> AlarmConstants.AT_START
                condition.contains(AlarmConstants.TEN_MINUTES_BEFORE) -> AlarmConstants.TEN_MINUTES_BEFORE
                condition.contains(AlarmConstants.ONE_HOUR_BEFORE) -> AlarmConstants.ONE_HOUR_BEFORE
                else -> {
                    Log.d("AlarmScheduler", "Invalid alarm condition: $condition for schedule [$scheduleId]")
                    return@forEach
                }
            }

            // 조건별 알람 트리거 시간 계산
            val triggerTime = when (cleanedCondition) {
                AlarmConstants.AT_START -> startDateTime
                AlarmConstants.TEN_MINUTES_BEFORE -> startDateTime.minusMinutes(10)
                AlarmConstants.ONE_HOUR_BEFORE -> startDateTime.minusHours(1)
                else -> return@forEach
            }

            // 알람 인텐트 구성
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = "com.team.personalschedule_xml.ACTION_SCHEDULE_ALARM"
                putExtra("scheduleId", scheduleId)
                putExtra("title", schedule.title)
                putExtra("condition", condition)
            }

            // PendingIntent 구성
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                scheduleId + condition.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerMillis = triggerTime.toInstant(ZoneId.systemDefault().rules.getOffset(triggerTime)).toEpochMilli()

            // 현재 시간보다 이후인 경우만 알람 설정
            if (triggerMillis > System.currentTimeMillis()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
                Log.d("AlarmScheduler", "Alarm set for [$condition] at $triggerMillis for schedule [$scheduleId]")
            } else {
                Log.d("AlarmScheduler", "Alarm time ($triggerMillis) is in the past. Alarm skipped for schedule [$scheduleId]")
            }
        }
    }

    /**
     * 주어진 scheduleId 및 알림 조건을 기반으로 등록된 알람을 해제합니다.
     * - Schedule 객체가 있으면 해당 알림 조건에 해당하는 것만 해제
     * - 없으면 모든 조건(3가지)에 대해 해제
     */
    fun cancelAlarms(context: Context, scheduleId: Int, schedule: Schedule? = null) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val conditions = if (schedule != null && schedule.alarm.isNotBlank() && schedule.alarm != AlarmConstants.NO_ALARM) {
            schedule.alarm.split(",").map { it.trim() }.filter { it.isNotBlank() }.map { condition ->
                when {
                    condition.contains(AlarmConstants.AT_START) -> AlarmConstants.AT_START
                    condition.contains(AlarmConstants.TEN_MINUTES_BEFORE) -> AlarmConstants.TEN_MINUTES_BEFORE
                    condition.contains(AlarmConstants.ONE_HOUR_BEFORE) -> AlarmConstants.ONE_HOUR_BEFORE
                    else -> null
                }
            }.filterNotNull()
        } else {
            listOf(AlarmConstants.AT_START, AlarmConstants.TEN_MINUTES_BEFORE, AlarmConstants.ONE_HOUR_BEFORE)
        }

        // 조건별로 알람 취소
        conditions.forEach { condition ->
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                scheduleId + condition.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            Log.d("AlarmScheduler", "Alarm cancelled for condition [$condition] for schedule [$scheduleId]")
        }
    }
}