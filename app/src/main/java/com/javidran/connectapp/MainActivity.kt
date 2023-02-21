package com.javidran.connectapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.unit.dp
import com.javidran.connectapp.movelview.GameViewModel
import com.javidran.connectapp.ui.ActivityScreen
import com.javidran.connectapp.ui.theme.ConnectAppTheme

class MainActivity : AppCompatActivity() {

    private val gridViewModel by viewModels<GameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConnectAppTheme {
                // A surface container using the 'background' color from the theme
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
                Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)) {
                    ActivityScreen(gameViewModel = gridViewModel)
                }
            }
        }
    }
}