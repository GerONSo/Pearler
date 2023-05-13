package com.geronso.pearler.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class BottomCropImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    override fun setFrame(frameLeft: Int, frameTop: Int, frameRight: Int, frameBottom: Int): Boolean {
        if (drawable == null) {
            return super.setFrame(frameLeft, frameTop, frameRight, frameBottom)
        }
        val frameWidth = (frameRight - frameLeft).toFloat()
        val frameHeight = (frameBottom - frameTop).toFloat()
        val originalImageWidth = drawable.intrinsicWidth.toFloat()
        val originalImageHeight = drawable.intrinsicHeight.toFloat()
        var usedScaleFactor = 1f
        if (frameWidth > originalImageWidth || frameHeight > originalImageHeight) {
            // If frame is bigger than image
            // => Crop it, keep aspect ratio and position it at the bottom and center horizontally
            val fitHorizontallyScaleFactor = frameWidth / originalImageWidth
            val fitVerticallyScaleFactor = frameHeight / originalImageHeight
            usedScaleFactor = Math.max(fitHorizontallyScaleFactor, fitVerticallyScaleFactor)
        }
        val newImageWidth = originalImageWidth * usedScaleFactor
        val newImageHeight = originalImageHeight * usedScaleFactor
        val matrix = imageMatrix
        matrix.setScale(usedScaleFactor, usedScaleFactor, 0f, 0f) // Replaces the old matrix completly
        //comment matrix.postTranslate if you want crop from TOP
        matrix.postTranslate((frameWidth - newImageWidth) / 2, frameHeight - newImageHeight)
        imageMatrix = matrix
        return super.setFrame(frameLeft, frameTop, frameRight, frameBottom)
    }

}