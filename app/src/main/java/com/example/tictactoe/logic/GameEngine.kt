package com.example.tictactoe.logic

import com.example.tictactoe.model.Board
import com.example.tictactoe.model.GameState
import com.example.tictactoe.model.Player

class GameEngine(
    val startingPlayer: Player = Player.X
) {
    val board = Board()

    var currentPlayer: Player = startingPlayer
        private set

    var state: GameState = GameState.IN_PROGRESS
        private set

    var winner: Player? = null
        private set

    var winningCells: IntArray? = null
        private set

    val isActive: Boolean
        get() = state == GameState.IN_PROGRESS

    /**
     * Realiza un movimiento. Devuelve true si fue válido.
     */
    fun play(row: Int, col: Int): Boolean {
        if (!isActive) return false
        if (!board.place(row, col, currentPlayer)) return false

        evaluate()
        return true
    }

    /** Revisa si hay ganador o empate y actualiza el estado. */
    private fun evaluate() {
        val cells = board.getWinningCells(currentPlayer)
        when {
            cells != null -> {
                state = GameState.WIN
                winner = currentPlayer
                winningCells = cells
            }
            board.isFull -> {
                state = GameState.DRAW
            }
            else -> {
                currentPlayer = currentPlayer.opponent
            }
        }
    }

    fun reset(newStartingPlayer: Player = startingPlayer) {
        board.reset()
        currentPlayer = newStartingPlayer
        state = GameState.IN_PROGRESS
        winner = null
        winningCells = null
    }
}