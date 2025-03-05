package com.team.personalschedule_xml.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineHeightSpan
import android.text.style.ReplacementSpan


/**
 * BaseContentSpan:
 * - extraSpace: 날짜 텍스트 아래에 확보할 추가 영역의 높이(픽셀 단위)
 * 이 Span은 날짜 텍스트는 그대로 그리고, 추가 영역만 확보
 */
class BaseContentSpan(private val extraSpace: Int) : ReplacementSpan(), LineHeightSpan {
    override fun getSize(
        paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?
    ): Int {
        val textWidth = paint.measureText(text, start, end).toInt()
        fm?.let {
            it.descent += extraSpace
            it.bottom += extraSpace
        }
        return textWidth
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        // 기존 날짜 텍스트를 그대로 그림 (추가 영역은 빈 공간으로 남김)
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }

    override fun chooseHeight(
        text: CharSequence,
        start: Int,
        end: Int,
        spanstartv: Int,
        lineHeight: Int,
        fm: Paint.FontMetricsInt
    ) {
        fm.descent += extraSpace
        fm.bottom += extraSpace
    }
}