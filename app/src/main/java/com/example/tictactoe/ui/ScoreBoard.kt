package com.example.tictactoe.ui
class ScoreBoard {
    var playerWins: Int = 0
        private set
    var cpuWins: Int = 0
        private set
    var draws: Int = 0
        private set
    fun addPlayerWin() { playerWins++ }
    fun addCpuWin() { cpuWins++ }
    fun reset() {
        playerWins = 0
        cpuWins = 0
    }
}