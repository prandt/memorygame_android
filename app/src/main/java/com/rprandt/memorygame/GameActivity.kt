package com.rprandt.memorygame

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import com.rprandt.memorygame.databinding.ActivityGameBinding
import com.rprandt.memorygame.services.SoundService

data class CardGame(
    val id: Int,
    val imageResId: Int,
    var isRevealed: Boolean = false,
    var isMatched: Boolean = false
)
class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var soundService: SoundService
    private lateinit var cards: List<CardGame>
    private val handler = Handler(Looper.getMainLooper())
    private val maxPlays = 50
    private var currentPlays = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        cards = generateCards()
        binding = ActivityGameBinding.inflate(layoutInflater)
        soundService = SoundService(this)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupUi()
    }

    private fun setupUi() {
        setRemainingPlaysText()
        setTotalPlaysText()
        for (card in cards) {
            val cardView = LayoutInflater.from(this).inflate(R.layout.card_item, binding.gridBoard, false)

            val imageView = cardView.findViewById<ImageView>(R.id.card_image)

            cardView.tag = card

            cardView.setOnClickListener {
                onClickCard(cardView, imageView, card)
            }
            binding.gridBoard.addView(cardView)
        }
    }

    private fun onClickCard(cardView: View, imageView: ImageView, card: CardGame) {
        incrementPlays()
        soundService.playCardFlipSound()
        if (!card.isRevealed) {
            flipCard(cardView, imageView, card.imageResId)
            card.isRevealed = true
            checkMatch()
        }
    }

    private fun flipCard(cardView: View, imageView: ImageView, newImage: Int) {
        val flipOut = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 90f)
        val flipIn = ObjectAnimator.ofFloat(cardView, "rotationY", -90f, 0f)

        flipOut.duration = 200
        flipIn.duration = 200

        flipOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                imageView.setImageResource(newImage)
                flipIn.start()
            }
        })

        flipOut.start()
    }

    private fun generateCards() : List<CardGame> {
        val cardImages = listOf(
            R.drawable.bat_cat,
            R.drawable.blue_cat,
            R.drawable.pig_cat,
            R.drawable.white_cat,
            R.drawable.lion_cat,
            R.drawable.mystical_cat
        )
        val cards = mutableListOf<CardGame>()
        for (i in cardImages.indices) {
            cards.add(CardGame(i, cardImages[i]))
            cards.add(CardGame(i + cardImages.size, cardImages[i]))
        }
        cards.shuffle()
        return cards
    }

    private fun checkMatch() {
        val revealedCards = cards.filter { it.isRevealed && !it.isMatched }
        if (revealedCards.size == 1) return
        if (revealedCards.size > 2) return resetCards()
        if (revealedCards[0].imageResId == revealedCards[1].imageResId) {
            cards.find { it.id == revealedCards[0].id }?.isMatched = true
            cards.find { it.id == revealedCards[1].id }?.isMatched = true
            return checkWinGame()
        }
        resetCards()
    }

    private fun resetCards() {
        cards
            .filter { !it.isMatched && it.isRevealed }
            .forEach {
                it.isRevealed = false
                // FLIP BACK
                val cardView = binding.gridBoard.children.find { view ->
                    (view.tag as? CardGame)?.id == it.id
                }
                val imageView = cardView?.findViewById<ImageView>(R.id.card_image)
                handler.postDelayed({
                    flipCard(cardView!!, imageView!!, R.drawable.verse_card)
                }, 1000)
            }
    }

    private fun checkWinGame() {
        if (cards.all { it.isMatched }) {
            showWinDialog()
        }
    }

    private fun getRemainingPlays(): Int {
        return maxPlays - currentPlays
    }

    private fun incrementPlays() {
        currentPlays++
        setRemainingPlaysText()
        setTotalPlaysText()
        if (currentPlays >= maxPlays) {
            showGameOverDialog()
        }
    }

    private fun setRemainingPlaysText() {
        binding.remaningCounter.text = "Jogadas restantes ${getRemainingPlays()}"
    }

    private fun setTotalPlaysText() {
        binding.playsCounter.text = "Total de jogadas: $currentPlays"
    }

    private fun showGameOverDialog() {
        showDialog(
            title = "Fim de Jogo",
            message = "Você não tem mais jogadas restantes. Deseja jogar novamente?",
            onConfirm = {
                resetGame()
            }
        )
    }

    private fun showWinDialog() {
        showDialog(
            title = "Parabéns!",
            message = "Você ganhou! Deseja jogar novamente?",
            onConfirm = {
                resetGame()
            }
        )
    }

    private fun showDialog(title: String, message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Jogar Novamente") { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun resetGame() {
        currentPlays = 0
        cards.forEach { it.isRevealed = false; it.isMatched = false }
        binding.gridBoard.removeAllViews()
        setupUi()
    }

}
