package com.rprandt.memorygame.scenes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import com.rprandt.memorygame.MainActivity
import com.rprandt.memorygame.interfaces.IScene
import com.rprandt.memorygame.objects.CardObject


class CardModel (
    val name: String,
    var bitmap: Bitmap? = null,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false,
    var cardObject: CardObject? = null,
)

class Positions (
    val x: Int,
    val y: Int,
)

class GameScene(private val mainScene: MainActivity.MainScene): IScene {

    private val cardWith = mainScene.width / 4
    private val cardHeight = mainScene.width / 4
    private val centerX = mainScene.width / 2
    private val yPos = 70

    private var verseCardBitmap: Bitmap? = null
    private var batCatBitmap: Bitmap? = null
    private var blueCatBitmap: Bitmap? = null
    private var lionCatBitmap: Bitmap? = null
    private var mysticalCatBitmap: Bitmap? = null
    private var pigCatBitmap: Bitmap? = null
    private var whiteCatBitmap: Bitmap? = null
    private val handler = Handler(Looper.getMainLooper())


    // List of card models
    private var cardModels: List<CardModel>

    private val positions = listOf(
        Positions(centerX - cardWith, yPos),
        Positions(centerX + 20, yPos),
        Positions(centerX - cardWith, (yPos + 20) + cardHeight),
        Positions(centerX + 20, (yPos + 20) + cardHeight),
        Positions(centerX - cardWith, (yPos + 40) + (cardHeight * 2)),
        Positions(centerX + 20, (yPos + 40) + (cardHeight * 2)),
        Positions(centerX - cardWith, (yPos + 60) + (cardHeight * 3)),
        Positions(centerX + 20, (yPos + 60) + (cardHeight * 3)),
        Positions(centerX - cardWith, (yPos + 80) + (cardHeight * 4)),
        Positions(centerX + 20, (yPos + 80) + (cardHeight * 4)),
        Positions(centerX - cardWith, (yPos + 100) + (cardHeight * 5)),
        Positions(centerX + 20, (yPos + 100) + (cardHeight * 5)),
        Positions(centerX - cardWith, (yPos + 120) + (cardHeight * 6)),
        Positions(centerX + 20, (yPos + 120) + (cardHeight * 6)),
        Positions(centerX - cardWith, (yPos + 140) + (cardHeight * 7)),
        Positions(centerX + 20, (yPos + 140) + (cardHeight * 7)),
    )

    init {
        loadAssets()
        cardModels = listOf(
            CardModel("batCat", batCatBitmap),
            CardModel("batCat", batCatBitmap),
            CardModel("blueCat", blueCatBitmap),
            CardModel("blueCat", blueCatBitmap),
            CardModel("lionCat", lionCatBitmap),
            CardModel("lionCat", lionCatBitmap),
            CardModel("mysticalCat", mysticalCatBitmap),
            CardModel("mysticalCat", mysticalCatBitmap),
            CardModel("pigCat", pigCatBitmap),
            CardModel("pigCat", pigCatBitmap),
            CardModel("whiteCat", whiteCatBitmap),
            CardModel("whiteCat", whiteCatBitmap),
        )
        cardModels = cardModels.shuffled()
        for (card in cardModels) {
            val position = positions[cardModels.indexOf(card)]
            card.cardObject = CardObject(verseCardBitmap!!, position.x, position.y, mainScene)
        }
    }

    override fun render(canvas: Canvas) {
        for (card in cardModels) {
            card.cardObject?.setImage(if (card.isFlipped) card.bitmap!! else verseCardBitmap!!)
            card.cardObject?.render(canvas)
        }
    }

    override fun onTouch(e: MotionEvent): Boolean {
        for (card in cardModels) {
            if (card.cardObject?.onTouch(e) != true) {
               continue
            }
            if (card.isMatched) {
                continue
            }

            card.isFlipped = true
            checkForMatch()
            return true
        }
        return false
    }

    private fun loadAssets() {
        verseCardBitmap = loadBitmap("verse_card.webp")
        batCatBitmap = loadBitmap("bat_cat.webp")
        blueCatBitmap = loadBitmap("blue_cat.webp")
        lionCatBitmap = loadBitmap("lion_cat.webp")
        mysticalCatBitmap = loadBitmap("mystical_cat.webp")
        pigCatBitmap = loadBitmap("pig_cat.webp")
        whiteCatBitmap = loadBitmap("white_cat.webp")
    }

    private fun loadBitmap(file: String): Bitmap? {
        try {
            val inputStream = mainScene.context.assets.open(file)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // Resize the bitmap to fit the card size
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, cardWith, cardHeight, true)
            // Recycle the original bitmap to free up memory
            bitmap.recycle()
            // Close the input stream
            inputStream.close()
            return resizedBitmap
        }
        catch (e: Exception) {
            Log.d("App", e.message ?: "Algo ocorreu de errado ao carrgar a imagem")
        }

        return null
    }

    private fun checkForMatch() {
        val flippedCards = cardModels.filter { it.isFlipped && !it.isMatched }
        if (flippedCards.size == 1) return
        if (flippedCards.size > 2) {
            resetFlippedCards()
            return
        }
        if (flippedCards[0].name == flippedCards[1].name) {
            setFlippedCardsToMatched()
            checkGameOver()
            return
        }
        handler.postDelayed({
            resetFlippedCards()
        }, 1000
        )
    }


    private fun resetFlippedCards() = cardModels
        .filter { !it.isMatched }
        .forEach { it.isFlipped = false }

    private fun checkGameOver() {
        if (cardModels.all { it.isMatched }) {
            restartGame()
        }
    }

    private fun setFlippedCardsToMatched() {
        cardModels.filter { it.isFlipped }
            .forEach { it.isMatched = true }
    }

    private fun restartGame() {
        cardModels.forEach { it.isFlipped = false; it.isMatched = false }
        cardModels = cardModels.shuffled()
        for (card in cardModels) {
            val position = positions[cardModels.indexOf(card)]
            card.cardObject = CardObject(verseCardBitmap!!, position.x, position.y, mainScene)
        }
    }

}