package com.team.personalschedule_xml.retrofit

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.team.personalschedule_xml.data.remote.HolidayService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GetHolidays {

    val thisYear = CalendarDay.today().year
    val threeYear = arrayOf(thisYear -1, thisYear, thisYear + 1)

    fun getHolidays() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val holidayService = retrofit.create(HolidayService::class.java)
    }
}