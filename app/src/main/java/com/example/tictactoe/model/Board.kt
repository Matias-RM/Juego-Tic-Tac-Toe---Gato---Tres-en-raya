package com.example.tictactoe.model

class Board {

    private val cells: Array<Array<Player?>> =
        Array(3) { arrayOfNulls<Player?>(3) }

    var movesCount: Int = 0
        private set

    val isFull: Boolean
        get() = movesCount == 9

    fun get(row: Int, col: Int): Player? = cells[row][col]

    fun place(row: Int, col: Int, player: Player): Boolean {
        if (cells[row][col] != null) return false
        cells[row][col] = player
        movesCount++
        return true
    }

    fun undo(row: Int, col: Int) {
        if (cells[row][col] != null) {
            cells[row][col] = null
            movesCount--
        }
    }

    fun reset() {
        for (i in 0..2) for (j in 0..2) cells[i][j] = null
        movesCount = 0
    }

    fun getWinningCells(player: Player): IntArray? {
        // Filas
        for (i in 0..2) {
            if (cells[i][0] == player && cells[i][1] == player && cells[i][2] == player)
                return intArrayOf(i * 3, i * 3 + 1, i * 3 + 2)
        }
        // Columnas
        for (j in 0..2) {
            if (cells[0][j] == player && cells[1][j] == player && cells[2][j] == player)
                return intArrayOf(j, j + 3, j + 6)
        }
        // Diagonales
        if (cells[0][0] == player && cells[1][1] == player && cells[2][2] == player)
            return intArrayOf(0, 4, 8)
        if (cells[0][2] == player && cells[1][1] == player && cells[2][0] == player)
            return intArrayOf(2, 4, 6)

        return null
    }

    fun isWinner(player: Player): Boolean = getWinningCells(player) != null
}