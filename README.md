README.md - LLM Usage Declaration
As per the assignment section 5, here's a brief declaration of LLM use in this project. I used LLM for support (not substitution) in clarifying concepts, debugging errors, and brainstorming code structures. All code was adapted and tested by me.
Tools Used

Grok (built by xAI)

Example Prompts/Questions (2 examples)

"what is this" — Provided the initial code to analyze what the app does (Whack-A-Mole game in Kotlin/Compose).
"help me code step by step, because by copying and pasting the code i am lost, lets go from the start, where i have just created a new file with default configuration" — Used to break down building the app from scratch, focusing on adding features one at a time.

Parts Influenced by AI

Navigation to Settings Screen
Before: No navigation; settings icon did nothing (just a log message).
Before code (in TopAppBar actions):KotlinIconButton(onClick = { Log.d("WhackAMole", "Settings icon clicked") }) { ... }
After: Added NavHost, rememberNavController, and composable routes for "game" and "settings". Changed onClick to navController.navigate("settings"). Added SettingsScreen with back arrow.
After code:KotlinIconButton(onClick = { navController.navigate("settings") }) { ... }
Why changed: To meet core requirement for in-Compose navigation and a secondary screen with back navigation. The AI suggested the structure, but I adapted the route names and added padding for UI match to PDF screenshots.

Persistent High Score with SharedPreferences
Before: High score was in-memory only (reset on app restart).
Before code:Kotlinvar highScore by remember { mutableStateOf(0) }
if (score > highScore) { highScore = score }
After: Added LocalContext, SharedPreferences, load from prefs on init, and save with .edit().putInt().apply() on update.
After code:Kotlinval sharedPreferences = context.getSharedPreferences("WhackAMolePrefs", Context.MODE_PRIVATE)
var highScore by remember { mutableStateOf(sharedPreferences.getInt("high_score", 0)) }
if (score > highScore) {
    highScore = score
    sharedPreferences.edit().putInt("high_score", score).apply()
}
Why changed: To satisfy persistent storage requirement (load on start, update if higher). The AI explained SharedPreferences, but I tested saving across restarts and fixed variable names.


Key Takeaways/Lessons Learned

Navigation: Learned that Compose NavHost is efficient for single-activity apps, avoiding multiple Activities. Key lesson: Always use popBackStack() for back navigation to prevent stack buildup.
Persistent Storage: Understood SharedPreferences for simple key-value data is lightweight and async with .apply() — no need for Room for basic high score. Lesson: Always default to 0 on load to handle first run.
