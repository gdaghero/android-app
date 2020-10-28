package com.piso12.indoorex.fragments.search.stores

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.graphics.drawable.toBitmap
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.piso12.indoorex.R

class MapView(context: Context?, attr: AttributeSet?) : SubsamplingScaleImageView(context, attr) {

    private val paint: Paint = Paint()
    private var pinBitmap: Bitmap
    private lateinit var pin: PointF

    init {
        val drawable = context?.getDrawable(R.drawable.ic_room_24px)!!
        drawable.setTint(Color.RED)
        var bitmap = drawable.toBitmap(160, 160)
        val density = resources.displayMetrics.densityDpi.toFloat()
        val width = density / 1000f * bitmap.width
        val height = density / 1000f * bitmap.height
        pinBitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), true)
        maxScale = 10f
    }

    fun add(point: PointF) {
        pin = point
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isReady)
            return
        paint.isAntiAlias = true
        drawPin(canvas, pin, pinBitmap)
    }

    private fun drawPin(canvas: Canvas, point: PointF, icon: Bitmap) {
        sourceToViewCoord(point)?.let {
            canvas.drawBitmap(icon, it.x - pinBitmap.width / 2, it.y - icon!!.height / 2, paint)
        }
    }
}
