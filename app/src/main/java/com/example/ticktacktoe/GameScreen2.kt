import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.LinkedList

@Composable
fun GameScreen2(navController: NavController, playerX: String, playerO: String) {
    var board by remember { mutableStateOf(List(3) { MutableList(3) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var winningLine by remember { mutableStateOf<List<Pair<Int, Int>>?>(null) }
    val moveHistory = remember { mutableStateMapOf("X" to LinkedList<Pair<Int, Int>>(), "O" to LinkedList<Pair<Int, Int>>()) }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Tic Tac Toe", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        PlayerIndicators(playerX, playerO, currentPlayer)
        Spacer(modifier = Modifier.height(20.dp))
        TicTacToeBoard2(board, moveHistory, { i, j ->
            if (board[i][j].isEmpty() && winner == null) {
                handleMove(i, j, currentPlayer, moveHistory, board)
                val result = checkWinner(board)
                if (result != null) {
                    winner = if (result.first == "X") playerX else playerO
                    winningLine = result.second
                } else {
                    currentPlayer = if (currentPlayer == "X") "O" else "X"
                }
            }
        }, winningLine)
        Spacer(modifier = Modifier.height(20.dp))
        winner?.let {
            Text(text = "$it Wins!", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(30.dp))
        ElevatedButton(onClick = {
            board = List(3) { MutableList(3) { "" } }
            currentPlayer = "X"
            winner = null
            winningLine = null
            moveHistory.clear()
            moveHistory["X"] = LinkedList()
            moveHistory["O"] = LinkedList()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Restart Game", style = MaterialTheme.typography.titleLarge)
        }
    }
}

fun handleMove(i: Int, j: Int, currentPlayer: String, moveHistory: MutableMap<String, LinkedList<Pair<Int, Int>>>, board: List<MutableList<String>>) {
    moveHistory[currentPlayer]?.let { history ->
        if (history.size == 3) {
            val (oldI, oldJ) = history.poll()
            board[oldI][oldJ] = ""
        }
        history.add(i to j)
    }
    board[i][j] = currentPlayer
}

@Composable
fun TicTacToeBoard2(board: List<List<String>>, moveHistory: MutableMap<String, LinkedList<Pair<Int, Int>>>, onCellClick: (Int, Int) -> Unit, winningLine: List<Pair<Int, Int>>?) {
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(winningLine) {
        if (winningLine != null) {
            animatedProgress.snapTo(0f)
            animatedProgress.animateTo(1f, animationSpec = tween(durationMillis = 500))
        }
    }
    Box(modifier = Modifier.size(300.dp)
        .drawWithContent {
            drawContent() // Draws the X and O symbols first

            winningLine?.let { line ->
                val cellSize = size.width / 3
                val startX = line.first().second * cellSize + cellSize / 2
                val startY = line.first().first * cellSize + cellSize / 2
                val endX = line.last().second * cellSize + cellSize / 2
                val endY = line.last().first * cellSize + cellSize / 2

                drawLine(
                    color = Color.Red.copy(alpha = 0.8f),
                    start = Offset(startX, startY),
                    end = Offset(startX + (endX - startX) * animatedProgress.value,
                        startY + (endY - startY) * animatedProgress.value),
                    strokeWidth = 12f,
                    cap = StrokeCap.Round
                )
            }
        }
    ) {
        Column {
            for (i in 0..2) {
                Row {
                    for (j in 0..2) {
                        Box(modifier = Modifier.size(100.dp).padding(4.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).clickable(enabled = board[i][j].isEmpty() && winningLine == null) {
                            onCellClick(i, j)
                        }, contentAlignment = Alignment.Center) {
                            Text(text = board[i][j], fontSize = 36.sp, color = when (board[i][j]) {
                                "X" -> MaterialTheme.colorScheme.primary
                                "O" -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.onSurface
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerIndicators(playerX: String, playerO: String, currentPlayer: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        PlayerIndicator2(playerX, "X", currentPlayer == "X", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
        PlayerIndicator2(playerO, "O", currentPlayer == "O", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer)
    }
}

@Composable
fun PlayerIndicator2(name: String, symbol: String, isActive: Boolean, symbolColor: Color, bgColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(120.dp).padding(8.dp).background(if (isActive) bgColor else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)).padding(12.dp)) {
        Text(text = name, style = MaterialTheme.typography.titleMedium, color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface)
        Text(text = symbol, fontSize = 36.sp, color = symbolColor)
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreen2Preview() {
    GameScreen2(rememberNavController(), "Player X", "Player O")
}
