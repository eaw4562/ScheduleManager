package com.team.personalschedule_xml.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.style.ReplacementSpan

/**
 * CurrentDaySpan:
 * 날짜 텍스트 영역에 흰색 원을 그린 후, 날짜 텍스트를 그리는 Span.
 * 원은 날짜 텍스트의 너비를 기준으로 약간의 여백(padding)을 더해 그립니다.
 */
class CurrentDaySpan : ReplacementSpan() {

    override fun getSize(
        paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?
    ): Int {
        // 날짜 텍스트의 너비를 반환
        return paint.measureText(text, start, end).toInt()
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
        // 날짜 텍스트의 너비를 계산
        val textWidth = paint.measureText(text, start, end)
        // 폰트 메트릭스로 텍스트 높이 계산 (baseline 기준)
        val fm = paint.fontMetrics
        // 텍스트의 수평 중심 위치
        val centerX = x + textWidth / 2
        // 텍스트의 수직 중앙은 baseline y와 ascent, descent 값을 이용해 계산
        val centerY = y + (fm.descent + fm.ascent) / 2

        // 원의 반지름은 텍스트 너비의 절반에 약간의 패딩을 더함 (예: 4픽셀)
        val radius = textWidth / 2 + 10

        // 원을 그리기 위해 현재 paint의 색상, 스타일을 저장
        val originalColor = paint.color
        val originalStyle = paint.style

        // 흰색 원을 그리기
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, radius, paint)

        // 원 위에 날짜 텍스트 그리기
        paint.color = originalColor
        paint.style = originalStyle
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }
}
