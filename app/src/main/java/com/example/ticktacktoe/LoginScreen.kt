import android.widget.Toast
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ticktacktoe.R

@Composable
fun LoginScreen(navController: NavController) {
    var playerX by remember { mutableStateOf("") }
    var playerO by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    // Typing Effect for Title
    var displayedText by remember { mutableStateOf("") }
    val fullText = "TIC TAC TOE"

    LaunchedEffect(Unit) {
        displayedText = ""
        for (i in fullText.indices) {
            displayedText = fullText.substring(0, i + 1)
            delay(200)
        }
    }

    // Logo Rotation Animation
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(durationMillis = 3000, easing = EaseInOut)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF3ED)) // Light Cream Background
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = displayedText,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    shadow = Shadow(color = Color.Black, blurRadius = 4f)
                ),
                color = Color(0xFF7B4F50) // Darker shade for contrast
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF7B7B).copy(alpha = 0.2f)) // Light Transparent Red
                    .border(4.dp, Color(0xFFFF7B7B), CircleShape)
                    .clickable {
                        coroutineScope.launch {
                            rotation.snapTo(0f)
                            rotation.animateTo(
                                targetValue = 360f,
                                animationSpec = tween(durationMillis = 2000, easing = EaseInOut)
                            )
                        }
                    }
                    .graphicsLayer(rotationZ = rotation.value),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(id = R.drawable.applogo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(100.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Player Name Input Fields
            listOf("Player X" to Color(0xFF7B4F50), "Player O" to Color(0xFF7B4F50)).forEach { (label, color) ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = label,
                        color = color,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        modifier = Modifier.align(Alignment.Start) // Align label to the left
                    )
                    OutlinedTextField(
                        value = if (label == "Player X") playerX else playerO,
                        onValueChange = { if (label == "Player X") playerX = it else playerO = it },
                        placeholder = { Text("Enter name", color = Color.LightGray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start), // Ensures left alignment
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(BorderStroke(2.dp, color), shape = RoundedCornerShape(6.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Start Normal Game Button
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7B7B))
            ) {
                Text(
                    "Start Normal Game",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Infinite Game Button
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7B7B))
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
