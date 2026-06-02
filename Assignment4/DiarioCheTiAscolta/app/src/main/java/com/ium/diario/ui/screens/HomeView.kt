package com.ium.diario.ui.screens

import com.ium.diario.models.*
import com.ium.diario.state.AppViewModel
import com.ium.diario.ui.theme.*
import com.ium.diario.navigate.Routes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────────────────────────────────────
// HomeView
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeView(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    val profile   by viewModel.profile.collectAsStateWithLifecycle()

    var selectedCategory by remember { mutableStateOf(EventCategory.TUTTI) }

    val filteredEvents = remember(selectedCategory) {
        EventData.filtered(by = selectedCategory)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DiarioColors.Background)
            .verticalScroll(rememberScrollState())
    ) {

        // ── Header ────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val greeting = if (profile.name.isBlank()) "👋" else "${profile.name}!"
            Text(
                text = "Ciao, $greeting",
                style = DiarioTypography.displayLarge.copy(
                    color      = DiarioColors.Gray900,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 26.sp
                )
            )

            // Icona notifiche con badge rosso
            Box(
                modifier = Modifier
                    .clickable { navController.navigate(Routes.NOTIFICHE) }
            ) {
                Icon(
                    imageVector       = Icons.Outlined.Notifications,
                    contentDescription = "Notifiche",
                    tint              = DiarioColors.PrimaryBlue,
                    modifier          = Modifier.size(26.dp)
                )
                // Badge rosso
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(DiarioColors.Red, shape = CircleShape)
                        .align(Alignment.TopEnd)
                        .offset(x = 3.dp, y = (-3).dp)
                )
            }
        }

        // ── Search bar (link a SearchView) ────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp)
                .clip(DiarioRadius.sm)
                .background(DiarioColors.Gray50)
                .border(
                    width = 1.5.dp,
                    color = DiarioColors.Gray200,
                    shape = DiarioRadius.sm
                )
                .clickable { navController.navigate(Routes.SEARCH) }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector       = Icons.Default.Search,
                contentDescription = null,
                tint              = DiarioColors.Gray400,
                modifier          = Modifier.size(16.dp)
            )
            Text(
                text  = "Cerca eventi, luoghi...",
                style = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray400)
            )
        }

        // ── Filtri categoria ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EventCategory.entries.forEach { cat ->
                PillChip(
                    label    = cat.label,
                    isActive = selectedCategory == cat,
                    onClick  = { selectedCategory = cat }
                )
            }
        }

        // ── Suggeriti per te ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            SectionLabel(text = "Suggeriti per te")
            Text(
                text     = "Vedi tutti",
                style    = DiarioTypography.labelMedium.copy(color = DiarioColors.PrimaryBlue),
                modifier = Modifier.clickable { navController.navigate(Routes.SEARCH) }
            )
        }

        // ── Lista eventi ──────────────────────────────────────────────────────
        if (filteredEvents.isEmpty()) {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🔍", fontSize = 40.sp)
                Text(
                    text      = "Nessun evento trovato\nin questa categoria.",
                    style     = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray400),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                modifier            = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filteredEvents.forEach { event ->
                    EventCard(
                        event       = event,
                        viewModel   = viewModel,
                        onClick     = { navController.navigate(Routes.eventDetail(event.id)) }
                    )
                }
            }
        }

        // ── Banner contestuale ────────────────────────────────────────────────
        ContextualBanner(
            modifier    = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp),
            navController = navController
        )

        // Spazio bottom nav
        Spacer(modifier = Modifier.height(100.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EventCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EventCard(
    event     : AppEvent,
    viewModel : AppViewModel,
    onClick   : () -> Unit
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    var isLiked by remember(event.id, profile) {
        mutableStateOf(profile.hasFavorite(event.id))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DiarioRadius.lg)
            .background(Color.White)
            .border(
                width = 1.5.dp,
                color = if (event.isFeatured) DiarioColors.PrimaryBlue else DiarioColors.Gray200,
                shape = DiarioRadius.lg
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.Top
        ) {
            // Emoji immagine
            Box(
                modifier = Modifier
                    .size(width = 72.dp, height = 80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DiarioColors.Gray100),
                contentAlignment = Alignment.Center
            ) {
                Text(text = event.emoji, fontSize = 28.sp)
            }

            // Info
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
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
                    IconLabelSmall(
                        icon  = "clock",
                        label = "${event.time} · ${event.duration}"
                    )
                    IconLabelSmall(
                        icon  = "location",
                        label = event.walkLabel
                    )
                }
                // Badge
                if (event.isFree) {
                    FreeTag()
                } else {
                    AtmosphereTag(label = event.crowd)
                }
            }
        }

        // Pulsante cuore (top trailing)
        IconButton(
            onClick  = {
                isLiked = !isLiked
                viewModel.toggleFavorite(event.id)
            },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector       = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Salva",
                tint              = if (isLiked) DiarioColors.Red else DiarioColors.Gray400,
                modifier          = Modifier.size(18.dp)
            )
        }

        // Badge "Top match" (bottom trailing)
        if (event.isFeatured) {
            Text(
                text     = "Top match",
                style    = DiarioTypography.labelSmall.copy(
                    color      = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 10.sp
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp)
                    .clip(CircleShape)
                    .background(DiarioColors.PrimaryBlue)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ContextualBanner
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ContextualBanner(
    modifier      : Modifier = Modifier,
    navController : NavHostController
) {
    val targetEvent = EventData.find(id = "evt-001") ?: return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(DiarioRadius.sm)
            .background(Color(0xFFFFFBEB))
            .border(
                width = 2.dp,
                color = DiarioColors.Orange,
                shape = DiarioRadius.sm
            )
            .clickable { navController.navigate(Routes.eventDetail(targetEvent.id)) }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text  = "Hai 90 minuti liberi!",
                style = DiarioTypography.labelMedium.copy(
                    color      = DiarioColors.PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text  = "A 10 min da qui: ${targetEvent.title}",
                style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray600)
            )
        }
    }
}
