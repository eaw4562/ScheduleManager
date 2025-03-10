package com.team.personalschedule_xml.data.model

data class CalendarLabel(
    val color: Int,         // 예: 실제 색상 int 값 (Color.RED 등) 또는 색상 리소스
    val colorName: String,
    var isSelected: Boolean = false
)
