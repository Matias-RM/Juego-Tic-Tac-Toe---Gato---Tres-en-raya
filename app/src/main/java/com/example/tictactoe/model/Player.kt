package com.example.tictactoe.model

enum class Player(val symbol: String) {
    X("X"),
    O("O");

    val opponent: Player
        get() = if (this == X) O else X

    companion object {
        fun fromSymbol(symbol: String): Player? =
            entries.firstOrNull { it.symbol == symbol }
    }
}