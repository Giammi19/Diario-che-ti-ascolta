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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────────────────────────────────────
// ProfileView
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ProfileView(
    navController : NavHostController,
    viewModel     : AppViewModel
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    var editedProfile   by remember(profile) { mutableStateOf(profile.copy()) }
    var originalProfile by remember(profile) { mutableStateOf(profile.copy()) }
    var newInterest     by remember { mutableStateOf("") }
    var errors          by remember { mutableStateOf<List<ProfileValidationError>>(emptyList()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DiarioColors.Gray50)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── Header ────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DiarioSpacing.lg)
                    .padding(top = DiarioSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "Il mio Profilo",
                    style = DiarioTypography.titleLarge.copy(
                        color      = DiarioColors.Gray900,
                        fontWeight = FontWeight.Bold
                    )
                )
                IconButton(onClick = { navController.navigate(Routes.NOTIFICHE) }) {
                    Icon(
                        imageVector       = Icons.Filled.Notifications,
                        contentDescription = "Notifiche",
                        tint              = DiarioColors.PrimaryBlue,
                        modifier          = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(DiarioSpacing.md))

            // ── Avatar ────────────────────────────────────────────────────────
            Box(
                modifier            = Modifier.fillMaxWidth(),
                contentAlignment    = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    // Cerchio avatar con iniziale
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(DiarioColors.PrimaryBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = editedProfile.initials,
                            style = DiarioTypography.displayLarge.copy(
                                color      = DiarioColors.PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 36.sp
                            )
                        )
                    }
                    // Badge matita
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(DiarioColors.PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector       = Icons.Filled.Edit,
                            contentDescription = null,
                            tint              = Color.White,
                            modifier          = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DiarioSpacing.lg))

            // ── Info personali ────────────────────────────────────────────────
            ProfileInfoSection(
                editedProfile = editedProfile,
                errors        = errors,
                onChange      = { editedProfile = it },
                modifier      = Modifier.padding(horizontal = DiarioSpacing.lg)
            )

            Spacer(modifier = Modifier.height(DiarioSpacing.lg))

            // ── Interessi ─────────────────────────────────────────────────────
            ProfileInterestsSection(
                editedProfile = editedProfile,
                errors        = errors,
                newInterest   = newInterest,
                onNewInterestChange = { newInterest = it },
                onAddInterest = {
                    val trimmed = newInterest.trim()
                    if (trimmed.isNotEmpty() && !editedProfile.interests.contains(trimmed)) {
                        editedProfile = editedProfile.copy(
                            interests = editedProfile.interests + trimmed
                        )
                        newInterest = ""
                    }
                },
                onRemoveInterest = { interest ->
                    editedProfile = editedProfile.copy(
                        interests = editedProfile.interests.filter { it != interest }
                    )
                },
                modifier = Modifier.padding(horizontal = DiarioSpacing.lg)
            )

            Spacer(modifier = Modifier.height(DiarioSpacing.lg))

            // ── Giorni liberi ─────────────────────────────────────────────────
            ProfileDaysSection(
                editedProfile = editedProfile,
                errors        = errors,
                onChange      = { editedProfile = it },
                modifier      = Modifier.padding(horizontal = DiarioSpacing.lg)
            )

            Spacer(modifier = Modifier.height(DiarioSpacing.lg))

            // ── Fascia oraria ─────────────────────────────────────────────────
            ProfileTimeSlotSection(
                editedProfile = editedProfile,
                onChange      = { editedProfile = it },
                modifier      = Modifier.padding(horizontal = DiarioSpacing.lg)
            )

            Spacer(modifier = Modifier.height(DiarioSpacing.lg))

            // ── CTA Salva ─────────────────────────────────────────────────────
            Button(
                onClick  = {
                    errors = editedProfile.validate()
                    if (errors.isEmpty()) {
                        val diff = ProfileDiff.between(old = originalProfile, new = editedProfile)
                        viewModel.lastProfileDiff = diff
                        viewModel.updateProfile(editedProfile)
                        navController.navigate(Routes.PROFILE_SAVED)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DiarioSpacing.lg)
                    .height(52.dp),
                shape  = DiarioRadius.md,
                colors = ButtonDefaults.buttonColors(containerColor = DiarioColors.PrimaryBlue)
            ) {
                Text(
                    text  = "Salva modifiche",
                    style = DiarioTypography.labelLarge.copy(
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ProfileInfoSection
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileInfoSection(
    editedProfile : UserProfile,
    errors        : List<ProfileValidationError>,
    onChange      : (UserProfile) -> Unit,
    modifier      : Modifier = Modifier
) {
    Column(
        modifier            = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DiarioSpacing.md)
    ) {
        SectionLabel(text = "Informazioni personali")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(DiarioRadius.lg)
                .background(Color.White)
                .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
        ) {
            // Nome
            ProfileFieldRow(
                label       = "Nome *",
                placeholder = "Il tuo nome",
                value       = editedProfile.name,
                hasError    = errors.contains(ProfileValidationError.EMPTY_NAME),
                onValueChange = { onChange(editedProfile.copy(name = it)) }
            )
            AppDivider()
            // Professione
            ProfileFieldRow(
                label         = "Professione",
                placeholder   = "Es. Studente, Designer...",
                value         = editedProfile.job ?: "",
                hasError      = false,
                onValueChange = { onChange(editedProfile.copy(job = it.ifBlank { null })) }
            )
        }
    }
}

@Composable
private fun ProfileFieldRow(
    label         : String,
    placeholder   : String,
    value         : String,
    hasError      : Boolean,
    onValueChange : (String) -> Unit
) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = DiarioSpacing.md, vertical = DiarioSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text  = label,
            style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray500)
        )
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = { Text(placeholder) },
            isError       = hasError,
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth(),
            shape         = DiarioRadius.sm,
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = DiarioColors.PrimaryBlue,
                unfocusedBorderColor = DiarioColors.Gray200,
                errorBorderColor     = DiarioColors.Red
            )
        )
        if (hasError) {
            Text(
                text  = "Campo obbligatorio",
                style = DiarioTypography.labelSmall.copy(
                    color    = DiarioColors.Red,
                    fontSize = 10.sp
                )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ProfileInterestsSection
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileInterestsSection(
    editedProfile       : UserProfile,
    errors              : List<ProfileValidationError>,
    newInterest         : String,
    onNewInterestChange : (String) -> Unit,
    onAddInterest       : () -> Unit,
    onRemoveInterest    : (String) -> Unit,
    modifier            : Modifier = Modifier
) {
    Column(
        modifier            = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DiarioSpacing.md)
    ) {
        SectionLabel(text = "I tuoi interessi")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(DiarioRadius.lg)
                .background(Color.White)
                .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
                .padding(DiarioSpacing.md),
            verticalArrangement = Arrangement.spacedBy(DiarioSpacing.md)
        ) {
            if (errors.contains(ProfileValidationError.NO_INTERESTS)) {
                Text(
                    text  = "Aggiungi almeno un interesse",
                    style = DiarioTypography.labelSmall.copy(
                        color    = DiarioColors.Red,
                        fontSize = 10.sp
                    )
                )
            }

            // Chip interessi selezionati
            if (editedProfile.interests.isNotEmpty()) {
                @Suppress("DEPRECATION")
                com.google.accompanist.flowlayout.FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                    editedProfile.interests.forEach { interest ->
                        Row(
                            modifier = Modifier
                                .clip(DiarioRadius.pill)
                                .background(DiarioColors.PrimaryBlue.copy(alpha = 0.1f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text  = interest,
                                style = DiarioTypography.labelSmall.copy(
                                    color      = DiarioColors.PrimaryBlue,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Icon(
                                imageVector       = Icons.Filled.Close,
                                contentDescription = "Rimuovi",
                                tint              = DiarioColors.PrimaryBlue,
                                modifier          = Modifier
                                    .size(10.dp)
                                    .clickable { onRemoveInterest(interest) }
                            )
                        }
                    }
                }
            }

            // Campo aggiunta
            Row(
                horizontalArrangement = Arrangement.spacedBy(DiarioSpacing.sm),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value         = newInterest,
                    onValueChange = onNewInterestChange,
                    placeholder   = { Text("Aggiungi interesse...") },
                    singleLine    = true,
                    modifier      = Modifier.weight(1f),
                    shape         = DiarioRadius.sm,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = DiarioColors.PrimaryBlue,
                        unfocusedBorderColor = DiarioColors.Gray200
                    )
                )
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(DiarioRadius.sm)
                        .background(DiarioColors.PrimaryBlue)
                        .clickable(onClick = onAddInterest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector       = Icons.Filled.Add,
                        contentDescription = "Aggiungi",
                        tint              = Color.White,
                        modifier          = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ProfileDaysSection
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileDaysSection(
    editedProfile : UserProfile,
    errors        : List<ProfileValidationError>,
    onChange      : (UserProfile) -> Unit,
    modifier      : Modifier = Modifier
) {
    Column(
        modifier            = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DiarioSpacing.md)
    ) {
        SectionLabel(text = "Giorni liberi")

        if (errors.contains(ProfileValidationError.NO_DAYS)) {
            Text(
                text  = "Seleziona almeno un giorno",
                style = DiarioTypography.labelSmall.copy(
                    color    = DiarioColors.Red,
                    fontSize = 10.sp
                )
            )
        }

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DiarioSpacing.xs)
        ) {
            Weekday.entries.forEach { day ->
                val isSelected = editedProfile.freeDays.contains(day)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) DiarioColors.PrimaryBlue else DiarioColors.Gray100
                        )
                        .clickable {
                            val newDays = if (isSelected)
                                editedProfile.freeDays.filter { it != day }
                            else
                                editedProfile.freeDays + day
                            onChange(editedProfile.copy(freeDays = newDays))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = day.short,
                        style = DiarioTypography.labelSmall.copy(
                            color      = if (isSelected) Color.White else DiarioColors.Gray600,
                            fontWeight = FontWeight.Medium,
                            fontSize   = 11.sp
                        )
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ProfileTimeSlotSection
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileTimeSlotSection(
    editedProfile : UserProfile,
    onChange      : (UserProfile) -> Unit,
    modifier      : Modifier = Modifier
) {
    Column(
        modifier            = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DiarioSpacing.md)
    ) {
        SectionLabel(text = "Fascia oraria preferita")

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DiarioSpacing.sm)
        ) {
            TimeSlot.entries.forEach { slot ->
                val isSelected = editedProfile.preferredTimeSlot == slot
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(DiarioRadius.md)
                        .background(
                            if (isSelected) DiarioColors.PrimaryBlue else DiarioColors.Gray100
                        )
                        .clickable { onChange(editedProfile.copy(preferredTimeSlot = slot)) }
                        .padding(vertical = DiarioSpacing.sm),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = slot.emoji, fontSize = 20.sp)
                    Text(
                        text  = slot.label,
                        style = DiarioTypography.labelSmall.copy(
                            color      = if (isSelected) Color.White else DiarioColors.Gray600,
                            fontSize   = 10.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
