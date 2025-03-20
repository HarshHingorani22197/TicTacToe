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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.drawscope.drawLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun GameScreen(navController: NavController, playerX: String, playerO: String) {
    var board by remember { mutableStateOf(List(3) { MutableList(3) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var winningLine by remember { mutableStateOf<List<Pair<Int, Int>>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tic Tac Toe",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Player Indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PlayerIndicator(
                name = playerX,
                symbol = "X",
                isActive = currentPlayer == "X",
                symbolColor = MaterialTheme.colorScheme.primary,
                bgColor = MaterialTheme.colorScheme.primaryContainer
            )
            PlayerIndicator(
                name = playerO,
                symbol = "O",
                isActive = currentPlayer == "O",
                symbolColor = MaterialTheme.colorScheme.secondary,
                bgColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tic Tac Toe Board with Winning Line Animation
        TicTacToeBoard(board, { i, j ->
            if (board[i][j].isEmpty() && winner == null) {
                board = board.mapIndexed { rowIndex, row ->
                    row.mapIndexed { colIndex, cell ->
                        if (rowIndex == i && colIndex == j) currentPlayer else cell
                    }.toMutableList()
                }

                // Check for a winner
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

        // Winner Message
        winner?.let {
            Text(
                text = "$it Wins!",
                style = MaterialTheme.typography.headlineMedium,
                color = if (currentPlayer == "X") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Restart Button
        ElevatedButton(
            onClick = {
                board = List(3) { MutableList(3) { "" } }
                currentPlayer = "X"
                winner = null
                winningLine = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restart Game", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun TicTacToeBoard(board: List<List<String>>, onCellClick: (Int, Int) -> Unit, winningLine: List<Pair<Int, Int>>?) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(winningLine) {
        if (winningLine != null) {
            animatedProgress.snapTo(0f)
            animatedProgress.animateTo(1f, animationSpec = tween(durationMillis = 500))
        }
    }

    Box(
        modifier = Modifier
            .size(300.dp)
            .drawBehind {
                winningLine?.let { line ->
                    val startX = line.first().second * size.width / 3 + size.width / 6
                    val startY = line.first().first * size.height / 3 + size.height / 6
                    val endX = line.last().second * size.width / 3 + size.width / 6
                    val endY = line.last().first * size.height / 3 + size.height / 6

                    drawLine(
                        color = Color.Red,
                        start = Offset(startX, startY),
                        end = Offset(startX + (endX - startX) * animatedProgress.value, startY + (endY - startY) * animatedProgress.value),
                        strokeWidth = 8f
                    )
                }
            }
    ) {
        Column {
            for (i in 0..2) {
                Row {
                    for (j in 0..2) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable(enabled = board[i][j].isEmpty() && winningLine == null) {
                                    onCellClick(i, j)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = board[i][j],
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (board[i][j]) {
                                    "X" -> MaterialTheme.colorScheme.primary
                                    "O" -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerIndicator(name: String, symbol: String, isActive: Boolean, symbolColor: Color, bgColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .padding(8.dp)
            .background(
                if (isActive) bgColor else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = symbol,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = symbolColor
        )
    }
}

// Function to Check for a Winner
fun checkWinner(board: List<List<String>>): Pair<String, List<Pair<Int, Int>>>? {
    val winningPatterns = listOf(
        listOf(0 to 0, 0 to 1, 0 to 2),
        listOf(1 to 0, 1 to 1, 1 to 2),
        listOf(2 to 0, 2 to 1, 2 to 2),
        listOf(0 to 0, 1 to 0, 2 to 0),
        listOf(0 to 1, 1 to 1, 2 to 1),
        listOf(0 to 2, 1 to 2, 2 to 2),
        listOf(0 to 0, 1 to 1, 2 to 2),
        listOf(0 to 2, 1 to 1, 2 to 0)
    )

    for (pattern in winningPatterns) {
        val (a, b, c) = pattern
        if (board[a.first][a.second] == board[b.first][b.second] &&
            board[b.first][b.second] == board[c.first][c.second] &&
            board[a.first][a.second].isNotEmpty()
        ) {
            return board[a.first][a.second] to pattern
        }
    }
    return null
}
