package com.javidran.connectapp

import com.javidran.connectapp.enums.Disk
import com.javidran.connectapp.enums.Player

class Game {

    var grid: Array<Array<Disk>> = Array(NumberOfColumns) { Array(NumberOfRows) { Disk.Empty } }
        private set
    var turn = Player.Red
        private set
    var lastAdded: Pair<Int, Int>? = null // Column, Row
        private set
    var winner: Player? = null
        private set

    fun resetGame() {
        grid = Array(NumberOfColumns) { Array(NumberOfRows) { Disk.Empty } }
        turn = Player.Red
        lastAdded = null
        winner = null
    }

    fun addDisk(column: Int) {
        if(!isColumnFull(column) && !isGridFull()) {
            for(i in grid[column].indices) {
                if(grid[column][i] == Disk.Empty) {
                    // Disk can be added on the column.
                    grid[column][i] = chooseDiskColor()
                    lastAdded = Pair(column, i)
                    if(isTheGameFinished(column, i)) {
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
        return grid[column][NumberOfRows - 1] != Disk.Empty
    }

    fun isGridFull() : Boolean {
        for (index in grid.indices) {
            if(grid[index][NumberOfRows - 1] == Disk.Empty) {
                return false
            }
        }
        return true
    }

    private fun chooseDiskColor(): Disk {
        return if(turn == Player.Red) {
            Disk.Red
        } else {
            Disk.Yellow
        }
    }

    private fun isTheGameFinished(column: Int, row: Int): Boolean {
        return checkHorizontal(row) or
                checkVertical(column) or
                checkUpwardDiagonal(column, row) or
                checkDownwardDiagonal(column, row)
    }

    private fun checkHorizontal(row: Int): Boolean {
        var connectedDisks = 0
        for(columnIt in grid.indices) {
            connectedDisks = checkDisk(columnIt, row, connectedDisks)
            if(connectedDisks >= NumberOfDisksToWin) {
                return true
            }
        }
        return false
    }

    private fun checkVertical(column: Int): Boolean {
        var connectedDisks = 0
        for(rowIt in grid[column].indices) {
            connectedDisks = checkDisk(column, rowIt, connectedDisks)
            if(connectedDisks >= NumberOfDisksToWin) {
                return true
            }
        }
        return false
    }

    private fun checkUpwardDiagonal(column: Int, row: Int): Boolean {
        var columnIt =  if(row > column) { 0 } else { column - row } // Column iterator start
        var rowIt = if(row > column) { row - column } else { 0 } // Row iterator start
        var connectedDisks = 0
        while(columnIt < NumberOfColumns && rowIt < NumberOfRows) {
            connectedDisks = checkDisk(columnIt, rowIt, connectedDisks)
            if(connectedDisks >= NumberOfDisksToWin) {
                return true
            }
            columnIt++
            rowIt++
        }
        return false
    }

    private fun checkDownwardDiagonal(column: Int, row: Int): Boolean {
        var columnIt =  if(column > NumberOfRows - 1 - row) { column - (NumberOfRows - 1 - row) } else { 0 } // Column iterator start
        var rowIt = if(column > NumberOfRows - 1 - row) { NumberOfRows - 1 } else { row + column } // Row iterator start
        var connectedDisks = 0
        while(columnIt < NumberOfColumns && rowIt >= 0) {
            connectedDisks = checkDisk(columnIt, rowIt, connectedDisks)
            if(connectedDisks >= NumberOfDisksToWin) {
                return true
            }
            columnIt++
            rowIt--
        }
        return false
    }

    private fun checkDisk(column: Int, row: Int, connectedDisks: Int): Int {
        return if(grid[column][row] == chooseDiskColor()) { connectedDisks + 1 } else { 0 } //TODO: Si en vez de hacer una cuenta, se guardan los discos en una Lista, se podria saber la combinación ganadora
    }


    private fun changeTurn() {
        turn = if(turn == Player.Red) {
            Player.Yellow
        } else {
            Player.Red
        }
    }

    companion object {
        public const val NumberOfColumns = 7
        public const val NumberOfRows = 6
        private const val NumberOfDisksToWin = 4
    }
}