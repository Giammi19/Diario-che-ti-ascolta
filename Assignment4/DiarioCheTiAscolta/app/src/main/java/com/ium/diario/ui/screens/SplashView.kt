package com.ium.diario.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ium.diario.navigate.Routes
import com.ium.diario.state.AppViewModel
import com.ium.diario.ui.theme.AppColors
import com.ium.diario.ui.theme.AppTypography
import kotlinx.coroutines.delay

@Composable
fun SplashView(
    navController: NavController,
    appViewModel: AppViewModel
) {
    val scale = rememberInfiniteTransition(label = "scale")
    val animatedScale by scale.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(2000) // Simula caricamento
        val profile = appViewModel.profile.value
        if (profile.onboardingCompleted) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.ONBOARDING) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.GradientPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📖",
                fontSize = 120.sp,
                modifier = Modifier.scale(animatedScale)
            )
            Text(
                text = "Diario che ti Ascolta",
                style = AppTypography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Il tuo assistente personale per eventi",
                style = AppTypography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
