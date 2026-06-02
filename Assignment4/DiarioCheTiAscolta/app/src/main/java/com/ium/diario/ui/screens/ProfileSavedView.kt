package com.ium.diario.ui.screens

import com.ium.diario.models.*
import com.ium.diario.state.AppViewModel
import com.ium.diario.ui.theme.*
import com.ium.diario.navigate.Routes

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────────────────────────────────────
// ProfileSavedView
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ProfileSavedView(
    navController : NavHostController,
    viewModel     : AppViewModel
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val diff = viewModel.lastProfileDiff ?: ProfileDiff(emptyList())

    // Animazione checkmark
    var checkmarkVisible by remember { mutableStateOf(false) }
    val checkmarkScale by animateFloatAsState(
        targetValue    = if (checkmarkVisible) 1f else 0.2f,
        animationSpec  = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium,
        ),
        label = "checkmark_scale"
    )
    val checkmarkAlpha by animateFloatAsState(
        targetValue   = if (checkmarkVisible) 1f else 0f,
        animationSpec = tween(300),
        label         = "checkmark_alpha"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        checkmarkVisible = true
    }

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

            // ── Checkmark animato ──────────────────────────────────────────────
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(top = DiarioSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DiarioSpacing.md)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Alone esterno
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(DiarioColors.PrimaryBlue.copy(alpha = 0.12f))
                    )
                    // Cerchio blu
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(DiarioColors.PrimaryBlue)
                            .scale(checkmarkScale),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.Check,
                            contentDescription = null,
                            tint               = Color.White.copy(alpha = checkmarkAlpha),
                            modifier           = Modifier.size(32.dp)
                        )
                    }
                }

                Text(
                    text      = "Profilo aggiornato!",
                    style     = DiarioTypography.titleLarge.copy(
                        color      = DiarioColors.Gray900,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text      = "Le tue preferenze sono state salvate",
                    style     = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray500),
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.padding(horizontal = DiarioSpacing.lg)
                )
            }

            Spacer(modifier = Modifier.height(DiarioSpacing.xl))

            // ── Riepilogo modifiche ────────────────────────────────────────────
            if (diff.hasChanges) {
                Column(
                    modifier            = Modifier.padding(horizontal = DiarioSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(DiarioSpacing.sm)
                ) {
                    SectionLabel(text = "Cosa è cambiato")

                    diff.changedFields.forEach { field ->
                        ChangeRow(label = field)
                    }
                }
            } else {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(vertical = DiarioSpacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(DiarioSpacing.sm)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.RemoveCircle,
                        contentDescription = null,
                        tint               = DiarioColors.Gray300,
                        modifier           = Modifier.size(36.dp)
                    )
                    Text(
                        text  = "Nessuna modifica effettuata",
                        style = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray400)
                    )
                }
            }

            Spacer(modifier = Modifier.height(DiarioSpacing.lg))

            // ── Profilo corrente ───────────────────────────────────────────────
            Column(
                modifier            = Modifier.padding(horizontal = DiarioSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(DiarioSpacing.md)
            ) {
                SectionLabel(text = "Il tuo profilo ora")

                // Campi principali
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(DiarioRadius.lg)
                        .background(Color.White)
                        .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
                ) {
                    ProfileSummaryRow(label = "Nome",       value = profile.name)
                    AppDivider()
                    ProfileSummaryRow(label = "Professione", value = profile.job ?: "—")
                    AppDivider()
                    ProfileSummaryRow(
                        label = "Giorni liberi",
                        value = profile.freeDays.joinToString(" · ") { it.short }
                    )
                    AppDivider()
                    ProfileSummaryRow(
                        label = "Fascia oraria",
                        value = profile.preferredTimeSlot.label
                    )
                }

                // Interessi attuali
                if (profile.interests.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(DiarioRadius.lg)
                            .background(Color.White)
                            .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
                            .padding(DiarioSpacing.md)
                    ) {
                        Text(
                            text  = "Interessi",
                            style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray500)
                        )
                        Spacer(modifier = Modifier.height(DiarioSpacing.sm))
                        @Suppress("DEPRECATION")
                        com.google.accompanist.flowlayout.FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                            profile.interests.forEach { interest ->
                                Box(
                                    modifier = Modifier
                                        .clip(DiarioRadius.pill)
                                        .background(DiarioColors.PrimaryBlue.copy(alpha = 0.1f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text  = interest,
                                        style = DiarioTypography.labelSmall.copy(
                                            color      = DiarioColors.PrimaryBlue,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(DiarioSpacing.lg))

            // ── CTA ───────────────────────────────────────────────────────────
            Column(
                modifier            = Modifier.padding(horizontal = DiarioSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(DiarioSpacing.sm)
            ) {
                // Vai alla Home
                Button(
                    onClick  = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape  = DiarioRadius.md,
                    colors = ButtonDefaults.buttonColors(containerColor = DiarioColors.PrimaryBlue)
                ) {
                    Text(
                        text  = "Vai alla Home",
                        style = DiarioTypography.labelLarge.copy(
                            color      = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Torna al Profilo
                OutlinedButton(
                    onClick  = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape  = DiarioRadius.md,
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp, DiarioColors.PrimaryBlue
                    )
                ) {
                    Text(
                        text  = "Torna al Profilo",
                        style = DiarioTypography.labelLarge.copy(
                            color      = DiarioColors.PrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ChangeRow — riga singola modifica
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ChangeRow(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DiarioRadius.md)
            .background(Color.White)
            .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.md)
            .padding(horizontal = DiarioSpacing.md, vertical = DiarioSpacing.sm),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DiarioSpacing.md)
    ) {
        Icon(
            imageVector        = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint               = DiarioColors.PrimaryBlue,
            modifier           = Modifier.size(16.dp)
        )
        Text(
            text  = label,
            style = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray900)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ProfileSummaryRow — riga riepilogo profilo
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DiarioSpacing.md, vertical = DiarioSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text  = label,
            style = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray500)
        )
        Text(
            text      = value,
            style     = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray900),
            textAlign = TextAlign.End,
            modifier  = Modifier.weight(1f, fill = false)
        )
    }
}
