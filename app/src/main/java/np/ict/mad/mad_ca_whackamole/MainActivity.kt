package np.ict.mad.mad_ca_whackamole

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random
import np.ict.mad.mad_ca_whackamole.ui.theme.MAD_CA_WhackAMoleTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.ExperimentalMaterial3Api

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAD_CA_WhackAMoleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WhackAMoleApp()
                }
            }
        }
    }
}

@Composable
fun WhackAMoleApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "game") {
        composable("game") {
            GameScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    var isPlaying by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var timeRemaining by remember { mutableStateOf(30) }
    var highScore by remember { mutableStateOf(prefs.getInt("high_score", 0)) }
    var moleIndex by remember { mutableStateOf(-1) }
    var showGameOver by remember { mutableStateOf(false) }

    // Timer countdown
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            timeRemaining = 30
            while (timeRemaining > 0 && isPlaying) {
                delay(1000L)
                timeRemaining--
            }
            isPlaying = false
            if (score > highScore) {
                highScore = score
                prefs.edit().putInt("high_score", score).apply()
            }
            showGameOver = true
            Log.d("WhackAMole", "Time's up! Final score: $score | High: $highScore")
        }
    }

    // Mole random appearance
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                val randomDelay = Random.nextLong(700, 1001)
                delay(randomDelay)
                if (isPlaying) {
                    moleIndex = Random.nextInt(9)
                    Log.d("WhackAMole", "Mole at position $moleIndex")
                }
            }
            moleIndex = -1
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // TopAppBar at the top
        TopAppBar(
            title = { Text("Whack-A-Mole") },
            actions = {
                IconButton(onClick = {
                    navController.navigate("settings")
                    Log.d("WhackAMole", "Navigating to Settings")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        )

        // Main content below TopAppBar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Score and Time in a Row (as per requirements)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Time: $timeRemaining",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (timeRemaining <= 10)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // High Score Display
            Text(
                text = "High Score: $highScore",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3x3 Grid of holes
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(3) { col ->
                            val index = row * 3 + col
                            val isMoleHere = index == moleIndex && isPlaying

                            Button(
                                onClick = {
                                    if (isPlaying && isMoleHere) {
                                        score += 1
                                        moleIndex = Random.nextInt(9)
                                        Log.d("WhackAMole", "Hit! Score: $score")
                                    }
                                },
                                modifier = Modifier.size(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMoleHere)
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = if (isMoleHere) "🦫" else "⚫",
                                    style = MaterialTheme.typography.headlineLarge
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start / Restart button
            Button(
                onClick = {
                    isPlaying = true
                    score = 0
                    timeRemaining = 30
                    moleIndex = -1
                    showGameOver = false
                    Log.d("WhackAMole", "Game started")
                },
                enabled = !isPlaying,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = if (isPlaying) "Playing..." else "Start Game",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Game Over Dialog
    if (showGameOver) {
        AlertDialog(
            onDismissRequest = { showGameOver = false },
            title = { Text("Game Over!") },
            text = {
                Column {
                    Text("Final Score: $score")
                    if (score == highScore && score > 0) {
                        Text("New High Score! 🎉", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGameOver = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                    Log.d("WhackAMole", "Back to Game")
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Game settings will be available here.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}