package com.team.personalschedule_xml.utils

import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@BindingAdapter("formatteDate")
fun setFormattedDate(textView : TextView, date : LocalDate?) {
    date?.let {
        val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.KOREAN)
        val formatted = it.format(formatter)
        Log.d("BindingAdapter", "Formatted date: $formatted")
        textView.text = it.format(formatter)
    }
}

@BindingAdapter("formattedTime")
fun setFormattedTime(textView: TextView, time: LocalTime?) {
    time?.let {
        // 오전/오후를 표시
        val amPm = if (it.hour < 12) "오전" else "오후"
        val formatter = DateTimeFormatter.ofPattern("h:mm", Locale.KOREAN)
        textView.text = "$amPm ${it.format(formatter)}"
    }
}