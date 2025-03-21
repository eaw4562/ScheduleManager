package com.team.personalschedule_xml.ui.common.calendar

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

class WhiteCircleDrawable : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }
    override fun draw(p0: Canvas) {
        val radius = Math.min(bounds.width(), bounds.height()) / 2f
        val centerX = bounds.centerX().toFloat()
        val centerY = bounds.centerY().toFloat()
        p0.drawCircle(centerX, centerY, radius, paint)
    }

    override fun setAlpha(p0: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getConstantState(): ConstantState {
        return object : ConstantState() {
            override fun newDrawable(): Drawable = WhiteCircleDrawable()

            override fun newDrawable(res: Resources?): Drawable = WhiteCircleDrawable()

            override fun getChangingConfigurations(): Int = 0
        }
    }
}