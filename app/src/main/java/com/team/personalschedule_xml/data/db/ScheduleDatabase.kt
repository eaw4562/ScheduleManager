package com.team.personalschedule_xml.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.team.personalschedule_xml.data.model.Notification
import com.team.personalschedule_xml.data.model.Schedule

@Database(entities = [Schedule::class, Notification::class], version = 3)
@TypeConverters(Converters::class)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao() : ScheduleDao
    abstract fun notificationDao() : NotificationDao
}