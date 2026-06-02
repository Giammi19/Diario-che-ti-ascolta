// ============================================================
// Theme.kt
// Il Diario che ti Ascolta — Design System
// ============================================================

package com.ium.diario.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ============================================================
// MARK: — Colori
// ============================================================

object AppColors {

    // Primario — Blu principale #3B82F6
    val Primary         = Color(0xFF3B82F6)
    val PrimaryLight    = Color(0xFF60A5FA)  // blue-400
    val PrimaryDark     = Color(0xFF2563EB)  // blue-600
    val PrimaryMuted    = Color(0xFFEFF6FF)  // blue-50

    // Sfondo
    val Background      = Color(0xFFF3F4F6)  // gray-100 — sfondo principale
    val Surface         = Color(0xFFFFFFFF)  // bianco — card/sheet
    val SurfaceVariant  = Color(0xFFF9FAFB)  // gray-50

    // Testo
    val TextPrimary     = Color(0xFF111827)  // gray-900
    val TextSecondary   = Color(0xFF6B7280)  // gray-500
    val TextTertiary    = Color(0xFF9CA3AF)  // gray-400
    val TextOnPrimary   = Color(0xFFFFFFFF)

    // Stato
    val Success         = Color(0xFF10B981)  // emerald-500
    val SuccessLight    = Color(0xFFD1FAE5)  // emerald-100
    val Warning         = Color(0xFFF59E0B)  // amber-500
    val WarningLight    = Color(0xFFFEF3C7)  // amber-100
    val Error           = Color(0xFFEF4444)  // red-500
    val ErrorLight      = Color(0xFFFEE2E2)  // red-100

    // Categorie eventi (usati anche nei badge)
    val CategoryMusica  = Color(0xFF8B5CF6)  // violet-500
    val CategoryTeatro  = Color(0xFFEC4899)  // pink-500
    val CategoryArte    = Color(0xFFF97316)  // orange-500
    val CategoryCinema  = Color(0xFF06B6D4)  // cyan-500
    val CategoryLettura = Color(0xFF84CC16)  // lime-500
    val CategoryAltro   = Color(0xFF6B7280)  // gray-500

    // Bordi
    val Border          = Color(0xFFE5E7EB)  // gray-200
    val BorderFocus     = Primary

    // Overlay / Scrim
    val Scrim           = Color(0x80000000)  // nero 50%
    val CardShadow      = Color(0x1A000000)  // nero 10%

    // Gradiente primario (usato in header, splash, bottoni hero)
    val GradientPrimary = Brush.linearGradient(
        colors = listOf(PrimaryDark, Primary, PrimaryLight)
    )

    // Gradiente card evento
    val GradientCard = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color(0xCC111827))
    )
}


// ============================================================
// MARK: — Tipografia
// ============================================================
//
// Nota: aggiungi i font .ttf in res/font/ e registrali qui.
// Se non hai font custom, rimuovi FontFamily e usa le costanti
// direttamente con FontWeight.
//
// Font consigliati (Google Fonts, liberi):
//   Display → "Playfair Display" (serif elegante, come SF Serif)
//   Body    → "Plus Jakarta Sans" (clean, moderno)
// ============================================================

// Decommentare dopo aver aggiunto i file in res/font/
/*
val playfairDisplay = FontFamily(
    Font(R.font.playfair_display_regular, FontWeight.Normal),
    Font(R.font.playfair_display_bold,    FontWeight.Bold),
)
val plusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular,    FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium,     FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold,   FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold,       FontWeight.Bold),
)
*/

// Fallback con font di sistema (Roboto/sans-serif) fino all'aggiunta dei custom
val DisplayFont = FontFamily.Serif   // → sostituire con playfairDisplay
val BodyFont    = FontFamily.Default // → sostituire con plusJakartaSans

object AppTypography {

