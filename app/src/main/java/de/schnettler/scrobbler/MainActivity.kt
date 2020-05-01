package de.schnettler.scrobbler

import android.icu.text.CompactDecimalFormat
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.Alignment.Companion.CenterHorizontally
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.wrapContentWidth
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.outlined.AccountCircle
import androidx.ui.material.icons.outlined.Favorite
import androidx.ui.material.icons.outlined.Home
import de.schnettler.scrobbler.components.BottomNavigationBar
import de.schnettler.scrobbler.model.MenuItem
import de.schnettler.scrobbler.screens.ChartScreen
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model: MainViewModel by viewModels()

        setContent {
            MaterialTheme {
                Scaffold(
                    topAppBar = {
                        TopAppBar(
                            title = { Text(text = "Scrobbler") }
                        )
                    },
                    bodyContent = {
                        ChartScreen(artistResponse = model.topArtists)
                    },
                    bottomAppBar = {
                        BottomNavigationBar(items = listOf(
                            MenuItem("Charts", Icons.Outlined.AccountCircle),
                            MenuItem("History", Icons.Outlined.Home),
                            MenuItem("Local", Icons.Outlined.Favorite)
                        ))
                    }
                )
            }
        }
    }
}