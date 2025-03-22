import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticktacktoe.R

@Composable
fun GameScreen(navController: NavController, playerX: String, playerO: String) {
    var board by remember { mutableStateOf(List(3) { MutableList(3) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var winningLine by remember { mutableStateOf<List<Pair<Int, Int>>?>(null) }
    var isDraw by remember { mutableStateOf(false) }

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
                symbolColor = MaterialTheme.colorScheme.primary,
                bgColor = MaterialTheme.colorScheme.primaryContainer
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tic Tac Toe Board
        TicTacToeBoard(board, { i, j ->
            if (board[i][j].isEmpty() && winner == null && !isDraw) {
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
                } else if (board.flatten().all { it.isNotEmpty() }) {
                    isDraw = true
                } else {
                    currentPlayer = if (currentPlayer == "X") "O" else "X"
                }
            }
        }, winningLine)

        Spacer(modifier = Modifier.height(20.dp))

        // Winner or Draw Message
        when {
            winner != null -> Text(
                text = "$winner Wins!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            isDraw -> Text(
                text = "Game Drawn!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
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
                isDraw = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restart Game", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun TicTacToeBoard(board: List<List<String>>, onCellClick: (Int, Int) -> Unit, winningLine: List<Pair<Int, Int>>?) {
    Column {
        for (i in 0..2) {
            Row {
                for (j in 0..2) {
                    val isWinningCell = winningLine?.contains(i to j) == true
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isWinningCell) Color.Green.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable(enabled = board[i][j].isEmpty() && winningLine == null) {
                                onCellClick(i, j)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (board[i][j]) {
                            "X" -> Image(
                                painter = painterResource(id = R.drawable.cross),
                                contentDescription = "X",
                                modifier = Modifier.size(64.dp)
                            )
                            "O" -> Image(
                                painter = painterResource(id = R.drawable.circle),
                                contentDescription = "O",
                                modifier = Modifier.size(64.dp)
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
            .width(130.dp)
            .height(110.dp)
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
        Spacer(modifier = Modifier.height(7.dp))
        Image(
            painter = painterResource(id = if (symbol == "X") R.drawable.cross else R.drawable.circle),
            contentDescription = symbol,
            modifier = Modifier.size(36.dp)
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
