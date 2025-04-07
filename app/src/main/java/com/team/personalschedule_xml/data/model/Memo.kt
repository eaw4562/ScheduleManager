package com.team.personalschedule_xml.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
data class Memo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title : String,
    val labelColorResId : Int,
    val labelName : String,
    val memo : String,
    val createdAt: Long = System.currentTimeMillis()
)
