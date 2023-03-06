package com.javidran.connectapp.ui

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.javidran.connectapp.R
import com.javidran.connectapp.enums.Disk
import com.javidran.connectapp.enums.Player
import com.javidran.connectapp.movelview.GameViewModel
import com.javidran.connectapp.ui.theme.ConnectAppTheme
import com.javidran.connectapp.ui.theme.RedPlayerColor
import com.javidran.connectapp.ui.theme.YellowPlayerColor

@Composable
fun ActivityScreen(gameViewModel: GameViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        WinCount(gameViewModel)
        Spacer(modifier = Modifier.weight(1.0f))
        GameZone(gameViewModel, modifier = Modifier.weight(2.0f) )
        Spacer(modifier = Modifier.weight(1.0f))
        ResetButtons(gameViewModel)
    }
}

@Composable
private fun GameZone(gameViewModel: GameViewModel, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxWidth()) {
        if (gameViewModel.winner.value != Player.None || gameViewModel.tie.value) {
            if (gameViewModel.tie.value) {
                Text(stringResource(R.string.tie), fontSize = 40.sp)
            } else {
                WinnerText(gameViewModel.winner.value)
            }
            Board(
                grid = gameViewModel.grid.value,
                onColumnPressed = { },
                winners = gameViewModel.winningCombination.value,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Turn(player = gameViewModel.turn.value)
            Board(
                grid = gameViewModel.grid.value,
                onColumnPressed = gameViewModel::addDisk,
                winners = emptyList(),
                modifier = Modifier.fillMaxWidth()
            )
        }
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
fun Board(
    grid: Array<Array<Disk>>,
    onColumnPressed: (column: Int) -> Unit,
    winners: List<Pair<Int, Int>>,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier.padding(10.dp)) {
        val (boardGrid, boardImage) = createRefs()

        BoardImage(modifier = Modifier.constrainAs(boardImage) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
            start.linkTo(parent.start)
        })

        BoardGrid(grid = grid, onColumnPressed = onColumnPressed, winners = winners, modifier = Modifier.constrainAs(boardGrid) {
            top.linkTo(boardImage.top)
            bottom.linkTo(boardImage.bottom)
            end.linkTo(boardImage.end)
            start.linkTo(boardImage.start)
        })
    }
}

@Composable
fun BoardGrid(
    grid: Array<Array<Disk>>,
    onColumnPressed: (column: Int) -> Unit,
    winners: List<Pair<Int, Int>>,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        grid.forEachIndexed { index, arrayOfDisks ->
            BoardColumn(
                diskArray = arrayOfDisks,
                columnNumber = index,
                onColumnPressed = onColumnPressed,
                winners = winners
                    .filter { pair -> pair.first == index }
                    .map { pair -> pair.second },
                modifier = modifier.decideDiskColumnWidthByPercentage().weight(1f)
            )
        }
        DiskSpacer(isVertical = true, modifier = modifier.weight(0.5f))
    }
}

fun Modifier.decideDiskColumnWidthByPercentage(): Modifier =
    this.layout { measurable, constraints ->
        val maxWidthAllowedByParent = constraints.maxWidth
        val placeable = measurable.measure(
            constraints.copy(minWidth = (maxWidthAllowedByParent / 43)*6)
        )

        layout(placeable.width, placeable.height) {
            placeable.parentData
        }
    }

@Composable
fun BoardColumn(
    diskArray: Array<Disk>,
    columnNumber: Int,
    onColumnPressed: (column: Int) -> Unit,
    winners: List<Int>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onColumnPressed(columnNumber) }
    ) {
        for (i in diskArray.indices.reversed()) {
            Disk(
                disk = diskArray[i],
                winner = winners.contains(i)
            )
            if(i == 0) {
                DiskSpacer()
            }
        }
    }
}

