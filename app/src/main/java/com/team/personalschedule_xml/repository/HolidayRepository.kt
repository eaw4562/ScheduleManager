package com.team.personalschedule_xml.repository

import android.util.Log
import com.team.personalschedule_xml.data.model.HolidayResponse
import com.team.personalschedule_xml.data.remote.HolidayService
import com.team.personalschedule_xml.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class HolidayRepository {

    private val holidayService: HolidayService
    private val serviceKey = BuildConfig.HolidaysKey

    init {
        // OkHttp 로깅 인터셉터 생성 (요청 및 응답의 상세 내용을 로그로 출력)
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // OkHttpClient에 로깅 인터셉터 추가
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        // Retrofit 인스턴스 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        holidayService = retrofit.create(HolidayService::class.java)
    }

    suspend fun getHolidays(year: Int): HolidayResponse? {
        return try {
            val response = holidayService.getHolidays(year, serviceKey)

            // 요청 URL 로깅
            Log.d("HolidayRepository", "Request URL: ${response.raw().request.url}")

            if (response.isSuccessful) {
                // 성공 응답의 내용을 로그에 출력
                Log.d("HolidayRepository", "Response: ${response.body()}")
                response.body()
            } else {
                // 에러 응답의 경우 에러 메시지 로그 출력
                val errorMsg = response.errorBody()?.string()
                Log.e("HolidayRepository", "API Error: $errorMsg")
                null
            }
        } catch (e: Exception) {
            // 예외 발생 시 에러 로그 출력
            Log.e("HolidayRepository", "Exception during API call", e)
            e.printStackTrace()
            null
        }
    }
}
