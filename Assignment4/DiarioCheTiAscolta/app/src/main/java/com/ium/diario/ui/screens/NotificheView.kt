package com.ium.diario.ui.screens

import com.ium.diario.models.*
import com.ium.diario.state.AppViewModel
import com.ium.diario.ui.theme.*

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────────────────────────────────────
// NotificheView
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificheView(
    navController : NavHostController,
    viewModel     : AppViewModel
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    
    // Stato locale per le impostazioni (copia di quelle nel profilo)
    var settings by remember(profile) {
        mutableStateOf(profile.notifications)
    }
    
    var saved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Notifiche", style = DiarioTypography.titleMedium.copy(fontWeight = FontWeight.Bold)) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    TextButton(onClick = { 
                        viewModel.saveNotificationSettings(settings)
                        saved = true
                    }) {
                        Text(
                            text  = if (saved) "Salvato" else "Salva",
                            style = DiarioTypography.labelLarge.copy(
                                color = if (saved) DiarioColors.Green else DiarioColors.PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = DiarioColors.Background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ── Switch Generale ───────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionLabel(text = "Impostazioni Generali")
                
                NotificationToggleCard(
                    title    = "Consenti notifiche",
                    subtitle = "Ricevi avvisi su eventi e prenotazioni",
                    checked  = settings.enabled,
                    onToggle = { settings = settings.copy(enabled = it); saved = false }
                )
            }

            // ── Tipi di Notifica ─────────────────────────────────────────────
            if (settings.enabled) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionLabel(text = "Cosa vuoi ricevere")

                    NotificationOptionRow(
                        title   = "Nuovi eventi suggeriti",
                        checked = settings.newEvents,
                        onToggle = { settings = settings.copy(newEvents = it); saved = false }
                    )
                    AppDivider()
                    NotificationOptionRow(
                        title   = "Promemoria prenotazioni",
                        checked = settings.bookingReminders,
                        onToggle = { settings = settings.copy(bookingReminders = it); saved = false }
                    )
                }

                // ── Frequenza ─────────────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionLabel(text = "Frequenza riepilogo")

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(DiarioRadius.lg)
                            .background(Color.White)
                            .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
                    ) {
                        NotificationFrequency.entries.forEachIndexed { idx, freq ->
                            FrequencyRow(
                                label      = freq.label,
                                isSelected = settings.frequency == freq,
                                onClick    = { settings = settings.copy(frequency = freq); saved = false }
                            )
                            if (idx < NotificationFrequency.entries.size - 1) AppDivider()
                        }
                    }
                }

                // ── Modalità Focus ────────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionLabel(text = "Silenzia notifiche (Focus)")

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(DiarioRadius.lg)
                            .background(Color.White)
                            .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Modalità Focus", style = DiarioTypography.labelLarge)
                                Text(
                                    "Non ricevere notifiche in orari specifici",
                                    style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray400)
                                )
                            }
                            Switch(
                                checked = settings.focusMode,
                                onCheckedChange = { settings = settings.copy(focusMode = it); saved = false },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = DiarioColors.PrimaryBlue)
                            )
                        }

                        if (settings.focusMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TimeField(
                                    label = "Dalle",
                                    value = settings.focusStart,
                                    modifier = Modifier.weight(1f),
                                    onValueChange = { settings = settings.copy(focusStart = it); saved = false }
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = DiarioColors.Gray300,
                                    modifier = Modifier.size(16.dp)
                                )
                                TimeField(
                                    label = "Alle",
                                    value = settings.focusEnd,
                                    modifier = Modifier.weight(1f),
                                    onValueChange = { settings = settings.copy(focusEnd = it); saved = false }
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun NotificationToggleCard(
    title    : String,
    subtitle : String,
    checked  : Boolean,
    onToggle : (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DiarioRadius.lg)
            .background(Color.White)
            .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = DiarioTypography.labelLarge.copy(fontWeight = FontWeight.Bold))
            Text(text = subtitle, style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray400))
        }
        Switch(
            checked         = checked,
            onCheckedChange = onToggle,
            colors          = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = DiarioColors.PrimaryBlue
            )
        )
    }
}

@Composable
private fun NotificationOptionRow(
    title    : String,
    checked  : Boolean,
    onToggle : (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(text = title, style = DiarioTypography.bodyMedium)
        Checkbox(
            checked         = checked,
            onCheckedChange = onToggle,
            colors          = CheckboxDefaults.colors(checkedColor = DiarioColors.PrimaryBlue)
        )
    }
}

@Composable
private fun FrequencyRow(
    label      : String,
    isSelected : Boolean,
    onClick    : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(text = label, style = DiarioTypography.bodyMedium)
        RadioButton(
            selected = isSelected,
            onClick  = onClick,
            colors   = RadioButtonDefaults.colors(selectedColor = DiarioColors.PrimaryBlue)
        )
    }
}

@Composable
private fun TimeField(
    label         : String,
    value         : String,
    modifier      : Modifier = Modifier,
    onValueChange : (String) -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray500))
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth(),
            shape         = DiarioRadius.sm,
            textStyle     = DiarioTypography.bodyMedium,
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = DiarioColors.PrimaryBlue,
                unfocusedBorderColor = DiarioColors.Gray200
            )
        )
    }
}
