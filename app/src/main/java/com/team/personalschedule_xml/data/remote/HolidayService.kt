package com.team.personalschedule_xml.data.remote

import com.team.personalschedule_xml.data.model.HolidayResponse
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Query

interface HolidayService {
    @GET("getRestDeInfo")
    suspend fun getHolidays(
        @Query("solYear") year: Int,
        @Query(value = "ServiceKey", encoded = true) serviceKey : String,
        @Query("_type") type: String = "json",
        @Query("numOfRows") numOfRows: Int = 100
    ): Response<HolidayResponse>
}