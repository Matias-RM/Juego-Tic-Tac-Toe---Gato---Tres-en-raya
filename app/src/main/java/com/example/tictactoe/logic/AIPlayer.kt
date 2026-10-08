package com.example.tictactoe.logic

import com.example.tictactoe.model.Board
import com.example.tictactoe.model.Player

enum class Difficulty { EASY, MEDIUM, HARD }

class AIPlayer(
    private val me: Player,
    private val opponent: Player,
    var difficulty: Difficulty = Difficulty.HARD
) {

    /** Devuelve la celda elegida por la IA. */
    fun chooseMove(board: Board): Pair<Int, Int>? = when (difficulty) {
        Difficulty.EASY -> randomMove(board)
        Difficulty.MEDIUM -> mediumMove(board)
        Difficulty.HARD -> minimaxMove(board)
    }

    // ---------- Fácil ----------
    private fun randomMove(board: Board): Pair<Int, Int>? {
        val empty = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) for (j in 0..2) if (board.get(i, j) == null) empty.add(i to j)
        return empty.randomOrNull()
    }

    // ---------- Medio ----------
    private fun mediumMove(board: Board): Pair<Int, Int>? {
        findWinningMove(board, me)?.let { return it }
        findWinningMove(board, opponent)?.let { return it }
        if (board.get(1, 1) == null) return 1 to 1
        return randomMove(board)
    }

    private fun findWinningMove(board: Board, player: Player): Pair<Int, Int>? {
        for (i in 0..2) for (j in 0..2) {
            if (board.get(i, j) == null) {
                board.place(i, j, player)
                val win = board.isWinner(player)
                board.undo(i, j)
                if (win) return i to j
            }
        }
        return null
    }

    // ---------- Minimax (CPU) ----------
    private fun minimaxMove(board: Board): Pair<Int, Int>? {
        var bestScore = Int.MIN_VALUE
        var best: Pair<Int, Int>? = null

        for (i in 0..2) for (j in 0..2) {
            if (board.get(i, j) == null) {
                board.place(i, j, me)
                val score = minimax(board, 0, false)
                board.undo(i, j)
                if (score > bestScore) {
                    bestScore = score
                    best = i to j
                }
            }
        }
        return best
    }

    private fun minimax(board: Board, depth: Int, isMaximizing: Boolean): Int {
        when {
            board.isWinner(me) -> return 10 - depth
            board.isWinner(opponent) -> return depth - 10
            board.isFull -> return 0
        }

        return if (isMaximizing) {
            var best = Int.MIN_VALUE
            for (i in 0..2) for (j in 0..2) if (board.get(i, j) == null) {
                board.place(i, j, me)
                best = maxOf(best, minimax(board, depth + 1, false))
                board.undo(i, j)
            }
            best
        } else {
            var best = Int.MAX_VALUE
            for (i in 0..2) for (j in 0..2) if (board.get(i, j) == null) {
                board.place(i, j, opponent)
                best = minOf(best, minimax(board, depth + 1, true))
                board.undo(i, j)
            }
            best
        }
    }
}