package com.ium.diario.ui.screens

import com.ium.diario.models.*
import com.ium.diario.state.AppViewModel
import com.ium.diario.ui.theme.*
import com.ium.diario.navigate.Routes
import com.ium.diario.state.AppTab

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────────────────────────────────────
// ConfirmView
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ConfirmView(
    bookingId     : String,
    navController : NavHostController,
    viewModel     : AppViewModel
) {
    val booking = remember(bookingId) { viewModel.bookings.value.find { it.id == bookingId } } ?: return

    var animateCheckmark by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (animateCheckmark) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val opacity by animateFloatAsState(
        targetValue = if (animateCheckmark) 1f else 0f,
        animationSpec = tween(600),
        label = "opacity"
    )

    LaunchedEffect(Unit) {
        animateCheckmark = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DiarioColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ── Icona Successo ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(DiarioColors.Green),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector       = Icons.Default.Check,
                contentDescription = null,
                tint              = Color.White,
                modifier          = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text  = "Tutto pronto!",
            style = DiarioTypography.titleLarge.copy(
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = DiarioColors.Gray900
            ),
            modifier = Modifier.alpha(opacity)
        )

        Text(
            text  = "La tua partecipazione è confermata.",
            style = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray500),
            modifier = Modifier.alpha(opacity)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ── Codice Prenotazione ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(DiarioRadius.lg)
                .background(Color.White)
                .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DiarioSpacing.md)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DiarioSpacing.xs)
            ) {
                Text(
                    text  = "Codice prenotazione",
                    style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray500)
                )
                Text(
                    text  = booking.code,
                    style = DiarioTypography.titleMedium.copy(
                        color      = DiarioColors.PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize   = 22.sp
                    ),
                    modifier = Modifier
                        .clip(DiarioRadius.sm)
                        .background(DiarioColors.PrimaryBlue.copy(alpha = 0.08f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            AppDivider()

            // ── Riepilogo rapido ────────────────────────────────────────────
            Column(
                modifier            = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DiarioSpacing.sm)
            ) {
                SummaryRow(label = "Evento", value = booking.eventTitle)
                AppDivider()
                SummaryRow(label = "Data",   value = booking.formattedDate)
                AppDivider()
                SummaryRow(label = "Posti",  value = booking.seats.toString())

                if (!booking.isFree) {
                    AppDivider()
                    SummaryRow(label = "Pagamento", value = booking.paymentMethodLabel)
                    AppDivider()
                    SummaryRow(
                        label     = "Totale",
                        value     = booking.totalLabel,
                        highlight = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(DiarioSpacing.xl))

        // ── CTA ─────────────────────────────────────────────────────────────
        Button(
            onClick  = {
                viewModel.setActiveTab(AppTab.CALENDAR)
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape  = DiarioRadius.md,
            colors = ButtonDefaults.buttonColors(containerColor = DiarioColors.PrimaryBlue)
        ) {
            Text(
                text  = "Vai al Calendario",
                style = DiarioTypography.labelLarge.copy(
                    color      = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        TextButton(
            onClick  = {
                viewModel.setActiveTab(AppTab.HOME)
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text  = "Torna alla Home",
                style = DiarioTypography.labelMedium.copy(color = DiarioColors.Gray500)
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label     : String,
    value     : String,
    highlight : Boolean = false
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text  = label,
            style = DiarioTypography.labelMedium.copy(color = DiarioColors.Gray500)
        )
        Text(
            text  = value,
            style = if (highlight) {
                DiarioTypography.labelLarge.copy(
                    color      = DiarioColors.Green,
                    fontWeight = FontWeight.Bold
                )
            } else {
                DiarioTypography.labelMedium.copy(
                    color      = DiarioColors.Gray900,
                    fontWeight = FontWeight.SemiBold
                )
            }
        )
    }
}
