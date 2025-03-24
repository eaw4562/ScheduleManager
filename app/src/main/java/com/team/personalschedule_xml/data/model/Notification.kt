package com.team.personalschedule_xml.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val scheduleId : Int,
    val title : String,
    val labelColor: Int,
    val startDateTime: LocalDateTime?,
    val endDateTime: LocalDateTime?,
    val actionType: ActionType,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