    // Display — titoli grandi (splash, hero)
    val displayLarge  = TextStyle(fontFamily = DisplayFont, fontWeight = FontWeight.Bold,   fontSize = 34.sp, lineHeight = 40.sp, letterSpacing = (-0.5).sp)
    val displayMedium = TextStyle(fontFamily = DisplayFont, fontWeight = FontWeight.Bold,   fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.3).sp)
    val displaySmall  = TextStyle(fontFamily = DisplayFont, fontWeight = FontWeight.Bold,   fontSize = 24.sp, lineHeight = 30.sp)

    // Headline — sezioni, card titolo
    val headlineLarge  = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Bold,     fontSize = 22.sp, lineHeight = 28.sp)
    val headlineMedium = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Bold,     fontSize = 18.sp, lineHeight = 24.sp)
    val headlineSmall  = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp)

    // Body — testo principale
    val bodyLarge  = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp)
    val bodyMedium = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp)
    val bodySmall  = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp)

    // Label — bottoni, badge, chip
    val labelLarge  = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp)
    val labelMedium = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = 0.1.sp)
    val labelSmall  = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Medium,   fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp)

    // Caption — metadata, timestamp
    val caption = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = 14.sp, color = AppColors.TextTertiary)
}


// ============================================================
// MARK: — Spacing
// ============================================================

object AppSpacing {
    val xxs  : Dp = 4.dp
    val xs   : Dp = 8.dp
    val sm   : Dp = 12.dp
    val md   : Dp = 16.dp
    val lg   : Dp = 20.dp
    val xl   : Dp = 24.dp
    val xxl  : Dp = 32.dp
    val xxxl : Dp = 48.dp
    val huge : Dp = 64.dp
}


// ============================================================
// MARK: — Raggi e forme
// ============================================================

object AppShape {
    val xs     = RoundedCornerShape(4.dp)
    val sm     = RoundedCornerShape(8.dp)
    val md     = RoundedCornerShape(12.dp)
    val lg     = RoundedCornerShape(16.dp)
    val xl     = RoundedCornerShape(20.dp)
    val xxl    = RoundedCornerShape(24.dp)
    val pill   = RoundedCornerShape(50)       // bordi completamente arrotondati
    val circle = CircleShape
}


// ============================================================
// MARK: — Elevation / Shadow
// ============================================================

object AppElevation {
    val none   : Dp = 0.dp
    val xs     : Dp = 1.dp
    val sm     : Dp = 2.dp
    val md     : Dp = 4.dp
    val lg     : Dp = 8.dp
    val xl     : Dp = 12.dp
    val xxl    : Dp = 16.dp
}


// ============================================================
// MARK: — MaterialTheme wrapper
// ============================================================

private val LightColorScheme = lightColorScheme(
    primary            = AppColors.Primary,
    onPrimary          = AppColors.TextOnPrimary,
    primaryContainer   = AppColors.PrimaryMuted,
    onPrimaryContainer = AppColors.PrimaryDark,
    secondary          = AppColors.PrimaryLight,
    onSecondary        = AppColors.TextOnPrimary,
    background         = AppColors.Background,
    onBackground       = AppColors.TextPrimary,
    surface            = AppColors.Surface,
    onSurface          = AppColors.TextPrimary,
    surfaceVariant     = AppColors.SurfaceVariant,
    onSurfaceVariant   = AppColors.TextSecondary,
    outline            = AppColors.Border,
    error              = AppColors.Error,
    onError            = Color.White,
)

@Composable
fun DiarioCheTiAscoltaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography(),   // MUI default; override con AppTypography dove serve
        content     = content
    )
}


// ============================================================
// MARK: — Componenti riutilizzabili
// ============================================================

// ── PrimaryButton ────────────────────────────────────────────

@Composable
fun PrimaryButton(
    text      : String,
    onClick   : () -> Unit,
    modifier  : Modifier = Modifier,
    enabled   : Boolean  = true,
    isLoading : Boolean  = false,
    fullWidth : Boolean  = true,
) {
    Button(
        onClick  = onClick,
        enabled  = enabled && !isLoading,
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .height(52.dp),
        shape    = AppShape.pill,
        colors   = ButtonDefaults.buttonColors(
            containerColor         = AppColors.Primary,
            contentColor           = Color.White,
            disabledContainerColor = AppColors.Primary.copy(alpha = 0.4f),
            disabledContentColor   = Color.White.copy(alpha = 0.6f),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation  = AppElevation.sm,
            pressedElevation  = AppElevation.xs,
        ),
        contentPadding = PaddingValues(horizontal = AppSpacing.xl, vertical = AppSpacing.sm),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color     = Color.White,
                modifier  = Modifier.size(20.dp),
                strokeWidth = 2.dp,
            )
        } else {
            Text(text = text, style = AppTypography.labelLarge, color = Color.White)
        }
    }
}

