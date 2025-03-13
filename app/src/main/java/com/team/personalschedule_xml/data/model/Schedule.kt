package com.team.personalschedule_xml.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class Schedule (
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val title : String,
    val isAllDay : Boolean,
    val startDateTime : String,
    val endDateTime : String,
    val labelColor: Int,
    val labelName: String,
    val alarm : String,
    val memo : String?
)
