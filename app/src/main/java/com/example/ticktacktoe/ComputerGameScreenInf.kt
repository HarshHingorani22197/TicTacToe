import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.max
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ComputerGameScreenInf(navController: NavController, player: String) {
    var board by remember { mutableStateOf(List(3) { MutableList(3) { "" } }) }
    var xMoves by remember { mutableStateOf(mutableListOf<Pair<Int, Int>>()) }
    var oMoves by remember { mutableStateOf(mutableListOf<Pair<Int, Int>>()) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var winningLine by remember { mutableStateOf<List<Pair<Int, Int>>?>(null) }
    var isDraw by remember { mutableStateOf(false) }

    LaunchedEffect(currentPlayer) {
        if (currentPlayer == "O" && winner == null && !isDraw) {
            Handler(Looper.getMainLooper()).postDelayed({
                makeComputerMove(board, oMoves)?.let { (i, j) ->
                    if (oMoves.size == 3) {
                        val (oldI, oldJ) = oMoves.removeFirst()
                        board = board.mapIndexed { rowIndex, row ->
                            row.mapIndexed { colIndex, cell ->
                                if (rowIndex == oldI && colIndex == oldJ) "" else cell
                            }.toMutableList()
                        }
                    }
                    oMoves.add(Pair(i, j))
                    board = board.mapIndexed { rowIndex, row ->
                        row.mapIndexed { colIndex, cell ->
                            if (rowIndex == i && colIndex == j) "O" else cell
                        }.toMutableList()
                    }

                    val result = checkWinner(board)
                    if (result != null) {
                        winner = "Computer"
                        winningLine = result.second
                    } else if (board.flatten().all { it.isNotEmpty() }) {
                        isDraw = true
                    } else {
                        currentPlayer = "X"
                    }
                }
            }, 500)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF3ED))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TIC TAC TOE (Limited Moves)",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
            ),
            color = Color(0xFF7B4F50),
            modifier = Modifier.padding(bottom = 25.dp, top = 50.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PlayerIndicator(name = player, symbol = "X", isActive = currentPlayer == "X")
            PlayerIndicator(name = "Computer", symbol = "O", isActive = currentPlayer == "O")
        }

        Spacer(modifier = Modifier.height(20.dp))

        TicTacToeBoard(board, { i, j ->
            if (board[i][j].isEmpty() && currentPlayer == "X" && winner == null && !isDraw) {
                if (xMoves.size == 3) {
                    val (oldI, oldJ) = xMoves.removeFirst()
                    board = board.mapIndexed { rowIndex, row ->
                        row.mapIndexed { colIndex, cell ->
                            if (rowIndex == oldI && colIndex == oldJ) "" else cell
                        }.toMutableList()
                    }
                }

                xMoves.add(Pair(i, j))
                board = board.mapIndexed { rowIndex, row ->
                    row.mapIndexed { colIndex, cell ->
                        if (rowIndex == i && colIndex == j) "X" else cell
                    }.toMutableList()
                }

                val result = checkWinner(board)
                if (result != null) {
                    winner = player
                    winningLine = result.second
                } else if (board.flatten().all { it.isNotEmpty() }) {
                    isDraw = true
                } else {
                    currentPlayer = "O"
                }
            }
        }, winningLine)

        Spacer(modifier = Modifier.height(20.dp))

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

        ElevatedButton(
            onClick = {
                board = List(3) { MutableList(3) { "" } }
                xMoves.clear()
                oMoves.clear()
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

fun makeComputerMove(board: List<List<String>>, oMoves: MutableList<Pair<Int, Int>>): Pair<Int, Int>? {
    var bestMove: Pair<Int, Int>? = null
    var bestScore = Int.MIN_VALUE

    for (i in 0..2) {
        for (j in 0..2) {
            if (board[i][j].isEmpty()) {
                val newBoard = board.map { it.toMutableList() }.toMutableList()
                newBoard[i][j] = "O"

                val score = minimax(newBoard, depth = 0, isMaximizing = false, alpha = Int.MIN_VALUE, beta = Int.MAX_VALUE)

                if (score > bestScore) {
                    bestScore = score
                    bestMove = Pair(i, j)
                }
            }
        }
    }
    return bestMove
}
