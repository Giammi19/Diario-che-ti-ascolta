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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// CalendarView
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarView(
    navController : NavHostController,
    viewModel     : AppViewModel
) {
    val bookings     by viewModel.bookings.collectAsStateWithLifecycle()
    val manualEvents by viewModel.manualEvents.collectAsStateWithLifecycle()

    var selectedDate  by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth  by remember { mutableStateOf(YearMonth.now()) }
    var showAddSheet  by remember { mutableStateOf(false) }

    // Filtra per giorno selezionato
    val dayBookings = remember(bookings, selectedDate) {
        bookings.filter { !it.isManual && it.date == selectedDate }
    }
    val dayManualEvents = remember(manualEvents, selectedDate) {
        manualEvents.filter { it.date == selectedDate }
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
                    text  = "Il mio Diario",
                    style = DiarioTypography.titleLarge.copy(
                        color      = DiarioColors.Gray900,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(DiarioSpacing.lg))

            // ── Calendario ────────────────────────────────────────────────────
            CalendarCard(
                currentMonth  = currentMonth,
                selectedDate  = selectedDate,
                bookings      = bookings,
                onPrevMonth   = { currentMonth = currentMonth.minusMonths(1) },
                onNextMonth   = { currentMonth = currentMonth.plusMonths(1) },
                onSelectDate  = { selectedDate = it },
                modifier      = Modifier.padding(horizontal = DiarioSpacing.lg)
            )

            Spacer(modifier = Modifier.height(DiarioSpacing.lg))

            // ── Prenotazioni del giorno ───────────────────────────────────────
            BookingsSection(
                selectedDate = selectedDate,
                dayBookings  = dayBookings,
                modifier     = Modifier.padding(horizontal = DiarioSpacing.lg)
            )

            Spacer(modifier = Modifier.height(DiarioSpacing.lg))

            // ── Appunti personali ─────────────────────────────────────────────
            ManualEventsSection(
                dayEvents = dayManualEvents,
                onDelete  = { viewModel.removeManualEvent(it) },
                modifier  = Modifier.padding(horizontal = DiarioSpacing.lg)
            )

            Spacer(modifier = Modifier.height(80.dp))
        }

        // ── FAB ───────────────────────────────────────────────────────────────
        FloatingActionButton(
            onClick          = { showAddSheet = true },
            containerColor   = DiarioColors.PrimaryBlue,
            contentColor     = Color.White,
            shape            = CircleShape,
            modifier         = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = DiarioSpacing.lg, bottom = DiarioSpacing.xl)
        ) {
            Icon(
                imageVector       = Icons.Filled.Add,
                contentDescription = "Aggiungi appunto",
                modifier          = Modifier.size(22.dp)
            )
        }
    }

    // ── Bottom sheet: nuovo appunto ───────────────────────────────────────────
    if (showAddSheet) {
        AddEventSheet(
            onDismiss = { showAddSheet = false },
            onSave    = { title, time, note ->
                viewModel.addManualEvent(
                    title = title,
                    date  = selectedDate,
                    time  = time,
                    note  = note
                )
                showAddSheet = false
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CalendarCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CalendarCard(
    currentMonth  : YearMonth,
    selectedDate  : LocalDate,
    bookings      : List<Booking>,
    onPrevMonth   : () -> Unit,
    onNextMonth   : () -> Unit,
    onSelectDate  : (LocalDate) -> Unit,
    modifier      : Modifier = Modifier
) {
    val weekdaySymbols = listOf("L", "M", "M", "G", "V", "S", "D")

    // Calcola i giorni del mese (con null per padding iniziale)
    val days: List<LocalDate?> = remember(currentMonth) {
        val firstDay   = currentMonth.atDay(1)
        // dayOfWeek: Monday=1 … Sunday=7
        val offset     = (firstDay.dayOfWeek.value - 1)
        val nullPad    = List<LocalDate?>(offset) { null }
        val daysInMonth = (1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }
        nullPad + daysInMonth
    }

    val monthLabel = currentMonth.format(
        DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ITALIAN)
    ).replaceFirstChar { it.uppercase() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(DiarioRadius.lg)
            .background(Color.White)
            .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
            .padding(DiarioSpacing.md),
        verticalArrangement = Arrangement.spacedBy(DiarioSpacing.md)
    ) {
        // Mese + navigazione
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevMonth) {
                Icon(
                    imageVector       = Icons.Filled.ChevronLeft,
                    contentDescription = "Mese precedente",
                    tint              = DiarioColors.PrimaryBlue
                )
            }
            Text(
                text  = monthLabel,
                style = DiarioTypography.labelLarge.copy(
                    color      = DiarioColors.Gray900,
                    fontWeight = FontWeight.Bold
                )
            )
            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector       = Icons.Filled.ChevronRight,
                    contentDescription = "Mese successivo",
                    tint              = DiarioColors.PrimaryBlue
                )
            }
        }

        // Griglia 7 colonne
        // Intestazione giorni settimana
        Row(modifier = Modifier.fillMaxWidth()) {
            weekdaySymbols.forEach { symbol ->
                Text(
                    text      = symbol,
                    style     = DiarioTypography.labelSmall.copy(
                        color    = DiarioColors.Gray400,
                        fontSize = 11.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.weight(1f)
                )
            }
        }

        // Celle giorni
        val rows = days.chunked(7)
        rows.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // Padding se la settimana ha meno di 7 elementi
                repeat(7) { idx ->
                    val date = week.getOrNull(idx)
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        if (date != null) {
                            val isSelected = date == selectedDate
                            val isToday    = date == LocalDate.now()
                            val hasEvent   = bookings.any { it.date == date }

                            DayCell(
                                date       = date,
                                isSelected = isSelected,
                                isToday    = isToday,
                                hasEvent   = hasEvent,
                                onClick    = { onSelectDate(date) }
                            )
                        } else {
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date       : LocalDate,
    isSelected : Boolean,
    isToday    : Boolean,
    hasEvent   : Boolean,
    onClick    : () -> Unit
) {
    val bgColor = when {
        isSelected -> DiarioColors.PrimaryBlue
        isToday    -> DiarioColors.PrimaryBlue.copy(alpha = 0.12f)
        else       -> Color.Transparent
    }
    val textColor = when {
        isSelected -> Color.White
        isToday    -> DiarioColors.PrimaryBlue
        else       -> DiarioColors.Gray700
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = "${date.dayOfMonth}",
            style = DiarioTypography.labelMedium.copy(
                color      = textColor,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
            )
        )
        if (hasEvent) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else DiarioColors.PrimaryBlue)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-3).dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BookingsSection
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BookingsSection(
    selectedDate : LocalDate,
    dayBookings  : List<Booking>,
    modifier     : Modifier = Modifier
) {
    val dayLabel = selectedDate.format(DateTimeFormatter.ofPattern("d MMM", Locale.ITALIAN))

    Column(
        modifier            = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DiarioSpacing.sm)
    ) {
        SectionLabel(text = "Prenotazioni · $dayLabel")

        if (dayBookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DiarioRadius.lg)
                    .background(Color.White)
                    .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
                    .padding(vertical = DiarioSpacing.lg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = "Nessuna prenotazione per questo giorno",
                    style = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray400)
                )
            }
        } else {
            dayBookings.forEach { booking ->
                BookingRow(booking = booking)
            }
        }
    }
}

