package com.piso12.indoorex.animations

import android.graphics.Camera
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation

class FlipAnimation : Animation() {

    init {
        duration = ANIMATION_DELAY
        fillAfter = false
        interpolator = AccelerateDecelerateInterpolator()
    }

    private var camera: Camera? = null
    private var centerX = 0f
    private var centerY = 0f

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        centerX = width / 2.toFloat()
        centerY = height / 2.toFloat()
        camera = Camera()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val radians = Math.PI * interpolatedTime
        var degrees = (ROTATION_DEGREES * radians / Math.PI).toFloat()
        if (interpolatedTime >= HALF_INTERPOLATED_TIME) {
            degrees -= ROTATION_DEGREES
        }
        val matrix = t.matrix
        camera!!.save()
        camera!!.rotateY(degrees)
        camera!!.getMatrix(matrix)
        camera!!.restore()
        matrix.preTranslate(-centerX, -centerY)
        matrix.postTranslate(centerX, centerY)
    }

    private companion object {
        private const val ANIMATION_DELAY = 400L
        private const val ROTATION_DEGREES = 180f
        private const val HALF_INTERPOLATED_TIME = 0.5f
    }
}
