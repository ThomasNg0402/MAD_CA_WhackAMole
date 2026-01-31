package np.ict.mad.mad_ca_whackamole

import android.os.Bundle
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
import android.content.Context
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAD_CA_WhackAMoleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen()
                }
            }
        }
    }
}

@Composable
fun GameScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("WhackAMolePrefs", Context.MODE_PRIVATE)

    var score by remember { mutableStateOf(0) }
    var timeRemaining by remember { mutableStateOf(30) }
    var moleIndex by remember { mutableStateOf(Random.nextInt(9)) }
    var isGameRunning by remember { mutableStateOf(false) }
    var showGameOverDialog by remember { mutableStateOf(false) }
    var highScore by remember { mutableStateOf(sharedPreferences.getInt("high_score", 0)) }

    // Timer effect - decreases time every second
    LaunchedEffect(isGameRunning) {
        if (isGameRunning) {
            while (timeRemaining > 0 && isGameRunning) {
                delay(1000)
                timeRemaining--
            }
            if (timeRemaining == 0) {
                isGameRunning = false
                // Update high score if current score is higher
                if (score > highScore) {
                    highScore = score
                    sharedPreferences.edit().putInt("high_score", score).apply()
                }
                showGameOverDialog = true
            }
        }
    }

    // Mole movement effect - moves mole every 700-1000ms
    LaunchedEffect(isGameRunning) {
        if (isGameRunning) {
            while (isGameRunning) {
                val randomDelay = Random.nextLong(700, 1001)
                delay(randomDelay)
                if (isGameRunning) {
                    moleIndex = Random.nextInt(9)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // TopAppBar
        TopAppBar(
            title = { Text("Whack-A-Mole") },
            actions = {
                IconButton(onClick = { /* Navigate to settings */ }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Score and Time Display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Score: $score",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Time: $timeRemaining",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // High Score Display
        Text(
            text = "High Score: $highScore",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3x3 Grid of holes
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
                        val isMole = index == moleIndex && isGameRunning

                        Button(
                            onClick = {
                                if (isGameRunning && index == moleIndex) {
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

        // Start/Restart Button
        Button(
            onClick = {
                score = 0
                timeRemaining = 30
                moleIndex = Random.nextInt(9)
                isGameRunning = true
                showGameOverDialog = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = if (isGameRunning) "Restart Game" else "Start Game",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Game Over Dialog
if (showGameOverDialog) {
    AlertDialog(
        onDismissRequest = { showGameOverDialog = false },
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
            TextButton(onClick = { showGameOverDialog = false }) {
                Text("OK")
            }
        }
    )
}
}