package com.example.tictactoe

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tictactoe.logic.AIPlayer
import com.example.tictactoe.logic.Difficulty
import com.example.tictactoe.logic.GameEngine
import com.example.tictactoe.model.GameState
import com.example.tictactoe.model.Player
import com.example.tictactoe.ui.ScoreBoard

class MainActivity : AppCompatActivity() {

    // --- UI ---
    private lateinit var tvStatus: TextView
    private lateinit var tvScorePlayer: TextView
    private lateinit var tvScoreCPU: TextView
    private lateinit var buttons: Array<Button>
    private lateinit var btnReset: Button
    private lateinit var btnResetScore: Button
    private lateinit var layoutDifficulty: LinearLayout
    private lateinit var spinnerDifficulty: Spinner

    // --- Lógica ---
    private val engine = GameEngine()
    private val score = ScoreBoard()
    private var vsCPU = false
    private var ai: AIPlayer? = null

    private val HUMAN = Player.X
    private val CPU = Player.O

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setupListeners()
        setupDifficultySpinner()
        updateStatus()
        updateScoreUI()
    }

    // ---------------- UI setup ----------------
    private fun bindViews() {
        tvStatus = findViewById(R.id.tvStatus)
        tvScorePlayer = findViewById(R.id.tvScorePlayer)
        tvScoreCPU = findViewById(R.id.tvScoreCPU)
        btnReset = findViewById(R.id.btnReset)
        btnResetScore = findViewById(R.id.btnResetScore)
        layoutDifficulty = findViewById(R.id.layoutDifficulty)
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty)

        buttons = arrayOf(
            findViewById(R.id.btn0), findViewById(R.id.btn1), findViewById(R.id.btn2),
            findViewById(R.id.btn3), findViewById(R.id.btn4), findViewById(R.id.btn5),
            findViewById(R.id.btn6), findViewById(R.id.btn7), findViewById(R.id.btn8)
        )
    }

    private fun setupListeners() {
        buttons.forEachIndexed { index, btn ->
            btn.setOnClickListener { onCellClicked(index) }
        }
        btnReset.setOnClickListener { resetGame() }
        btnResetScore.setOnClickListener { score.reset(); updateScoreUI() }

        findViewById<RadioGroup>(R.id.radioGroupMode)
            .setOnCheckedChangeListener { _, checkedId ->
                vsCPU = checkedId == R.id.radioVsCPU
                layoutDifficulty.visibility = if (vsCPU) View.VISIBLE else View.GONE
                score.reset()
                createAI()
                resetGame()
                updateScoreUI()
            }
    }

    private fun setupDifficultySpinner() {
        val items = arrayOf("Fácil", "Medio", "Difícil (Imbatible)")
        spinnerDifficulty.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, items
        )
        spinnerDifficulty.setSelection(2)
        spinnerDifficulty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                val d = when (pos) {
                    0 -> Difficulty.EASY
                    1 -> Difficulty.MEDIUM
                    else -> Difficulty.HARD
                }
                ai?.difficulty = d
                resetGame()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    private fun createAI() {
        ai = if (vsCPU) AIPlayer(me = CPU, opponent = HUMAN) else null
    }

    // ---------------- Eventos ----------------
    private fun onCellClicked(index: Int) {
        if (!engine.isActive) return
        if (vsCPU && engine.currentPlayer == CPU) return

        val row = index / 3
        val col = index % 3
        if (!engine.play(row, col)) return

        renderCell(index, engine.board.get(row, col)!!)
        handlePostMove()
    }

    private fun cpuTurn() {
        if (!engine.isActive) return
        val move = ai?.chooseMove(engine.board) ?: return
        val (row, col) = move
        engine.play(row, col)
        renderCell(row * 3 + col, CPU)
        handlePostMove()
    }

    // ---------------- Render ----------------
    private fun renderCell(index: Int, player: Player) {
        buttons[index].text = player.symbol
    }

    private fun handlePostMove() {
        when (engine.state) {
            GameState.WIN -> {
                onWin(engine.winner!!)
            }
            GameState.DRAW -> {
                tvStatus.text = "¡Empate! "
            }
            GameState.IN_PROGRESS -> {
                updateStatus()
                maybeScheduleCpu()
            }
        }
    }

    private fun maybeScheduleCpu() {
        if (vsCPU && engine.currentPlayer == CPU) {
            buttons[0].postDelayed({ cpuTurn() }, 350)
        }
    }

    private fun onWin(winner: Player) {
        if (vsCPU) {
            if (winner == HUMAN) {
                score.addPlayerWin()
                tvStatus.text = "¡Ganaste! "
            } else {
                score.addCpuWin()
                tvStatus.text = "Ganó la CPU"
            }
        } else {
            tvStatus.text = "¡Ganó ${winner.symbol}! "
        }
        updateScoreUI()
    }

    private fun updateStatus() {
        tvStatus.text = when {
            !vsCPU -> "Turno de ${engine.currentPlayer.symbol}"
            engine.currentPlayer == HUMAN -> "Tu turno"
            else -> "Pensando... "
        }
    }

    private fun updateScoreUI() {
        if (vsCPU) {
            tvScorePlayer.text = "Tú: ${score.playerWins}"
            tvScoreCPU.text = "CPU: ${score.cpuWins}"
        } else {
            tvScorePlayer.text = "X: ${score.playerWins}"
            tvScoreCPU.text = "O: ${score.cpuWins}"
        }
    }

    private fun resetGame() {
        engine.reset()
        buttons.forEach { it.text = "" }
        updateStatus()
    }
}