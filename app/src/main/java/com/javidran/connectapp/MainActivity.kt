package com.javidran.connectapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.javidran.connectapp.enums.Disk
import com.javidran.connectapp.enums.Player
import com.javidran.connectapp.movelview.GameViewModel
import com.javidran.connectapp.ui.theme.Amarillo
import com.javidran.connectapp.ui.theme.ColorLinea
import com.javidran.connectapp.ui.theme.ConnectAppTheme

class MainActivity : AppCompatActivity() {

    private val gridViewModel by viewModels<GameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConnectAppTheme {
                // A surface container using the 'background' color from the theme
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
                Surface(color = MaterialTheme.colors.background) {
                    ActivityScreen(gameViewModel = gridViewModel)
                }
            }
        }
    }

    @Composable
    fun ActivityScreen(gameViewModel: GameViewModel) {
        Column(horizontalAlignment = CenterHorizontally) {
            WinCount(gameViewModel)
            Spacer(modifier = Modifier.weight(1.0f))
            GameZone(gameViewModel)
            Spacer(modifier = Modifier.weight(1.0f))
            ResetButtons(gameViewModel)
        }
    }

    @Composable
    private fun GameZone(gameViewModel: GameViewModel) {
        if (gameViewModel.winner.value != Player.None || gameViewModel.tie.value) {
            if(gameViewModel.tie.value) {
                Text("IT'S A TIE!", fontSize = 40.sp)
            } else {
                PlayerText(gameViewModel.winner.value.toString().uppercase() + " WON!", player = gameViewModel.winner.value, fontSize = 40.sp)
            }
            DiskGrid(grid = gameViewModel.grid.value, onColumnPressed = { })
        } else {
            Turn(player = gameViewModel.turn.value)
            DiskGrid(grid = gameViewModel.grid.value, onColumnPressed = gameViewModel::addDisk)
        }
    }

    @Composable
    private fun WinCount(gameViewModel: GameViewModel) {
        Row(Modifier.padding(10.dp)) {
            Spacer(modifier = Modifier.weight(0.5f))
            WonGames(player = Player.Red, numberOfWonGames = gameViewModel.redWins.value)
            Spacer(modifier = Modifier.weight(1.0f))
            WonGames(player = Player.Yellow, numberOfWonGames = gameViewModel.yellowWins.value)
            Spacer(modifier = Modifier.weight(0.5f))
        }
    }

    @Composable
    private fun ResetButtons(gameViewModel: GameViewModel) {
        Button(onClick = gameViewModel::resetGame, Modifier.padding(bottom = 10.dp)) {
            Text("Reset game")
        }
        Button(onClick = gameViewModel::resetAllGames, Modifier.padding(bottom = 10.dp)) {
            Text("Reset win count")
        }
    }

    @Composable
    fun WonGames(player: Player, numberOfWonGames: Int) {
        Text(text = player.name, color = chooseColor(player))
        Text(" won $numberOfWonGames games.")
    }

    @Composable
    fun Turn(player: Player) {
        Row(Modifier.padding(10.dp)) {
            Text(text = "It's ")
            PlayerText(player = player)
            Text(text = "'s turn.")
        }
    }

    @Composable
    fun PlayerText(text: String? = null, player: Player, fontSize: TextUnit = TextUnit.Unspecified) {
        val internalText: String = text ?: player.name
        Text(
            text = internalText,
            color = chooseColor(player),
            fontSize = fontSize
        )
    }

    private fun chooseColor(player: Player): Color {
        return if(player == Player.Red) {
            Color.Red
        } else {
            Amarillo
        }
    }

    @Composable
    fun DiskGrid(grid: Array<Array<Disk>>, onColumnPressed: (column: Int) -> Unit) {
        Column(modifier = Modifier.width(IntrinsicSize.Min), horizontalAlignment = CenterHorizontally) {
            Divider(color = ColorLinea)
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Spacer(modifier = Modifier.weight(1.0f))
                grid.forEachIndexed { index, arrayOfDisks ->
                    if(index == 0) {
                        Divider(color = ColorLinea, modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp))
                    }
                    DiskColumn(diskArray = arrayOfDisks, columnNumber = index, onColumnPressed = onColumnPressed)
                    Divider(color = ColorLinea, modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp))
                }
                Spacer(modifier = Modifier.weight(1.0f))
            }
            Divider(color = ColorLinea)
        }
    }

    @Composable
    fun DiskColumn(diskArray: Array<Disk>, columnNumber: Int, onColumnPressed: (column: Int) -> Unit) {
        Column(
            Modifier.clickable {
                onColumnPressed(columnNumber)
            }
        ) {
            for(i in diskArray.indices.reversed()) {
                Disk(diskArray[i])
            }
        }
    }

    @Composable
    fun Disk(disk: Disk) {
        val fontSize = 40.sp
        DisableSelection {
            when (disk) {
                Disk.Red -> {
                    Text(text = "\uD83D\uDD34", fontSize = fontSize, style = TextStyle.Default)
                }
                Disk.Yellow -> {
                    Text(text = "\uD83D\uDFE1", fontSize = fontSize, style = TextStyle.Default)
                }
                else -> {
                    Text(text = "⚫", fontSize = fontSize, modifier = Modifier.alpha(0.0f))
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun RedDisk() {
        ConnectAppTheme {
            Disk(Disk.Red)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun YellowDisk() {
        ConnectAppTheme {
            Disk(Disk.Yellow)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DiskColumnPreview() {
        val game: Game = Game()

        val array = arrayOf(Disk.Red, Disk.Red, Disk.Yellow, Disk.Red, Disk.Empty, Disk.Empty)
        ConnectAppTheme {
            DiskColumn(diskArray = array, columnNumber = 0, game::addDisk)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GridPreview() {
        val game: Game = Game()

        val array1 = arrayOf(Disk.Red, Disk.Red, Disk.Yellow, Disk.Red, Disk.Empty, Disk.Empty)
        val array2 = arrayOf(Disk.Red, Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
        val array3 = arrayOf(Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
        val grid = arrayOf(array1, array2, array3)

        ConnectAppTheme {
            DiskGrid(grid = grid, onColumnPressed = game::addDisk)
        }
    }
}