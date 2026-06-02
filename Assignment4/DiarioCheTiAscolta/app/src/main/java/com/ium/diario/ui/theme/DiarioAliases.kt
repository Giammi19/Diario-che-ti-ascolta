// ============================================================
// DiarioAliases.kt
// Alias per compatibilità tra i nomi usati nelle screen
// (DiarioColors, DiarioTypography, ecc.) e quelli definiti
// in Theme.kt (AppColors, AppTypography, ecc.)
// ============================================================

package com.ium.diario.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colori ───────────────────────────────────────────────────────────────────

object DiarioColors {
    val PrimaryBlue      = AppColors.Primary
    val PrimaryBlueLight = AppColors.PrimaryMuted
    val Background       = AppColors.Background
    val Gray50           = AppColors.SurfaceVariant
    val Gray100          = Color(0xFFF3F4F6)
    val Gray200          = AppColors.Border
    val Gray300          = Color(0xFFD1D5DB)
    val Gray400          = AppColors.TextTertiary
    val Gray500          = AppColors.TextSecondary
    val Gray600          = Color(0xFF4B5563)
    val Gray700          = Color(0xFF374151)
    val Gray900          = AppColors.TextPrimary
    val Red              = AppColors.Error
    val Green            = AppColors.Success
    val Orange           = AppColors.Warning
}

// ── Tipografia ───────────────────────────────────────────────────────────────

object DiarioTypography {
    val displayLarge  = AppTypography.displayLarge
    val titleLarge    = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize   = 20.sp,
        lineHeight = 28.sp
    )
    val titleMedium   = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 16.sp,
        lineHeight = 22.sp
    )
    val bodyLarge     = AppTypography.bodyLarge
    val bodyMedium    = AppTypography.bodyMedium
    val bodySmall     = AppTypography.bodySmall
    val labelLarge    = AppTypography.labelLarge
    val labelMedium   = AppTypography.labelMedium
    val labelSmall    = AppTypography.labelSmall
}

// ── Spaziature ───────────────────────────────────────────────────────────────

object DiarioSpacing {
    val xs  : Dp = AppSpacing.xs
    val sm  : Dp = AppSpacing.sm
    val md  : Dp = AppSpacing.md
    val lg  : Dp = AppSpacing.lg
    val xl  : Dp = AppSpacing.xl
    val xxl : Dp = AppSpacing.xxl
}

// ── Raggi ────────────────────────────────────────────────────────────────────

object DiarioRadius {
    val xs   = AppShape.xs
    val sm   = AppShape.sm
    val md   = AppShape.md
    val lg   = AppShape.lg
    val pill = AppShape.pill
}

// ── Componenti mancanti ──────────────────────────────────────────────────────

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text     = text.uppercase(),
        style    = DiarioTypography.labelSmall.copy(
            color        = DiarioColors.Gray500,
            fontWeight   = FontWeight.SemiBold,
            letterSpacing = 1.sp
        ),
        modifier = modifier
    )
}

@Composable
fun IconLabelSmall(icon: String, label: String, modifier: Modifier = Modifier) {
    val imageVector = when (icon) {
        "clock"    -> Icons.Default.AccessTime
        "location" -> Icons.Default.LocationOn
        "people"   -> Icons.Default.People
        else       -> Icons.Default.Search
    }
    Row(
        modifier            = modifier,
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector       = imageVector,
            contentDescription = null,
            tint              = DiarioColors.Gray400,
            modifier          = Modifier.size(12.dp)
        )
        Text(
            text  = label,
            style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray400)
        )
    }
}

@Composable
fun FreeTag() {
    Text(
        text     = "GRATUITO",
        style    = DiarioTypography.labelSmall.copy(
            color      = DiarioColors.Green,
            fontWeight = FontWeight.Bold,
            fontSize   = 10.sp
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(DiarioColors.Green.copy(alpha = 0.1f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
fun AtmosphereTag(label: String) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier            = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(DiarioColors.Gray100)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector       = Icons.Default.People,
            contentDescription = null,
            tint              = DiarioColors.Gray500,
            modifier          = Modifier.size(10.dp)
        )
        Text(
            text  = label,
            style = DiarioTypography.labelSmall.copy(
                color    = DiarioColors.Gray500,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun PillChip(
    label    : String,
    isActive : Boolean,
    onClick  : () -> Unit
) {
    Surface(
        onClick = onClick,
        shape   = CircleShape,
        color   = if (isActive) DiarioColors.PrimaryBlue else Color.Transparent,
        border  = if (isActive) null else BorderStroke(1.dp, DiarioColors.Gray200),
        modifier = Modifier.height(32.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = label,
                style = DiarioTypography.labelMedium.copy(
                    color      = if (isActive) Color.White else DiarioColors.Gray600,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                )
            )
        }
    }
}
