package com.team.personalschedule_xml.ui.settings

import com.team.personalschedule_xml.data.model.Schedule
import java.time.LocalDate

sealed class SearchListItem {
    data class Header(val date : LocalDate, val itemCount : Int) : SearchListItem()
    data class SearchItem(val schedule : Schedule) : SearchListItem()
}