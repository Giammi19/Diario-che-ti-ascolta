package com.ium.diario.ui.screens

import com.ium.diario.models.*
import com.ium.diario.state.AppViewModel
import com.ium.diario.ui.theme.*
import com.ium.diario.navigate.Routes

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────────────────────────────────────
// BookingView
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingView(
    eventId       : String,
    navController : NavHostController,
    viewModel     : AppViewModel
) {
    val event = remember(eventId) { EventData.find(id = eventId) } ?: return

    var seats      by remember { mutableIntStateOf(1) }
    var seatsError by remember { mutableStateOf<String?>(null) }

    val total      = event.pricePerPerson * seats
    val totalLabel = if (total == 0.0) "Gratuito" else "€ ${"%.2f".format(total)}"
    val seatsLabel = if (seats == 1) "1 posto" else "$seats posti"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = "Prenota",
                        style = DiarioTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector       = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = DiarioColors.Background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Riepilogo evento ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DiarioRadius.lg)
                    .background(Color.White)
                    .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment     = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 72.dp, height = 80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DiarioColors.Gray100),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = event.emoji, fontSize = 28.sp)
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier            = Modifier.weight(1f)
                ) {
                    Text(
                        text  = event.title,
                        style = DiarioTypography.labelLarge.copy(
                            color      = DiarioColors.Gray900,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text  = event.venue,
                        style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray400)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconLabelSmall(icon = "clock",    label = "${event.time} · ${event.duration}")
                        IconLabelSmall(icon = "location", label = event.walkLabel)
                    }
                    if (event.isFree) FreeTag() else AtmosphereTag(label = event.crowd)
                }
            }

            // ── Selettore posti ───────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(DiarioRadius.sm)
                        .background(DiarioColors.Gray50)
                        .border(
                            width = 1.5.dp,
                            color = if (seatsError != null) DiarioColors.Red else DiarioColors.Gray200,
                            shape = DiarioRadius.sm
                        )
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Bottone −
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick  = {
                                if (seats > 1) { seats--; seatsError = null }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector       = Icons.Default.Remove,
                                contentDescription = "Diminuisci",
                                tint              = DiarioColors.PrimaryBlue,
                                modifier          = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Contatore centrale
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text  = "$seats",
                            style = DiarioTypography.displayLarge.copy(
                                color      = DiarioColors.Gray900,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 26.sp
                            )
                        )
                        Text(
                            text  = "max ${event.maxSeats} posti disponibili",
                            style = DiarioTypography.labelSmall.copy(
                                color    = DiarioColors.Gray400,
                                fontSize = 10.sp
                            )
                        )
                    }

                    // Bottone +
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick  = {
                                if (seats < event.maxSeats) { seats++; seatsError = null }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector       = Icons.Default.Add,
                                contentDescription = "Aumenta",
                                tint              = DiarioColors.PrimaryBlue,
                                modifier          = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (seatsError != null) {
                    Text(
                        text  = seatsError!!,
                        style = DiarioTypography.labelSmall.copy(color = DiarioColors.Red)
                    )
                }
            }

            AppDivider()

            // ── Riepilogo prezzo ──────────────────────────────────────────────
            SectionLabel(text = "Riepilogo")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DiarioRadius.sm)
                    .background(DiarioColors.Gray50)
                    .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.sm)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector       = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint              = DiarioColors.Gray600,
                        modifier          = Modifier.size(18.dp)
                    )
                    Text(
                        text  = seatsLabel,
                        style = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray900)
                    )
                }
                Text(
                    text  = totalLabel,
                    style = DiarioTypography.labelLarge.copy(
                        color      = DiarioColors.Green,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Banner cancellazione
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DiarioRadius.sm)
                    .background(DiarioColors.PrimaryBlueLight)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector       = Icons.Default.Security,
                    contentDescription = null,
                    tint              = DiarioColors.PrimaryBlue,
                    modifier          = Modifier.size(18.dp)
                )
                Text(
                    text  = "Cancellazione gratuita fino a 2h prima",
                    style = DiarioTypography.labelMedium.copy(color = DiarioColors.PrimaryBlue)
                )
            }

            Text(
                text      = "I tuoi dati non verranno condivisi con terze parti.",
                style     = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray400),
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )

            // ── CTA ───────────────────────────────────────────────────────────
            if (event.isFree) {
                Button(
                    onClick  = {
                        viewModel.bookingSeats = seats
                        viewModel.confirmBooking(eventId = event.id, seats = seats, paymentMethod = null)
                        val lastBooking = viewModel.bookings.value.lastOrNull()
                        if (lastBooking != null) {
                            navController.navigate(Routes.confirm(lastBooking.id)) {
                                popUpTo(Routes.HOME)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape  = DiarioRadius.md,
                    colors = ButtonDefaults.buttonColors(containerColor = DiarioColors.PrimaryBlue)
                ) {
                    Text(
                        text  = "Conferma partecipazione",
                        style = DiarioTypography.labelLarge.copy(
                            color      = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            } else {
                Button(
                    onClick  = {
                        viewModel.bookingSeats = seats
                        navController.navigate(Routes.payment(event.id, seats))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape  = DiarioRadius.md,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (seats < 1) DiarioColors.Gray200 else DiarioColors.PrimaryBlue
                    ),
                    enabled = seats >= 1
                ) {
                    Text(
                        text  = "Paga ora",
                        style = DiarioTypography.labelLarge.copy(
                            color      = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
