package com.example.carguide

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Custom View that draws a semi-transparent guide overlay on top of the camera
 * preview.
 *
 * The overlay consists of:
 *   - A semi-transparent filled rectangle (the "frame") that the car should fill.
 *   - A bright dashed border around the frame.
 *   - A center crosshair to help align the car.
 *   - Corner brackets for a "viewfinder" look.
 *
 * The size of the frame is driven by the selected [CarType].
 */
class GuideOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /** Currently active template. Changing it triggers a redraw. */
    var carType: CarType = CarType.BERLINE
        set(value) {
            field = value
            invalidate()
        }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2200E5FF")
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CC00E5FF")
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    private val crossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CCFFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val outsidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#55000000") // darken area outside frame
        style = Paint.Style.FILL
    }

    private val cornerLen = 48f
    private val cornerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFD54F") // amber corner brackets
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return

        // The camera preview uses 4:3 aspect ratio with fitCenter, so on a
        // widescreen display the preview doesn't fill the entire screen.
        // We compute the actual preview area so the overlay stays inside it.
        val previewW: Float
        val previewH: Float
        if (width.toFloat() / height > 4f / 3f) {
            // Screen is wider than 4:3 -> preview is height-constrained
            previewH = height.toFloat()
            previewW = previewH * 4f / 3f
        } else {
            // Screen is taller than 4:3 -> preview is width-constrained
            previewW = width.toFloat()
            previewH = previewW * 3f / 4f
        }
        val previewLeft = (width - previewW) / 2f
        val previewTop = (height - previewH) / 2f

        // Guide frame dimensions, relative to the preview area (not the screen).
        val fw = previewW * carType.widthRatio
        val fh = previewH * carType.heightRatio
        val left = previewLeft + (previewW - fw) / 2f
        val top  = previewTop + (previewH - fh) / 2f
        val rect = RectF(left, top, left + fw, top + fh)

        // 1. Darken everything outside the frame.
        // Top strip (full screen width, from top to frame top).
        canvas.drawRect(0f, 0f, width.toFloat(), top, outsidePaint)
        // Bottom strip.
        canvas.drawRect(0f, top + fh, width.toFloat(), height.toFloat(), outsidePaint)
        // Left strip.
        canvas.drawRect(0f, top, left, top + fh, outsidePaint)
        // Right strip.
        canvas.drawRect(left + fw, top, width.toFloat(), top + fh, outsidePaint)

        // 2. Semi-transparent fill inside the frame.
        canvas.drawRect(rect, fillPaint)

        // 3. Bright border.
        canvas.drawRect(rect, borderPaint)

        // 4. Center crosshair (center of the preview area, not the screen).
        val cx = previewLeft + previewW / 2f
        val cy = previewTop + previewH / 2f
        val arm = 40f
        canvas.drawLine(cx - arm, cy, cx + arm, cy, crossPaint)
        canvas.drawLine(cx, cy - arm, cx, cy + arm, crossPaint)

        // 5. Corner brackets for a viewfinder feel.
        drawCorners(canvas, rect)

        // 6. Label inside frame (top-left).
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFFFFFFF")
            textSize = 36f
        }
        canvas.drawText(carType.label, left + 16f, top + 44f, labelPaint)
    }

    private fun drawCorners(canvas: Canvas, r: RectF) {
        // top-left
        canvas.drawLine(r.left, r.top, r.left + cornerLen, r.top, cornerPaint)
        canvas.drawLine(r.left, r.top, r.left, r.top + cornerLen, cornerPaint)
        // top-right
        canvas.drawLine(r.right, r.top, r.right - cornerLen, r.top, cornerPaint)
        canvas.drawLine(r.right, r.top, r.right, r.top + cornerLen, cornerPaint)
        // bottom-left
        canvas.drawLine(r.left, r.bottom, r.left + cornerLen, r.bottom, cornerPaint)
        canvas.drawLine(r.left, r.bottom, r.left, r.bottom - cornerLen, cornerPaint)
        // bottom-right
        canvas.drawLine(r.right, r.bottom, r.right - cornerLen, r.bottom, cornerPaint)
        canvas.drawLine(r.right, r.bottom, r.right, r.bottom - cornerLen, cornerPaint)
    }
}
