package com.javidran.connectapp.ui

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.javidran.connectapp.R
import com.javidran.connectapp.datasource.Game
import com.javidran.connectapp.enums.Disk
import com.javidran.connectapp.enums.Player
import com.javidran.connectapp.movelview.GameViewModel
import com.javidran.connectapp.ui.theme.ConnectAppTheme
import com.javidran.connectapp.ui.theme.LineColor
import com.javidran.connectapp.ui.theme.RedPlayerColor
import com.javidran.connectapp.ui.theme.YellowPlayerColor

@Composable
fun ActivityScreen(gameViewModel: GameViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
        if (gameViewModel.tie.value) {
            Text(stringResource(R.string.tie), fontSize = 40.sp)
        } else {
            WinnerText(gameViewModel.winner.value)
        }
        DiskGrid(
            grid = gameViewModel.grid.value,
            onColumnPressed = { },
            winners = gameViewModel.winningCombination.value
        )
    } else {
        Turn(player = gameViewModel.turn.value)
        DiskGrid(
            grid = gameViewModel.grid.value,
            onColumnPressed = gameViewModel::addDisk,
            winners = emptyList()
        )
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
        Text(stringResource(R.string.new_game))
    }
    Button(onClick = gameViewModel::resetAllGames, Modifier.padding(bottom = 10.dp)) {
        Text(stringResource(R.string.reset_win_count))
    }
}

@Composable
fun WonGames(player: Player, numberOfWonGames: Int) {
    Text(
        text = translatePlayerName(player, context = LocalContext.current),
        color = chooseColor(player)
    )
    Text(stringResource(R.string.number_of_won_games_part1) + numberOfWonGames + stringResource(R.string.number_of_won_games_part2))
}

@Composable
fun Turn(player: Player) {
    Row(Modifier.padding(10.dp)) {
        Text(text = stringResource(R.string.turn_part1))
        PlayerText(player = player)
        Text(text = stringResource(R.string.turn_part2))
    }
}

@Composable
fun WinnerText(
    winner: Player
) {
    val winnerText = winner.toString().uppercase()
    PlayerText(
        text = stringResource(R.string.winner_part1) + winnerText + stringResource(R.string.winner_part2),
        player = winner,
        fontSize = 40.sp
    )
}

@Composable
fun PlayerText(
    text: String? = null,
    player: Player,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    val internalText: String = text ?: translatePlayerName(player, context = LocalContext.current)
    Text(
        text = internalText,
        color = chooseColor(player),
        fontSize = fontSize
    )
}

private fun chooseColor(player: Player): Color {
    return if (player == Player.Red) {
        RedPlayerColor
    } else {
        YellowPlayerColor
    }
}

private fun translatePlayerName(player: Player, context: Context): String {
    return if (player == Player.Red) {
        context.getString(R.string.red)
    } else {
        context.getString(R.string.yellow)
    }
}

@Composable
fun DiskGrid(
    grid: Array<Array<Disk>>,
    onColumnPressed: (column: Int) -> Unit,
    winners: List<Pair<Int, Int>>
) {
    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(color = LineColor, thickness = Dp.Hairline)
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Spacer(modifier = Modifier.weight(1.0f))
            grid.forEachIndexed { index, arrayOfDisks ->
                if (index == 0) {
                    Divider(
                        color = LineColor,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )
                }
                DiskColumn(
                    diskArray = arrayOfDisks,
                    columnNumber = index,
                    onColumnPressed = onColumnPressed,
                    winners = winners.filter { pair -> pair.first == index }
                        .map { pair -> pair.second }
                )
                Divider(
                    color = LineColor,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1.0f))
        }
        Divider(color = LineColor, thickness = Dp.Hairline)
    }
}

@Composable
fun DiskColumn(
    diskArray: Array<Disk>,
    columnNumber: Int,
    onColumnPressed: (column: Int) -> Unit,
    winners: List<Int>
) {
    Column(
        Modifier
            .width(IntrinsicSize.Min)
            .clickable { onColumnPressed(columnNumber) }
    ) {
        for (i in diskArray.indices.reversed()) {
            if (i == 0) {
                Divider(color = LineColor, thickness = Dp.Hairline)
            }
            Disk(diskArray[i], winners.contains(i))
            Divider(color = LineColor, thickness = Dp.Hairline)
        }
    }
}

@Composable
fun Disk(disk: Disk, winner: Boolean, modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(2.dp)) {
        when (disk) {
            Disk.Red -> {
                if (winner) {
                    DiskImage(id = R.drawable.winner_red_disk)
                } else {
                    DiskImage(id = R.drawable.red_disk)
                }
            }
            Disk.Yellow -> {
                if (winner) {
                    DiskImage(id = R.drawable.winner_yellow_disk)
                } else {
                    DiskImage(id = R.drawable.yellow_disk)
                }
            }
            else -> {
                DiskImage(id = R.drawable.empty_disk, modifier = Modifier.alpha(0f))
            }
        }
    }
}

@Composable
fun DiskImage(@DrawableRes id: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = id),
        contentDescription = null,
        modifier = modifier.size(50.dp)
    )
}

@Preview
@Composable
fun DiskImagePreview() {
    ConnectAppTheme {
        Column {
            DiskImage(id = R.drawable.red_disk)
            DiskImage(id = R.drawable.winner_red_disk)
            DiskImage(id = R.drawable.yellow_disk)
            DiskImage(id = R.drawable.winner_yellow_disk)
            DiskImage(id = R.drawable.empty_disk)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RedDisk() {
    ConnectAppTheme {
        Row {
            Disk(Disk.Red, false)
            Disk(Disk.Red, true)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun YellowDisk() {
    ConnectAppTheme {
        Row {
            Disk(Disk.Yellow, false)
            Disk(Disk.Yellow, true)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiskColumnPreview() {
    val game = Game()

    val array = arrayOf(Disk.Red, Disk.Red, Disk.Yellow, Disk.Red, Disk.Empty, Disk.Empty)
    ConnectAppTheme {
        DiskColumn(diskArray = array, columnNumber = 0, game::addDisk, emptyList())
    }
}

@Preview(showBackground = true)
@Composable
fun GridPreview() {
    val game = Game()

    val array1 = arrayOf(Disk.Red, Disk.Red, Disk.Yellow, Disk.Red, Disk.Empty, Disk.Empty)
    val array2 = arrayOf(Disk.Red, Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val array3 = arrayOf(Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val grid = arrayOf(array1, array2, array3)

    ConnectAppTheme {
        DiskGrid(grid = grid, onColumnPressed = game::addDisk, emptyList())
    }
}