@Composable
private fun BookingRow(booking: Booking) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DiarioRadius.lg)
            .background(Color.White)
            .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
            .padding(DiarioSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(DiarioSpacing.md),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Barra blu laterale
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(DiarioColors.PrimaryBlue)
        )

        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text  = booking.eventTitle,
                style = DiarioTypography.labelLarge.copy(
                    color      = DiarioColors.Gray900,
                    fontWeight = FontWeight.Bold
                )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(DiarioSpacing.xs)) {
                Text(
                    text  = booking.formattedTime,
                    style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray500)
                )
                if (!booking.isFree) {
                    Text("·", style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray400))
                    Text(
                        text  = booking.totalLabel,
                        style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray500)
                    )
                }
            }
        }

        Text(
            text     = "${booking.seats} posto/i",
            style    = DiarioTypography.labelSmall.copy(
                color      = DiarioColors.PrimaryBlue,
                fontSize   = 10.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .clip(DiarioRadius.xs)
                .background(DiarioColors.PrimaryBlue.copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ManualEventsSection
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ManualEventsSection(
    dayEvents : List<Booking>,
    onDelete  : (String) -> Unit,
    modifier  : Modifier = Modifier
) {
    Column(
        modifier            = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DiarioSpacing.sm)
    ) {
        SectionLabel(text = "Appunti personali")

        if (dayEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DiarioRadius.lg)
                    .background(Color.White)
                    .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
                    .padding(vertical = DiarioSpacing.lg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = "Nessun appunto per oggi",
                    style = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray400)
                )
            }
        } else {
            dayEvents.forEach { event ->
                ManualEventRow(event = event, onDelete = { onDelete(event.id) })
            }
        }
    }
}

