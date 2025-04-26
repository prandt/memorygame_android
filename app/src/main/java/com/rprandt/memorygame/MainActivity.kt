package com.rprandt.memorygame

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.rprandt.memorygame.interfaces.IScene
import com.rprandt.memorygame.scenes.StartScene

class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MainScene(this).also {
            it.setOnTouchListener(it)
            setContentView(it)
        }
    }
    
    class MainScene(context: Context): View(context), OnTouchListener {
        private var scene: IScene = StartScene(this)

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            this.render(canvas)
            invalidate()
        }

        private fun render(canvas: Canvas) {
            scene.render(canvas)
        }

        fun setScene(newScene: IScene) {
            scene = newScene
            invalidate() // Request a redraw of the view
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            val e = event ?: return false
            invalidate()
            return scene.onTouch(e)
        }

    }

}