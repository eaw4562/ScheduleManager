package com.team.personalschedule_xml.data.repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.team.personalschedule_xml.data.db.ScheduleDatabase
import com.team.personalschedule_xml.data.model.ActionType
import com.team.personalschedule_xml.data.model.Notification
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.utils.AlarmScheduler
import com.team.personalschedule_xml.utils.constants.AlarmConstants

class ScheduleRepository(private val context : Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        ScheduleDatabase::class.java,
        "schedule_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    private val scheduleDao = db.scheduleDao()
    private val notificationDao = db.notificationDao()

    suspend fun getAllNotifications(): List<Notification> {
        return notificationDao.getAllNotifications()
    }

    fun getAllSchedulesLive() : LiveData<List<Schedule>> = scheduleDao.getAllSchedulesLive()
    suspend fun getAllSchedulesOrdered() : List<Schedule> = scheduleDao.getAllSchedulesOrdered()

    suspend fun insertSchedule(schedule: Schedule, actionType: ActionType = ActionType.CREATED): Long {
        val id = scheduleDao.insertSchedule(schedule)

        notificationDao.insertNotification(
            Notification(
                scheduleId = id.toInt(),
                title =  schedule.title,
                labelColor = schedule.labelColor,
                startDateTime = schedule.startDateTime,
                endDateTime = schedule.endDateTime,
                actionType = actionType
            )
        )

        if (schedule.alarm.isNotBlank() && schedule.alarm != AlarmConstants.NO_ALARM) {
            //scheduleAlarms(id.toInt(), schedule) // 명확히 조건 추가
            AlarmScheduler.scheduleAlarms(context, id.toInt(), schedule)
        } else {
            Log.d("ScheduleRepository", "No alarms to set for inserted schedule [$id]")
        }
        return id
    }

    suspend fun updateSchedule(schedule: Schedule, actionType: ActionType = ActionType.UPDATED) {
        scheduleDao.updateSchedule(schedule)

        //업데이트 전 기존 알림 취소
        //cancelAlarms(schedule.id)
        AlarmScheduler.cancelAlarms(context, schedule.id)

        notificationDao.insertNotification(
            Notification(
                scheduleId = schedule.id,
                title =  schedule.title,
                labelColor = schedule.labelColor,
                startDateTime = schedule.startDateTime,
                endDateTime = schedule.endDateTime,
                actionType = actionType
            )
        )

        if (schedule.alarm.isNotBlank() && schedule.alarm != AlarmConstants.NO_ALARM) {
            //scheduleAlarms(schedule.id, schedule)
            AlarmScheduler.scheduleAlarms(context, schedule.id, schedule)

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
        //cancelAlarms(id) // 일정 삭제 시 알림 취소
        AlarmScheduler.cancelAlarms(context, id)
    }

    fun searchSchedules(keyword : String) : LiveData<List<Schedule>> {
        return scheduleDao.searchSchedules(keyword)
    }
}