@Composable
private fun ManualEventRow(
    event    : Booking,
    onDelete : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DiarioRadius.lg)
            .background(Color.White)
            .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
            .padding(DiarioSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(DiarioSpacing.md),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Barra amber laterale
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFF59E0B))
        )

        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text  = event.eventTitle,
                style = DiarioTypography.labelLarge.copy(
                    color      = DiarioColors.Gray900,
                    fontWeight = FontWeight.Bold
                )
            )
            if (event.notes.isNotBlank()) {
                Text(
                    text  = event.notes,
                    style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray500)
                )
            }
            Text(
                text  = event.formattedTime,
                style = DiarioTypography.labelSmall.copy(
                    color    = DiarioColors.Gray400,
                    fontSize = 10.sp
                )
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector       = Icons.Filled.Delete,
                contentDescription = "Elimina",
                tint              = DiarioColors.Gray400,
                modifier          = Modifier.size(16.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AddEventSheet — ModalBottomSheet per nuovo appunto
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEventSheet(
    onDismiss : () -> Unit,
    onSave    : (title: String, time: LocalTime, note: String?) -> Unit
) {
    var title      by remember { mutableStateOf("") }
    var note       by remember { mutableStateOf("") }
    var hour       by remember { mutableIntStateOf(LocalTime.now().hour) }
    var minute     by remember { mutableIntStateOf(LocalTime.now().minute) }
    var titleError by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        sheetState        = sheetState,
        containerColor    = Color.White,
        dragHandle        = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = DiarioSpacing.lg)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(DiarioSpacing.lg)
        ) {
            // Titolo sheet
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text  = "Annulla",
                        style = DiarioTypography.labelMedium.copy(color = DiarioColors.Gray500)
                    )
                }
                Text(
                    text  = "Nuovo appunto",
                    style = DiarioTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                TextButton(onClick = {
                    titleError = title.isBlank()
                    if (!titleError) {
                        onSave(title.trim(), LocalTime.of(hour, minute), note.ifBlank { null })
                    }
                }) {
                    Text(
                        text  = "Salva",
                        style = DiarioTypography.labelLarge.copy(
                            color      = DiarioColors.PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Campo titolo
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text  = "Titolo *",
                    style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray500)
                )
                OutlinedTextField(
                    value         = title,
                    onValueChange = { title = it; titleError = false },
                    placeholder   = { Text("Es. Meditazione, Chiamata con...") },
                    isError       = titleError,
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = DiarioRadius.sm,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = DiarioColors.PrimaryBlue,
                        unfocusedBorderColor = DiarioColors.Gray200,
                        errorBorderColor     = DiarioColors.Red
                    )
                )
                if (titleError) {
                    Text(
                        text  = "Il titolo è obbligatorio",
                        style = DiarioTypography.labelSmall.copy(
                            color    = DiarioColors.Red,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Orario (semplice picker HH:MM)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text  = "Orario",
                    style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray500)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Ore
                    OutlinedTextField(
                        value         = "%02d".format(hour),
                        onValueChange = { hour = it.toIntOrNull()?.coerceIn(0, 23) ?: hour },
                        label         = { Text("HH") },
                        singleLine    = true,
                        modifier      = Modifier.width(80.dp),
                        shape         = DiarioRadius.sm,
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = DiarioColors.PrimaryBlue,
                            unfocusedBorderColor = DiarioColors.Gray200
                        )
                    )
                    Text(":", style = DiarioTypography.titleLarge)
                    // Minuti
                    OutlinedTextField(
                        value         = "%02d".format(minute),
                        onValueChange = { minute = it.toIntOrNull()?.coerceIn(0, 59) ?: minute },
                        label         = { Text("MM") },
                        singleLine    = true,
                        modifier      = Modifier.width(80.dp),
                        shape         = DiarioRadius.sm,
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = DiarioColors.PrimaryBlue,
                            unfocusedBorderColor = DiarioColors.Gray200
                        )
                    )
                }
            }

            // Campo nota
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text  = "Nota (opzionale)",
                    style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray500)
                )
                OutlinedTextField(
                    value         = note,
                    onValueChange = { note = it },
                    placeholder   = { Text("Aggiungi una nota...") },
                    minLines      = 3,
                    maxLines      = 5,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = DiarioRadius.sm,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = DiarioColors.PrimaryBlue,
                        unfocusedBorderColor = DiarioColors.Gray200
                    )
                )
            }
        }
    }
}
