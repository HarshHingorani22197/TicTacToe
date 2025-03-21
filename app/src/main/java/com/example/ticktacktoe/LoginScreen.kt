import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ticktacktoe.R

@Composable
fun LoginScreen(navController: NavController) {
    var playerX by remember { mutableStateOf("") }
    var playerO by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4A148C), Color(0xFF7B1FA2))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Tic Tac Toe",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Logo
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.applogo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(100.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Input Fields
            listOf("Player X" to Color(0xFFFFD54F), "Player O" to Color(0xFF64B5F6))
                .forEach { (label, color) ->
                    Text(
                        text = label,
                        color = color,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                    )
                    OutlinedTextField(
                        value = if (label == "Player X") playerX else playerO,
                        onValueChange = { if (label == "Player X") playerX = it else playerO = it },
                        placeholder = { Text("Enter name", color = Color.LightGray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(
                                BorderStroke(2.dp, color),
                                shape = RoundedCornerShape(12.dp)
                            )
                    )
                }

            Spacer(modifier = Modifier.height(32.dp))

            // Animated Start Button
            Button(
                onClick = {
                    if (playerX.isEmpty()) playerX = "Player 1"
                    if (playerO.isEmpty()) playerO = "Player 2"
                    navController.navigate("GameScreen/${playerX}/${playerO}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081))
            ) {
                Text(
                    "Start Normal Game",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Infinite Game Button
            Button(
                onClick = {
                    if (playerX.isEmpty()) playerX = "Player 1"
                    if (playerO.isEmpty()) playerO = "Player 2"
                    navController.navigate("GameScreen2/${playerX}/${playerO}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF40C4FF))
            ) {
                Text(
                    "Start Infinite Game",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(navController = rememberNavController())
}
