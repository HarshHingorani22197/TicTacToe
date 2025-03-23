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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(Color(0xFFFBF3ED)) // Set background color
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text ="TIC TAC TOE",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
            ),
            color = Color(0xFF7B4F50),
            modifier = Modifier
                .padding(bottom = 25.dp, top = 50.dp),

        )

        Spacer(modifier = Modifier.height(24.dp))

        // Player Indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PlayerIndicator(name = playerX, symbol = "X", isActive = currentPlayer == "X")
            PlayerIndicator(name = playerO, symbol = "O", isActive = currentPlayer == "O")
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
                color = Color(0xFF7B7B7B)
            )
            isDraw -> Text(
                text = "Game Drawn!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF7B7B7B)
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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFFFF7B7B))
        ) {
            Text("Restart Game", style = MaterialTheme.typography.titleLarge, color = Color.White)
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
                                else Color(0x33FF7B7B)
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
fun PlayerIndicator(name: String, symbol: String, isActive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(130.dp)
            .height(110.dp)
            .padding(8.dp)
            .background(
                if (isActive) Color(0xFFFF7B7B).copy(alpha = 0.2f) // Highlighted background
                else Color(0xFFFBF3ED), // Normal background
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = if (isActive) Color(0xFFFF7B7B) else Color(0xFF7B4F50)
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
