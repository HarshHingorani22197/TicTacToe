import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Player Turn Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            PlayerIndicator(name = playerX, symbol = "X", isActive = currentPlayer == "X")
            Spacer(modifier = Modifier.width(20.dp))
            PlayerIndicator(name = playerO, symbol = "O", isActive = currentPlayer == "O")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tic Tac Toe Grid
        for (i in 0..2) {
            Row {
                for (j in 0..2) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .background(Color(0xFF222244), RoundedCornerShape(8.dp))
                            .clickable(enabled = board[i][j].isEmpty() && winner == null) {
                                board = board.mapIndexed { rowIndex, row ->
                                    row.mapIndexed { colIndex, cell ->
                                        if (rowIndex == i && colIndex == j) currentPlayer else cell
                                    }.toMutableList()
                                }

                                // Check for a winner
                                winner = checkWinner(board)

                                // Switch player if no winner
                                if (winner == null) {
                                    currentPlayer = if (currentPlayer == "X") "O" else "X"
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = board[i][j],
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (board[i][j]) {
                                "X" -> Color.Red
                                "O" -> Color.Yellow
                                else -> Color.White
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Winner Message
        winner?.let {
            Text(
                text = "$it Wins!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Restart Button
        Button(
            onClick = {
                board = List(3) { MutableList(3) { "" } }
                currentPlayer = "X"
                winner = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(4.dp)
        ) {
            Text("Restart Game", fontSize = 20.sp, color = Color.White)
        }
    }
}

// Function to Check for a Winner
fun checkWinner(board: List<List<String>>): String? {
    val winningPatterns = listOf(
        // Rows
        listOf(0 to 0, 0 to 1, 0 to 2),
        listOf(1 to 0, 1 to 1, 1 to 2),
        listOf(2 to 0, 2 to 1, 2 to 2),

        // Columns
        listOf(0 to 0, 1 to 0, 2 to 0),
        listOf(0 to 1, 1 to 1, 2 to 1),
        listOf(0 to 2, 1 to 2, 2 to 2),

        // Diagonals
        listOf(0 to 0, 1 to 1, 2 to 2),
        listOf(0 to 2, 1 to 1, 2 to 0)
    )

    for (pattern in winningPatterns) {
        val (a, b, c) = pattern
        if (board[a.first][a.second] == board[b.first][b.second] &&
            board[b.first][b.second] == board[c.first][c.second] &&
            board[a.first][a.second].isNotEmpty()
        ) {
            return board[a.first][a.second]
        }
    }

    return null
}

@Composable
fun PlayerIndicator(name: String, symbol: String, isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(
                if (isActive) Color.White else Color.Black,
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = name,
                fontSize = 14.sp,
                color = if (isActive) Color.Black else Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = symbol,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = if (symbol == "X") Color.Red else Color.Yellow
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    val navController = rememberNavController()
    GameScreen(navController, playerX = "Cross", playerO = "Circle")
}
