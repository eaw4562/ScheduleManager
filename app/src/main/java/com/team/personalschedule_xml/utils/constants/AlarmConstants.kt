package com.team.personalschedule_xml.utils.constants

//알림 조건 상수 관리 객체
object AlarmConstants {
    const val NO_ALARM = "알림 없음"
    const val AT_START = "시작"
    const val TEN_MINUTES_BEFORE = "10분 전"
    const val ONE_HOUR_BEFORE = "1시간 전"

    val ALL_CONDITIONS = listOf(AT_START, TEN_MINUTES_BEFORE, ONE_HOUR_BEFORE)
}
