package com.team.personalschedule_xml.data.model

enum class ActionType(val message : String) {
    CREATED("일정을 작성했습니다."),
    UPDATED("일정을 수정했습니다."),
    DELETED("일정을 삭제했습니다."),
    COPIED("일정을 복사했습니다.");

    companion object {
        fun from(name : String) : ActionType = valueOf(name)
    }
}