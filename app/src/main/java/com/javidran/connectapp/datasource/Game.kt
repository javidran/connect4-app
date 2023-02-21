package com.javidran.connectapp.datasource

import com.javidran.connectapp.enums.Disk
import com.javidran.connectapp.enums.Player

class Game {

    var grid: Array<Array<Disk>> = Array(NUMBER_OF_COLUMNS) { Array(NUMBER_OF_ROWS) { Disk.Empty } }
        private set
    var turn = Player.Red
        private set
    var lastAdded: Pair<Int, Int>? = null // Column, Row
        private set
    var winner: Player? = null
        private set
    var winningCombination: List<Pair<Int, Int>> = emptyList()
        private set

    fun resetGame() {
        grid = Array(NUMBER_OF_COLUMNS) { Array(NUMBER_OF_ROWS) { Disk.Empty } }
        turn = Player.Red
        lastAdded = null
        winner = null
    }

    fun addDisk(column: Int) {
        if (!isColumnFull(column) && !isGridFull()) {
            for (i in grid[column].indices) {
                if (grid[column][i] == Disk.Empty) {
                    // Disk can be added on the column.
                    grid[column][i] = chooseDiskColor()
                    lastAdded = Pair(column, i)
                    if (isTheGameFinished(column, i)) {
                        winner = turn
                    } else {
                        changeTurn()
                    }
                    break
                }
            }
        }
    }


    private fun isColumnFull(column: Int): Boolean {
        return grid[column][NUMBER_OF_ROWS - 1] != Disk.Empty
    }

    fun isGridFull(): Boolean {
        for (index in grid.indices) {
            if (grid[index][NUMBER_OF_ROWS - 1] == Disk.Empty) {
                return false
            }
        }
        return true
    }

    private fun chooseDiskColor(): Disk {
        return if (turn == Player.Red) {
            Disk.Red
        } else {
            Disk.Yellow
        }
    }

    private fun isTheGameFinished(column: Int, row: Int): Boolean {
        winningCombination = checkHorizontal(row)
            .plus(checkVertical(column))
            .plus(checkUpwardDiagonal(column, row))
            .plus(checkDownwardDiagonal(column, row))
        return winningCombination.isNotEmpty()
    }

    private fun checkHorizontal(row: Int): List<Pair<Int, Int>> {
        var winningCombination = emptyPairList()
        for (columnIt in grid.indices) {
            winningCombination =
                increaseOrResetWinningCombination(columnIt, row, winningCombination)
        }
        return returnWinningCombinationIfWon(winningCombination)
    }

    private fun checkVertical(column: Int): List<Pair<Int, Int>> {
        var winningCombination = emptyPairList()
        for (rowIt in grid[column].indices) {
            winningCombination =
                increaseOrResetWinningCombination(column, rowIt, winningCombination)
        }
        return returnWinningCombinationIfWon(winningCombination)
    }

    private fun checkUpwardDiagonal(column: Int, row: Int): List<Pair<Int, Int>> {
        var columnIt = if (row > column) {
            0
        } else {
            column - row
        } // Column iterator start
        var rowIt = if (row > column) {
            row - column
        } else {
            0
        } // Row iterator start
        var winningCombination = emptyPairList()
        while (columnIt < NUMBER_OF_COLUMNS && rowIt < NUMBER_OF_ROWS) {
            winningCombination =
                increaseOrResetWinningCombination(columnIt, rowIt, winningCombination)
            columnIt++
            rowIt++
        }
        return returnWinningCombinationIfWon(winningCombination)
    }

    private fun checkDownwardDiagonal(column: Int, row: Int): List<Pair<Int, Int>> {
        var columnIt = if (column > NUMBER_OF_ROWS - 1 - row) {
            column - (NUMBER_OF_ROWS - 1 - row)
        } else {
            0
        } // Column iterator start
        var rowIt = if (column > NUMBER_OF_ROWS - 1 - row) {
            NUMBER_OF_ROWS - 1
        } else {
            row + column
        } // Row iterator start
        var winningCombination = emptyPairList()
        while (columnIt < NUMBER_OF_COLUMNS && rowIt >= 0) {
            winningCombination =
                increaseOrResetWinningCombination(columnIt, rowIt, winningCombination)
            columnIt++
            rowIt--
        }
        return returnWinningCombinationIfWon(winningCombination)
    }

    private fun increaseOrResetWinningCombination(
        column: Int,
        row: Int,
        winningCombination: List<Pair<Int, Int>>
    ): List<Pair<Int, Int>> {
        return if (winningCombination.size >= NUMBER_OF_DISKS_TO_WIN) {
            winningCombination
        } else if (grid[column][row] == chooseDiskColor()) {
            winningCombination.plus(Pair(column, row))
        } else {
            emptyList()
        }
    }

    private fun returnWinningCombinationIfWon(winningCombination: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        return if (winningCombination.size >= NUMBER_OF_DISKS_TO_WIN) {
            winningCombination
        } else {
            emptyPairList()
        }
    }

    private fun changeTurn() {
        turn = if (turn == Player.Red) {
            Player.Yellow
        } else {
            Player.Red
        }
    }

    companion object {
        private const val NUMBER_OF_COLUMNS = 7
        private const val NUMBER_OF_ROWS = 6
        private const val NUMBER_OF_DISKS_TO_WIN = 4

        private fun emptyPairList() = emptyList<Pair<Int, Int>>()
    }
}