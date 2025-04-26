package com.rprandt.memorygame.services

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.rprandt.memorygame.R

class SoundService(context: Context) {

    private val soundPool: SoundPool
    private val cardFlipSound: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        cardFlipSound = soundPool.load(context, R.raw.card_flip, 1)
    }

    fun playCardFlipSound() {
        soundPool.play(cardFlipSound, 1f, 1f, 1, 0, 1f)
    }

}