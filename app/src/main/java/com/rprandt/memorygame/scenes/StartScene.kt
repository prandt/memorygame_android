package com.rprandt.memorygame.scenes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import com.rprandt.memorygame.MainActivity
import com.rprandt.memorygame.interfaces.IScene

class StartScene(private val mainScene: MainActivity.MainScene) : IScene {

    override fun render(canvas: Canvas) {
        val text = "Toque para iniciar..."
        canvas.drawText(text, mainScene.width/2f, mainScene.height/2f, getPaint())
    }

    override fun onTouch(e: MotionEvent): Boolean {
        return when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                mainScene.setScene(GameScene(mainScene))
                true
            }
            else -> false
        }
    }

    private fun getPaint(): Paint {
        return Paint().apply {
            color = Color.BLUE
            textSize = 50f
            isAntiAlias = true
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
        }
    }

}