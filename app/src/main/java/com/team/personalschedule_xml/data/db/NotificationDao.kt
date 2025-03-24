package com.team.personalschedule_xml.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.team.personalschedule_xml.data.model.Notification

@Dao
interface NotificationDao {
    @Insert
    suspend fun insertNotification(notification: Notification)

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    suspend fun getAllNotifications() : List<Notification>

    @Query("DELETE FROM notifications WHERE id = :id ")
    suspend fun deleteNotification(id : Int)
}