package np.ict.mad.mad_ca_whackamole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                    GameScreen()               // ← must stay here, inside setContent
                }
            }
        }
    }
}

// This must have @Composable
@Composable
fun GameScreen() {
    Text(text = "Whack-A-Mole\nTap to start")
}