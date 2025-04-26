package com.rprandt.memorygame.interfaces

import android.graphics.Canvas
import android.view.MotionEvent

interface IScene {
    fun render(canvas: Canvas)
    fun onTouch(e: MotionEvent): Boolean
}