// ── SecondaryButton ──────────────────────────────────────────

@Composable
fun SecondaryButton(
    text     : String,
    onClick  : () -> Unit,
    modifier : Modifier = Modifier,
    enabled  : Boolean  = true,
    fullWidth: Boolean  = true,
) {
    OutlinedButton(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .height(52.dp),
        shape    = AppShape.pill,
        colors   = ButtonDefaults.outlinedButtonColors(
            contentColor         = AppColors.Primary,
            disabledContentColor = AppColors.Primary.copy(alpha = 0.4f),
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = if (enabled) AppColors.Primary else AppColors.Primary.copy(alpha = 0.3f)
        ),
        contentPadding = PaddingValues(horizontal = AppSpacing.xl, vertical = AppSpacing.sm),
    ) {
        Text(text = text, style = AppTypography.labelLarge, color = AppColors.Primary)
    }
}

// ── TextButton (ghost) ───────────────────────────────────────

@Composable
fun GhostButton(
    text    : String,
    onClick : () -> Unit,
    modifier: Modifier = Modifier,
    color   : Color    = AppColors.Primary,
) {
    TextButton(
        onClick  = onClick,
        modifier = modifier,
        colors   = ButtonDefaults.textButtonColors(contentColor = color),
    ) {
        Text(text = text, style = AppTypography.labelMedium, color = color)
    }
}

// ── AppCard ──────────────────────────────────────────────────

@Composable
fun AppCard(
    modifier  : Modifier = Modifier,
    onClick   : (() -> Unit)? = null,
    elevation : Dp       = AppElevation.sm,
    shape     : RoundedCornerShape = AppShape.lg,
    content   : @Composable ColumnScope.() -> Unit,
) {
    val cardModifier = if (onClick != null)
        modifier
            .shadow(elevation, shape)
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = ripple(),
                onClick           = onClick,
            )
    else
        modifier
            .shadow(elevation, shape)
            .clip(shape)

    Column(
        modifier = cardModifier
            .background(AppColors.Surface)
            .padding(AppSpacing.md),
        content = content,
    )
}

// ── SectionHeader ────────────────────────────────────────────

@Composable
fun SectionHeader(
    title   : String,
    action  : String?    = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier   = Modifier,
) {
    Row(
        modifier            = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment   = Alignment.CenterVertically,
    ) {
        Text(
            text  = title,
            style = AppTypography.headlineMedium,
            color = AppColors.TextPrimary,
        )
        if (action != null && onAction != null) {
            GhostButton(text = action, onClick = onAction)
        }
    }
}

// ── CategoryBadge ─────────────────────────────────────────────

@Composable
fun CategoryBadge(
    label    : String,
    color    : Color    = AppColors.Primary,
    modifier : Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(AppShape.pill)
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xxs),
    ) {
        Text(
            text  = label,
            style = AppTypography.labelSmall,
            color = color,
        )
    }
}

// ── RatingBadge ──────────────────────────────────────────────

@Composable
fun RatingBadge(
    rating  : Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier          = modifier
            .clip(AppShape.pill)
            .background(AppColors.Warning.copy(alpha = 0.12f))
            .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xxs),
    ) {
        Text(text = "★", color = AppColors.Warning, fontSize = 12.sp)
        Text(
            text  = "%.1f".format(rating),
            style = AppTypography.labelSmall,
            color = AppColors.Warning,
        )
    }
}

// ── TagChip ───────────────────────────────────────────────────
// Chip selezionabile per filtri / interessi

@Composable
fun TagChip(
    label      : String,
    selected   : Boolean,
    onToggle   : () -> Unit,
    modifier   : Modifier = Modifier,
) {
    val bgColor      = if (selected) AppColors.Primary else AppColors.Surface
    val textColor    = if (selected) Color.White       else AppColors.TextSecondary
    val borderColor  = if (selected) AppColors.Primary else AppColors.Border

    Box(
        modifier = modifier
            .clip(AppShape.pill)
            .background(bgColor)
            .border(1.dp, borderColor, AppShape.pill)
            .clickable(onClick = onToggle)
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs),
    ) {
        Text(
            text  = label,
            style = AppTypography.labelMedium,
            color = textColor,
        )
    }
}

