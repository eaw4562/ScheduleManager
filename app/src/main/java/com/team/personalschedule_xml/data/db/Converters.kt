package com.team.personalschedule_xml.data.db

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?) : String? =
        dateTime?.format(DateTimeFormatter.ISO_DATE_TIME)

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?) : LocalDateTime? =
        dateTimeString?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
}