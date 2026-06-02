package com.ium.diario.ui.screens

import com.ium.diario.models.*
import com.ium.diario.state.AppViewModel
import com.ium.diario.ui.theme.*
import com.ium.diario.navigate.Routes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────────────────────────────────────
// SearchView
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SearchView(
    navController : NavHostController,
    @Suppress("UNUSED_PARAMETER") viewModel : AppViewModel
) {
    var query            by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(EventCategory.TUTTI) }

    val results = remember(query, selectedCategory) {
        EventData.filtered(by = selectedCategory, query = query)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DiarioColors.Background)
    ) {
        // ── Search Header ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 16.dp, bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value         = query,
                    onValueChange = { query = it },
                    placeholder   = { Text("Cerca eventi, luoghi...") },
                    leadingIcon   = { Icon(Icons.Default.Search, null, tint = DiarioColors.Gray400) },
                    trailingIcon  = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, null, tint = DiarioColors.Gray400)
                            }
                        }
                    },
                    modifier      = Modifier.weight(1f),
                    shape         = DiarioRadius.sm,
                    singleLine    = true,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = DiarioColors.PrimaryBlue,
                        unfocusedBorderColor = DiarioColors.Gray200,
                        focusedContainerColor = DiarioColors.Gray50,
                        unfocusedContainerColor = DiarioColors.Gray50
                    )
                )
                
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Annulla", color = DiarioColors.Gray500)
                }
            }

            // ── Filtri categoria ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 8.dp),
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
        }

        // ── Risultati ────────────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize()) {
            if (results.isEmpty()) {
                EmptySearchState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(results) { event ->
                        SearchResultRow(
                            event   = event,
                            onClick = { navController.navigate(Routes.eventDetail(event.id)) }
                        )
                    }
                }
            }
        }
    }
}

// ── Search Components ────────────────────────────────────────────────────────

@Composable
private fun SearchResultRow(
    event   : Event,
    onClick : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DiarioRadius.lg)
            .background(Color.White)
            .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DiarioColors.Gray50),
            contentAlignment = Alignment.Center
        ) {
            Text(text = event.emoji, fontSize = 24.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = event.title,
                style = DiarioTypography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text  = "${event.venue} · ${event.time} · ${event.walkLabel}",
                style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray400)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = DiarioColors.Gray300,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔍", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Nessun risultato trovato",
            style = DiarioTypography.titleMedium.copy(color = DiarioColors.Gray900)
        )
        Text(
            "Prova con parole chiave diverse o cambia categoria.",
            style = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray500),
            textAlign = TextAlign.Center
        )
    }
}

// Note: I used LazyColumn which needs an import.
// Let me add the necessary imports.