// ── InputField ────────────────────────────────────────────────

@Composable
fun AppInputField(
    value        : String,
    onValueChange: (String) -> Unit,
    placeholder  : String,
    modifier     : Modifier = Modifier,
    label        : String?  = null,
    isError      : Boolean  = false,
    errorMessage : String?  = null,
    leadingIcon  : @Composable (() -> Unit)? = null,
    trailingIcon : @Composable (() -> Unit)? = null,
    singleLine   : Boolean  = true,
    maxLines     : Int      = 1,
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text     = label,
                style    = AppTypography.labelMedium,
                color    = AppColors.TextSecondary,
                modifier = Modifier.padding(bottom = AppSpacing.xxs),
            )
        }
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = { Text(placeholder, color = AppColors.TextTertiary, style = AppTypography.bodyMedium) },
            singleLine    = singleLine,
            maxLines      = maxLines,
            isError       = isError,
            leadingIcon   = leadingIcon,
            trailingIcon  = trailingIcon,
            modifier      = Modifier.fillMaxWidth(),
            shape         = AppShape.md,
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AppColors.Primary,
                unfocusedBorderColor = AppColors.Border,
                errorBorderColor     = AppColors.Error,
                focusedContainerColor   = AppColors.Surface,
                unfocusedContainerColor = AppColors.Surface,
                cursorColor          = AppColors.Primary,
                focusedTextColor     = AppColors.TextPrimary,
                unfocusedTextColor   = AppColors.TextPrimary,
            ),
            textStyle = AppTypography.bodyMedium,
        )
        if (isError && errorMessage != null) {
            Text(
                text     = errorMessage,
                style    = AppTypography.bodySmall,
                color    = AppColors.Error,
                modifier = Modifier.padding(top = AppSpacing.xxs, start = AppSpacing.xs),
            )
        }
    }
}

// ── Divider ──────────────────────────────────────────────────

@Composable
fun AppDivider(
    modifier: Modifier = Modifier,
    color   : Color    = AppColors.Border,
) {
    HorizontalDivider(
        modifier  = modifier,
        thickness = 1.dp,
        color     = color,
    )
}

// ── EmptyState ────────────────────────────────────────────────

@Composable
fun EmptyState(
    icon    : String,
    title   : String,
    subtitle: String,
    modifier: Modifier = Modifier,
    action  : (@Composable () -> Unit)? = null,
) {
    Column(
        modifier              = modifier
            .fillMaxWidth()
            .padding(AppSpacing.xxl),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        Text(text = icon,  fontSize = 56.sp, textAlign = TextAlign.Center)
        Text(text = title, style = AppTypography.headlineMedium, color = AppColors.TextPrimary, textAlign = TextAlign.Center)
        Text(text = subtitle, style = AppTypography.bodyMedium, color = AppColors.TextSecondary, textAlign = TextAlign.Center)
        if (action != null) {
            Spacer(Modifier.height(AppSpacing.xs))
            action()
        }
    }
}

// ── LoadingOverlay ────────────────────────────────────────────

@Composable
fun LoadingOverlay(
    message: String = "Caricamento…",
) {
    Box(
        modifier          = Modifier
            .fillMaxSize()
            .background(AppColors.Scrim),
        contentAlignment  = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
            Text(text = message, style = AppTypography.bodyMedium, color = Color.White)
        }
    }
}

// ── ScreenContainer ───────────────────────────────────────────

@Composable
fun ScreenContainer(
    modifier       : Modifier  = Modifier,
    backgroundColor: Color     = AppColors.Background,
    padding        : PaddingValues = PaddingValues(0.dp),
    content        : @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(padding),
        content  = content,
    )
}

// ── Helper: colore per categoria ──────────────────────────────

fun categoryColor(category: String): Color = when (category.lowercase()) {
    "musica"        -> AppColors.CategoryMusica
    "teatro"        -> AppColors.CategoryTeatro
    "arte"          -> AppColors.CategoryArte
    "cinema"        -> AppColors.CategoryCinema
    "lettura"       -> AppColors.CategoryLettura
    else            -> AppColors.CategoryAltro
}

// ── Helper: colore di sfondo tag ─────────────────────────────

fun categoryBackgroundColor(category: String): Color =
    categoryColor(category).copy(alpha = 0.12f)