@Composable
fun Disk(disk: Disk, winner: Boolean, modifier: Modifier = Modifier) {
    when (disk) {
        Disk.Red -> {
            if (winner) {
                DiskImage(id = R.drawable.winner_red_disk, modifier = modifier)
            } else {
                DiskImage(id = R.drawable.red_disk, modifier = modifier)
            }
        }
        Disk.Yellow -> {
            if (winner) {
                DiskImage(id = R.drawable.winner_yellow_disk, modifier = modifier)
            } else {
                DiskImage(id = R.drawable.yellow_disk, modifier = modifier)
            }
        }
        else -> {
            DiskImage(id = R.drawable.empty_disk, modifier = modifier)
        }
    }
}

@Composable
fun DiskImage(@DrawableRes id: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = id),
        contentScale = ContentScale.Fit,
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
}

@Composable
fun BoardImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.board),
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
}

@Composable
fun DiskSpacer(modifier: Modifier = Modifier, isVertical: Boolean = false,) {
    val id = if(isVertical) R.drawable.disk_column_spacer else R.drawable.disk_row_spacer
    val internalModifier = if(isVertical) {
        modifier.wrapContentHeight().wrapContentWidth()
    } else {
        modifier.fillMaxWidth().wrapContentHeight()
    }

    Image(
        painter = painterResource(id),
        contentScale = ContentScale.Fit,
        contentDescription = null,
        modifier = internalModifier
    )
}

@Preview
@Composable
fun DiskImagePreview() {
    ConnectAppTheme {
        Column {
            DiskImage(id = R.drawable.red_disk, modifier = Modifier.size(50.dp))
            DiskImage(id = R.drawable.winner_red_disk, modifier = Modifier.size(50.dp))
            DiskImage(id = R.drawable.yellow_disk, modifier = Modifier.size(50.dp))
            DiskImage(id = R.drawable.winner_yellow_disk, modifier = Modifier.size(50.dp))
            DiskImage(id = R.drawable.empty_disk, modifier = Modifier.size(50.dp))
        }
    }
}

@Preview
@Composable
fun DisksPreview() {
    ConnectAppTheme {
        Column {
            Disk(Disk.Red, false, modifier = Modifier.size(50.dp))
            Disk(Disk.Red, true, modifier = Modifier.size(50.dp))
            Disk(Disk.Empty, false, modifier = Modifier.size(50.dp))
            Disk(Disk.Empty, true, modifier = Modifier.size(50.dp))
            Disk(Disk.Yellow, false, modifier = Modifier.size(50.dp))
            Disk(Disk.Yellow, true, modifier = Modifier.size(50.dp))
        }
    }
}

@Preview
@Composable
fun DiskColumnPreview() {
    val array = arrayOf(Disk.Red, Disk.Red, Disk.Yellow, Disk.Red, Disk.Empty, Disk.Empty)
    ConnectAppTheme {
        Box(Modifier.width(50.dp)) {
            BoardColumn(
                diskArray = array,
                columnNumber = 0,
                onColumnPressed = { },
                winners = emptyList()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BoardGridPreview() {
    val array1 = arrayOf(Disk.Red, Disk.Red, Disk.Yellow, Disk.Red, Disk.Empty, Disk.Empty)
    val array2 = arrayOf(Disk.Red, Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val array3 = arrayOf(Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val array4 = arrayOf(Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val array5 = arrayOf(Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val array6 = arrayOf(Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val array7 = arrayOf(Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val grid = arrayOf(array1, array2, array3, array4, array5, array6, array7)

    ConnectAppTheme {
        Box(Modifier.fillMaxSize()) {
            BoardGrid(
                grid = grid,
                onColumnPressed = { },
                winners = emptyList()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BoardPreview() {
    val array1 = arrayOf(Disk.Red, Disk.Red, Disk.Yellow, Disk.Red, Disk.Empty, Disk.Empty)
    val array2 = arrayOf(Disk.Red, Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val array3 = arrayOf(Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val array4 = arrayOf(Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val array5 = arrayOf(Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val array6 = arrayOf(Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val array7 = arrayOf(Disk.Red, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty, Disk.Empty)
    val grid = arrayOf(array1, array2, array3, array4, array5, array6, array7)

    ConnectAppTheme {
        Box(Modifier.fillMaxSize()) {
            Board(
                grid = grid,
                onColumnPressed = { },
                winners = emptyList(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}