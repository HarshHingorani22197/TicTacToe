import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.LinkedList
import com.example.ticktacktoe.R

@Composable
fun GameScreen2(navController: NavController, playerX: String, playerO: String) {
    var board by remember { mutableStateOf(List(3) { MutableList(3) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var winningLine by remember { mutableStateOf<List<Pair<Int, Int>>?>(null) }
    val moveHistory = remember { mutableStateMapOf("X" to LinkedList<Pair<Int, Int>>(), "O" to LinkedList<Pair<Int, Int>>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF3ED)) // Light Cream Background
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PlayerIndicators(
                name = playerX,
                symbol = "X",
                isActive = currentPlayer == "X"
            )
            PlayerIndicators(
                name = playerO,
                symbol = "O",
                isActive = currentPlayer == "O"
            )
        }

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
            Text(
                text = "$it Wins!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFFF7B7B) // Red Highlight
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        ElevatedButton(
            onClick = {
                board = List(3) { MutableList(3) { "" } }
                currentPlayer = "X"
                winner = null
                winningLine = null
                moveHistory.clear()
                moveHistory["X"] = LinkedList()
                moveHistory["O"] = LinkedList()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7B7B))
        ) {
            Text("Restart Game", style = MaterialTheme.typography.titleLarge, color = Color.White)
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
fun TicTacToeBoard2(
    board: List<List<String>>,
    moveHistory: MutableMap<String, LinkedList<Pair<Int, Int>>>,
    onCellClick: (Int, Int) -> Unit,
    winningLine: List<Pair<Int, Int>>?
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(winningLine) {
        if (winningLine != null) {
            animatedProgress.snapTo(0f)
            animatedProgress.animateTo(1f, animationSpec = tween(durationMillis = 500))
        }
    }

    Box(modifier = Modifier.size(300.dp)) {
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
                                    else Color(0xFFFF7B7B).copy(alpha = 0.2f) // Light Transparent Red
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
                                    modifier = Modifier.size(60.dp)
                                )
                                "O" -> Image(
                                    painter = painterResource(id = R.drawable.circle),
                                    contentDescription = "O",
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerIndicators(name: String, symbol: String, isActive: Boolean) {
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

@Preview(showBackground = true)
@Composable
fun GameScreen2Preview() {
    GameScreen2(rememberNavController(), "Player X", "Player O")
}
