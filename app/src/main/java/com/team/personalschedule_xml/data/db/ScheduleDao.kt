package com.team.personalschedule_xml.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.team.personalschedule_xml.data.model.Schedule

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule): Long

    @Query("SELECT * FROM SCHEDULE")
    suspend fun getAllSchedules() : List<Schedule>

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Query("SELECT * FROM SCHEDULE WHERE id = :id")
    suspend fun getScheduleById(id: Int): Schedule?

    @Query("DELETE FROM SCHEDULE WHERE id = :id")
    suspend fun deleteSchedule(id: Int)

    @Query("SELECT * FROM schedule ORDER BY startDateTime ASC")
    fun getAllSchedulesLive(): LiveData<List<Schedule>>

    @Query("SELECT * FROM schedule ORDER BY startDateTime ASC")
    suspend fun getAllSchedulesOrdered(): List<Schedule>

    @Query("SELECT * FROM schedule WHERE title LIKE '%' || :keyword || '%' ORDER BY startDateTime ASC")
    fun searchSchedules(keyword : String) : LiveData<List<Schedule>>
}