LLM Usage Documentation
Mobile App Development – Whack-A-Mole Project

Tools I Used

Claude 3.5 Sonnet (Anthropic): for explaining concepts I didn’t fully get and checking bugs.

GitHub Copilot: for autocompleting Kotlin and Compose code.

ChatGPT: for quick Google-style research on Android best practices.

1. LaunchedEffect and Coroutines
Prompt I asked:

“When should I use LaunchedEffect vs rememberCoroutineScope? I’m trying to make a countdown timer that updates the UI.”

What I learned:
LaunchedEffect restarts when its key changes, while rememberCoroutineScope gives manual control. I used LaunchedEffect for the timer.

Code change:

kotlin
// Before
LaunchedEffect(Unit) {
    while(timeRemaining > 0) {
        delay(1000L)
        timeRemaining--
    }
}

// After
LaunchedEffect(isPlaying) {
    if (isPlaying) {
        timeRemaining = 30
        while (timeRemaining > 0 && isPlaying) {
            delay(1000L)
            timeRemaining--
        }
    }
}
Why: The timer wasn’t restarting before. Changing the key to isPlaying made it reset properly when a new game starts.
Takeaway: LaunchedEffect runs again only when its key changes.

2. Navigation in Compose
Prompt I asked:

“Should I use multiple Activities or one Activity with Compose Navigation for a simple game?”

What I learned:
Single-Activity apps are the modern standard. NavHost handles everything (like back stack, transitions).

Used code:

kotlin
@Composable
fun WhackAMoleApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "game") {
        composable("game") { GameScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}
Why: It’s cleaner and easier to manage than Intent switching.

3. Random Number Range
Prompt I asked:

“Is Random.nextLong() inclusive or exclusive for the upper bound?”

What I found:
The upper bound is exclusive, so to include 1000 I had to use 1001.

Fixed code:

kotlin
val randomDelay = Random.nextLong(700, 1001)
4. SharedPreferences in Composables
Prompt I asked:

“Do I have to pass Context to a Composable to use SharedPreferences?”

What I learned:
Nope. You can use LocalContext.current in Compose. Much cleaner.

Code:

kotlin
@Composable
fun GameScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    var highScore by remember { mutableStateOf(prefs.getInt("high_score", 0)) }
}
How AI Helped
Game loop (20%) – Helped me understand coroutine-based structure.

State management (15%) – Explained what should be remember vs rememberSaveable.

Navigation (10%) – Gave examples for NavHost setup.

UI (5%) – Suggested modifier combos.

SharedPreferences (10%) – Showed how to use LocalContext properly.

Overall: AI gave me ideas (40%), I did the actual logic and UI myself (60%).

What I Learned
Compose side effects: Finally understood LaunchedEffect and keys.

State hoisting: Keep state where it’s needed.

Navigation: One activity = simpler.

Coroutines: Stop them properly using isPlaying.

Material 3: Some stuff still experimental, used @OptIn to make it work.

My Honest Take
AI basically helped me learn faster. It explained stuff I would’ve spent hours Googling.
But I still had to type and test everything, so all the game logic and UI are mine. Conclusion, AI was only used for clarification, brainstorming, and small code patterns, not for generating full solutions or submitting unmodified AI code.
It’s more like I used AI as a tutor, not a shortcut.

