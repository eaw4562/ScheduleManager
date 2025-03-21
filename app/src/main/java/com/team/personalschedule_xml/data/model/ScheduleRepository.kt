package com.team.personalschedule_xml.data.model

import android.content.Context
import androidx.room.Room
import com.team.personalschedule_xml.data.db.ScheduleDatabase

class ScheduleRepository(context : Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        ScheduleDatabase::class.java,
        "schedule_database"
    ).build()

    private val scheduleDao = db.scheduleDao()

    suspend fun insertSchedule(schedule: Schedule): Long {
        return scheduleDao.insertSchedule(schedule)
    }

    suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.updateSchedule(schedule
        )
    }

    suspend fun getAllSchedules(): List<Schedule> {
        return scheduleDao.getAllSchedules()
    }
}