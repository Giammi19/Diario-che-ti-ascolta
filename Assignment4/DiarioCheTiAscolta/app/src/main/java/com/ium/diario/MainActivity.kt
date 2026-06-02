package com.ium.diario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ium.diario.navigate.AppNavigation
import com.ium.diario.state.AppViewModel
import com.ium.diario.ui.theme.DiarioCheTiAscoltaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = viewModel()
            DiarioCheTiAscoltaTheme {
                AppNavigation(appViewModel)
            }
        }
    }
}
