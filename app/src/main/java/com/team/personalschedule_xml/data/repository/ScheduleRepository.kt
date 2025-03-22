package com.team.personalschedule_xml.data.repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Room
import com.team.personalschedule_xml.data.db.ScheduleDatabase
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.utils.constants.AlarmConstants
import com.team.personalschedule_xml.utils.receiver.AlarmReceiver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class ScheduleRepository(private val context : Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        ScheduleDatabase::class.java,
        "schedule_database"
    ).build()

    private val scheduleDao = db.scheduleDao()

    suspend fun insertSchedule(schedule: Schedule): Long {
        val id = scheduleDao.insertSchedule(schedule)

        if (schedule.alarm.isNotBlank() && schedule.alarm != AlarmConstants.NO_ALARM) {
            scheduleAlarms(id.toInt(), schedule) // 명확히 조건 추가
        } else {
            Log.d("ScheduleRepository", "No alarms to set for inserted schedule [$id]")
        }
        return id
    }

    suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.updateSchedule(schedule)

        //업데이트 전 기존 알림 취소
        cancelAlarms(schedule.id)

        if (schedule.alarm.isNotBlank() && schedule.alarm != AlarmConstants.NO_ALARM) {
            scheduleAlarms(schedule.id, schedule)
        } else {
            Log.d("ScheduleRepository", "No alarms to set for updated schedule [${schedule.id}]")
        }
    }

    suspend fun getAllSchedules(): List<Schedule> {
        return scheduleDao.getAllSchedules()
    }

    suspend fun getScheduleById(id: Int): Schedule? {
        return scheduleDao.getScheduleById(id)
    }

    suspend fun deleteSchedule(id: Int) {
        scheduleDao.deleteSchedule(id)
        cancelAlarms(id) // 일정 삭제 시 알림 취소
    }

    /**
     * 알림 설정을 위한 메서드
     */
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarms(scheduleId : Int, schedule: Schedule) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (schedule.startDateTime.isBlank()) {
            Log.e("ScheduleRepository", "startDateTime is empty")
            return
        }

        val startDateTime = if (schedule.startDateTime.contains("T")) {
            LocalDateTime.parse(schedule.startDateTime)
        } else {
            LocalDate.parse(schedule.startDateTime).atStartOfDay()
        }

        val alarmConditions = schedule.alarm.split(",").map { it.trim() }
        alarmConditions.forEach { condition ->
            val cleanedCondition = when {
                condition.contains(AlarmConstants.AT_START) -> AlarmConstants.AT_START
                condition.contains(AlarmConstants.TEN_MINUTES_BEFORE) -> AlarmConstants.TEN_MINUTES_BEFORE
                condition.contains(AlarmConstants.ONE_HOUR_BEFORE) -> AlarmConstants.ONE_HOUR_BEFORE
                else -> {
                    Log.d("ScheduleRepository", "Invalid alarm condition: $condition")
                    return@forEach
                }
            }

            val triggerTime = when (cleanedCondition) {
                AlarmConstants.AT_START -> startDateTime
                AlarmConstants.TEN_MINUTES_BEFORE -> startDateTime.minusMinutes(10)
                AlarmConstants.ONE_HOUR_BEFORE -> startDateTime.minusHours(1)
                else -> return@forEach
            }

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("scheduleId", scheduleId)
                putExtra("title", schedule.title)
                putExtra("condition", condition)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                scheduleId + condition.hashCode(), // 고유 requestCode 생성
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerMillis = triggerTime.toInstant(ZoneId.systemDefault().rules.getOffset(triggerTime)).toEpochMilli()

            if (triggerMillis > System.currentTimeMillis()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
                Log.d("ScheduleRepository", "Alarm set for [$condition] at $triggerMillis")
            } else {
                Log.d("ScheduleRepository", "Alarm time ($triggerMillis) is in the past. Alarm skipped.")
            }
        }
    }

    /**
     * 알림 취소 메서드
     */
    private fun cancelAlarms(scheduleId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val conditions = listOf(AlarmConstants.AT_START, AlarmConstants.TEN_MINUTES_BEFORE, AlarmConstants.ONE_HOUR_BEFORE)
        conditions.forEach { condition ->
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                scheduleId + condition.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            Log.d("ScheduleRepository", "Alarm cancelled for condition [$condition]")
        }
    }
}