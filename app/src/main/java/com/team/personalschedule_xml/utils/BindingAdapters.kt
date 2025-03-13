package com.team.personalschedule_xml.utils

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.team.personalschedule_xml.data.model.CalendarLabel
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
