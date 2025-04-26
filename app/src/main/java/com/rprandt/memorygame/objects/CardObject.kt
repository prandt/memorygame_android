package com.rprandt.memorygame.objects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.MotionEvent
import com.rprandt.memorygame.MainActivity
import com.rprandt.memorygame.interfaces.IObject
import com.rprandt.memorygame.services.SoundService


class CardObject(
    private var image: Bitmap,
    private val x: Int,
    private val y: Int,
    mainScene: MainActivity.MainScene,
): IObject {

    private val with = mainScene.width/4
    private val height = mainScene.width/4
    private val soundService = SoundService(mainScene.context)

    fun setImage(newImage: Bitmap) {
        image = newImage
    }

    override fun render(canvas: Canvas) {
        canvas.drawBitmap(image, x.toFloat(), y.toFloat(), null)
    }

    override fun onTouch(e: MotionEvent): Boolean {
        val touchX = e.x
        val touchY = e.y

        return when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                if (touchX >= x && touchX <= x + with && touchY >= y && touchY <= y + height) {
                    // Play sound when card is touched
                    soundService.playCardFlipSound()
                    // Handle card flip or selection logic here
                    true
                } else {
                    false
                }
            }
            MotionEvent.ACTION_UP -> {
                // Handle touch release if needed
                false
            }
            else -> false
        }
    }
}