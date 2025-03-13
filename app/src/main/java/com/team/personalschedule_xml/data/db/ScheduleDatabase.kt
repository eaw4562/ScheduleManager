package com.team.personalschedule_xml.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.data.model.ScheduleDao

@Database(entities = [Schedule::class], version = 1)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao() : ScheduleDao
}