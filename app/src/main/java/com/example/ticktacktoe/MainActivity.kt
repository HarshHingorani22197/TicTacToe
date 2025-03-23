package com.example.ticktacktoe
import ComputerGameScreen
import ComputerGameScreenInf
import GameScreen
import GameScreen2
import LoginScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ticktacktoe.ui.theme.TickTackToeTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TickTackToeTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "loginScreen", modifier = modifier) {
        composable("loginScreen") { LoginScreen(navController) }

        composable("gameScreen/{playerX}/{playerO}") { backStackEntry ->
            val playerX = backStackEntry.arguments?.getString("playerX") ?: "Player X"
            val playerO = backStackEntry.arguments?.getString("playerO") ?: "Player O"

            GameScreen(navController, playerX, playerO)
        }

        composable("gameScreen2/{playerX}/{playerO}") { backStackEntry ->
            val playerX = backStackEntry.arguments?.getString("playerX") ?: "Player X"
            val playerO = backStackEntry.arguments?.getString("playerO") ?: "Player O"

            GameScreen2(navController, playerX, playerO)
        }

        // New screen for playing against the computer
        composable("computerGameScreen/guneet") { backStackEntry ->
            val player = backStackEntry.arguments?.getString("player") ?: "Player"

            ComputerGameScreen(navController, player)
        }

        // New screen for playing against the computer
        composable("computerGameScreenInf/guneet") { backStackEntry ->
            val player = backStackEntry.arguments?.getString("player") ?: "Player"

            ComputerGameScreenInf(navController, player)
        }
    }
}
