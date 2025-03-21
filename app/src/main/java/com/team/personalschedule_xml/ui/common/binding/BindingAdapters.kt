package com.team.personalschedule_xml.ui.common.binding

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.team.personalschedule_xml.data.model.CalendarLabel
import java.time.LocalDate
import java.time.LocalDateTime
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

@BindingAdapter("formattedDetailDate")
fun setFormattedDetailDate(textView: TextView, date: LocalDate?) {
    date?.let {
        val formatter = DateTimeFormatter.ofPattern("M월d일 EEEE", Locale.KOREAN)
        textView.text = it.format(formatter)
    }
}

@BindingAdapter("selectedLabelText")
fun setSelectedLabelText(textView: TextView, label: CalendarLabel?) {
    if (label != null) {
        textView.text = label.colorName
        // 예시로 배경색을 변경 (필요에 따라 수정)
        textView.setBackgroundColor(
            ContextCompat.getColor(textView.context, label.color)
        )    } else {
        textView.text = ""
        textView.background = null
    }
}

@BindingAdapter("visibleIfNotEmpty")
fun setVisibleIfNotEmpty(view: View, text: String?) {
    view.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
}

@BindingAdapter("textColorFromResource")
fun setTextColorFromResource(textView: TextView, colorRes: Int) {
    if (colorRes != 0) {
        textView.setTextColor(ContextCompat.getColor(textView.context, colorRes))
    } else {
        textView.setTextColor(Color.BLACK) // 기본값
    }
}


@BindingAdapter("formattedMoreDetailDate")
fun setFormattedMoreDetailDate(textView: TextView, dateTimeString: String?) {
    if (dateTimeString.isNullOrEmpty()) {
        textView.text = ""
        return
    }
    try {
        val localDateTime = LocalDateTime.parse(dateTimeString)
        val localDate = localDateTime.toLocalDate()
        // "yyyy. M. d (E)" 형식 예: "2025. 3. 19 (수)"
        val formatter = DateTimeFormatter.ofPattern("yyyy. M. d (E)", Locale.KOREAN)
        textView.text = localDate.format(formatter)
    } catch (e: Exception) {
        textView.text = ""
    }
}

@BindingAdapter("formattedMoreDetailTime")
fun setFormattedMoreDetailTime(textView: TextView, dateTimeString: String?) {
    if (dateTimeString.isNullOrEmpty()) {
        textView.text = ""
        return
    }
    try {
        val localDateTime = LocalDateTime.parse(dateTimeString)
        val localTime = localDateTime.toLocalTime()
        // "a h:mm" 형식 예: "오후 1:00"
        val formatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)
        textView.text = localTime.format(formatter)
    } catch (e: Exception) {
        textView.text = ""
    }
}