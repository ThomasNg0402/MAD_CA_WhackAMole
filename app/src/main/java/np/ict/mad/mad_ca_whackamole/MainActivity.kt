package np.ict.mad.mad_ca_whackamole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random
import android.content.Context
import android.util.Log
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import np.ict.mad.mad_ca_whackamole.ui.theme.MAD_CA_WhackAMoleTheme

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
    val sharedPreferences = context.getSharedPreferences("WhackAMolePrefs", Context.MODE_PRIVATE)

    var isPlaying by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var timeRemaining by remember { mutableStateOf(30) }
    var highScore by remember { mutableStateOf(sharedPreferences.getInt("high_score", 0)) }
    var moleIndex by remember { mutableStateOf(-1) }  // -1 means no mole visible at start
    var showGameOver by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            timeRemaining = 30   // reset timer when game starts
            while (timeRemaining > 0 && isPlaying) {
                delay(1000L)     // wait 1 second
                timeRemaining--
            }
            // When loop ends (time == 0 or stopped manually)
            isPlaying = false
            if (score > highScore) {
                highScore = score
                sharedPreferences.edit().putInt("high_score", score).apply()
            }
            showGameOver = true
            android.util.Log.d("WhackAMole","Time's up! Final Score: $score")
        }
    }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                // Random delay between 700ms and 1000ms
                val randomDelay = Random.nextLong(700, 1001)
                delay(randomDelay)

                if (isPlaying) {
                    moleIndex = Random.nextInt(9)  // 0 to 8
                    Log.d("WhackAMole", "Mole appeared at position $moleIndex")
                }
            }
            // When game stops, hide mole
            moleIndex = -1
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = { Text("Whack-A-Mole") },
            actions = {
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            }
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Time: $timeRemaining",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (!isPlaying) {
                Text(
                    text = "Tap to start",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "High Score: $highScore",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(32.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (row in 0..2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            val isMole = index == moleIndex && isPlaying

                            Button(
                                onClick = {
                                    if (isPlaying && index == moleIndex) {
                                        score++
                                        moleIndex = Random.nextInt(9)
                                    }
                                },
                                modifier = Modifier.size(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMole)
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = if (isMole) "🦫" else "⚫",
                                    style = MaterialTheme.typography.headlineLarge
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    score = 0
                    timeRemaining = 30
                    moleIndex = Random.nextInt(9)
                    isPlaying = true
                    showGameOver = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = if (isPlaying) "Restart Game" else "Start Game",
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
                    if (score == highScore) {
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
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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