package com.javidran.connectapp.movelview

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.javidran.connectapp.Game
import com.javidran.connectapp.enums.Disk
import com.javidran.connectapp.enums.Player

class GameViewModel : ViewModel() {

    var game = Game()
        private set

    var grid = mutableStateOf(
        Array(Game.NumberOfColumns) { Array(Game.NumberOfRows) { Disk.Empty } }
    )
        private set

    var winner = mutableStateOf(Player.None)
        private set

    var turn = mutableStateOf(Player.Red)

    val redWins = mutableStateOf(0)
    val yellowWins = mutableStateOf(0)

    val tie = mutableStateOf(false)

    private var addingDiskFlag = false

    fun addDisk(column: Int) {
        if(!addingDiskFlag) {
            addingDiskFlag = true
            game.addDisk(column = column)
            game.winner?.let {
                winner.value = it
                if (it == Player.Red) {
                    redWins.value++
                } else {
                    yellowWins.value++
                }
            }
            turn.value = game.turn
            grid.value = game.grid.copyOf()
            tie.value = game.isGridFull()
            addingDiskFlag = false
        }
    }

    fun resetGame() {
        game.resetGame()
        grid.value = game.grid.copyOf()
        winner.value = Player.None
        turn.value = Player.Red
        tie.value = false
    }

    fun resetAllGames() {
        redWins.value = 0
        yellowWins.value = 0
        resetGame()
    }

}