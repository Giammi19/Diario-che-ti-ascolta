package com.ium.diario.ui.screens

import com.ium.diario.models.*
import com.ium.diario.state.AppViewModel
import com.ium.diario.ui.theme.*
import com.ium.diario.navigate.Routes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.flowlayout.FlowRow

// ─────────────────────────────────────────────────────────────────────────────
// EventDetailView
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailView(
    eventId       : String,
    navController : NavHostController,
    viewModel     : AppViewModel
) {
    val event = remember(eventId) { EventData.find(id = eventId) } ?: return
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    var isLiked by remember(eventId, profile) {
        mutableStateOf(profile.hasFavorite(eventId))
    }

    LaunchedEffect(eventId) {
        viewModel.selectEvent(eventId)
    }

    Scaffold(
        containerColor = DiarioColors.Background
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            // ── Scroll content ────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                // ── Hero ──────────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0F4C81),
                                    Color(0xFF1A6FC4)
                                )
                            )
                        )
                ) {
                    // Emoji centrata
                    Text(
                        text     = event.emoji,
                        fontSize = 80.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 20.dp)
                    )
                }

                // ── Info Card ─────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .offset(y = (-24).dp)
                        .clip(DiarioRadius.lg)
                        .background(Color.White)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text  = event.category.label.uppercase(),
                            style = DiarioTypography.labelSmall.copy(
                                color        = DiarioColors.PrimaryBlue,
                                fontWeight   = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text  = event.title,
                            style = DiarioTypography.titleLarge.copy(fontSize = 24.sp)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        // Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text  = "${event.rating}",
                                style = DiarioTypography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text  = " (${event.reviewCount})",
                                style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray400)
                            )
                        }
                        // Tag "Popolare" se Featured
                        if (event.isFeatured) {
                            Text(
                                text     = "POPOLARE",
                                style    = DiarioTypography.labelSmall.copy(
                                    color      = DiarioColors.Orange,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(DiarioColors.Orange.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    AppDivider()

                    // Luogo e Orario
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetailRow(icon = "location", title = event.venue, subtitle = event.address)
                        DetailRow(icon = "clock",    title = event.formattedDate, subtitle = event.formattedTime)
                    }

                    AppDivider()

                    // Descrizione
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionLabel(text = "Informazioni")
                        Text(
                            text  = event.description,
                            style = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray600, lineHeight = 22.sp)
                        )
                    }

                    // Atmosfera / Tags
                    if (event.atmosphere.isNotEmpty()) {
                        @Suppress("DEPRECATION")
                        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                            event.atmosphere.forEach { tag ->
                                AtmosphereTag(label = tag)
                            }
                        }
                    }
                }

                // Spazio per il pulsante fisso in basso
                Spacer(modifier = Modifier.height(100.dp))
            }

            // ── Barra di navigazione custom (Top) ───────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick  = { navController.popBackStack() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector       = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        tint              = DiarioColors.Gray900
                    )
                }

                IconButton(
                    onClick  = {
                        isLiked = !isLiked
                        viewModel.toggleFavorite(event.id)
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector       = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Salva",
                        tint              = if (isLiked) DiarioColors.Red else DiarioColors.Gray900
                    )
                }
            }

            // ── Bottom Action Bar ─────────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color    = Color.White,
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text  = "Prezzo",
                            style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray400)
                        )
                        Text(
                            text  = event.priceLabel,
                            style = DiarioTypography.titleLarge.copy(color = DiarioColors.PrimaryBlue)
                        )
                    }

                    Button(
                        onClick  = { navController.navigate(Routes.booking(event.id)) },
                        modifier = Modifier
                            .height(50.dp)
                            .widthIn(min = 160.dp),
                        shape    = DiarioRadius.md,
                        colors   = ButtonDefaults.buttonColors(containerColor = DiarioColors.PrimaryBlue)
                    ) {
                        Text(
                            text  = if (event.isFree) "Prenota ora" else "Acquista biglietti",
                            style = DiarioTypography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: String, title: String, subtitle: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment     = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DiarioColors.Gray50),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector       = if (icon == "location") Icons.Default.LocationOn else Icons.Default.FavoriteBorder, // Fallback placeholder
                contentDescription = null,
                tint              = DiarioColors.PrimaryBlue,
                modifier          = Modifier.size(20.dp)
            )
        }
        Column {
            Text(
                text  = title,
                style = DiarioTypography.labelLarge.copy(color = DiarioColors.Gray900, fontWeight = FontWeight.SemiBold)
            )
            Text(
                text  = subtitle,
                style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray400)
            )
        }
    }
}
