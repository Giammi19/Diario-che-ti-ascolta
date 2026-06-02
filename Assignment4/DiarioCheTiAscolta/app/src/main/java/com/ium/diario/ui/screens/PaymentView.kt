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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────────────────────────────────────
// PaymentView
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentView(
    eventId       : String,
    navController : NavHostController,
    viewModel     : AppViewModel
) {
    val event = remember(eventId) { EventData.find(id = eventId) } ?: return
    val seats = viewModel.bookingSeats

    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(PaymentMethod.CARTA) }
    
    // Credit card fields
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    val total = event.price * seats
    val isFormValid = selectedMethod != PaymentMethod.CARTA || 
            (cardNumber.length == 16 && expiryDate.length == 4 && cvv.length == 3)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Pagamento", style = DiarioTypography.titleMedium.copy(fontWeight = FontWeight.Bold)) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
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

            // ── Riepilogo Ordine ─────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DiarioRadius.lg)
                    .background(Color.White)
                    .border(1.5.dp, DiarioColors.Gray200, DiarioRadius.lg)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text  = event.title,
                    style = DiarioTypography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text  = if (seats == 1) "1 biglietto" else "$seats biglietti",
                        style = DiarioTypography.bodyMedium.copy(color = DiarioColors.Gray500)
                    )
                    Text(
                        text  = "€ ${"%.2f".format(total)}",
                        style = DiarioTypography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                AppDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "Totale",
                        style = DiarioTypography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text  = "€ ${"%.2f".format(total)}",
                        style = DiarioTypography.displayLarge.copy(
                            color    = DiarioColors.PrimaryBlue,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // ── Metodi di Pagamento ──────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionLabel(text = "Metodo di pagamento")

                PaymentMethod.entries.forEach { method ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaymentMethodRow(
                            method = method,
                            isSelected = selectedMethod == method,
                            onSelect = { selectedMethod = method }
                        )

                        // Card details form if selected
                        if (method == PaymentMethod.CARTA && selectedMethod == method) {
                            CardDetailsForm(
                                cardNumber = cardNumber,
                                onCardNumberChange = { input -> 
                                    val digits = input.filter { it.isDigit() }
                                    if (digits.length <= 16) cardNumber = digits 
                                },
                                expiryDate = expiryDate,
                                onExpiryDateChange = { input -> 
                                    val digits = input.filter { it.isDigit() }
                                    if (digits.length <= 4) expiryDate = digits 
                                },
                                cvv = cvv,
                                onCvvChange = { input -> 
                                    val digits = input.filter { it.isDigit() }
                                    if (digits.length <= 3) cvv = digits 
                                }
                            )
                        }
                    }
                }
            }

            // ── Sicurezza ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector       = Icons.Default.Lock,
                    contentDescription = null,
                    tint              = DiarioColors.Gray400,
                    modifier          = Modifier.size(14.dp)
                )
                Text(
                    text  = "Pagamento sicuro crittografato SSL",
                    style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray400)
                )
            }

            // ── CTA ──────────────────────────────────────────────────────────
            Button(
                onClick  = {
                    viewModel.confirmBooking(
                        eventId = event.id,
                        seats = seats,
                        paymentMethod = selectedMethod?.label
                    )
                    val lastBooking = viewModel.bookings.value.lastOrNull()
                    if (lastBooking != null) {
                        navController.navigate(Routes.confirm(lastBooking.id)) {
                            popUpTo(Routes.HOME)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape    = DiarioRadius.md,
                colors   = ButtonDefaults.buttonColors(containerColor = DiarioColors.PrimaryBlue),
                enabled  = selectedMethod != null && isFormValid
            ) {
                Text(
                    text  = "Paga ora",
                    style = DiarioTypography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun CardDetailsForm(
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    expiryDate: String,
    onExpiryDateChange: (String) -> Unit,
    cvv: String,
    onCvvChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = cardNumber,
            onValueChange = onCardNumberChange,
            label = { Text("Numero Carta") },
            placeholder = { Text("0000 0000 0000 0000") },
            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = DiarioRadius.sm,
            visualTransformation = CreditCardVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DiarioColors.PrimaryBlue,
                unfocusedBorderColor = DiarioColors.Gray200
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = expiryDate,
                onValueChange = onExpiryDateChange,
                label = { Text("Scadenza") },
                placeholder = { Text("MM/AA") },
                modifier = Modifier.weight(1f),
                shape = DiarioRadius.sm,
                visualTransformation = ExpiryDateVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DiarioColors.PrimaryBlue,
                    unfocusedBorderColor = DiarioColors.Gray200
                )
            )

            OutlinedTextField(
                value = cvv,
                onValueChange = onCvvChange,
                label = { Text("CVV") },
                placeholder = { Text("123") },
                modifier = Modifier.weight(1f),
                shape = DiarioRadius.sm,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DiarioColors.PrimaryBlue,
                    unfocusedBorderColor = DiarioColors.Gray200
                )
            )
        }
    }
}

// ── Visual Transformations ───────────────────────────────────────────────────

class CreditCardVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0, 16) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != 15) out += " "
        }

        val creditCardOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset + 1
                if (offset <= 11) return offset + 2
                if (offset <= 16) return offset + 3
                return 19
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                if (offset <= 19) return offset - 3
                return 16
            }
        }

        return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
    }
}

class ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 4) text.text.substring(0, 4) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1) out += "/"
        }

        val expiryDateOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                return 5
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                return 4
            }
        }

        return TransformedText(AnnotatedString(out), expiryDateOffsetTranslator)
    }
}

@Composable
private fun PaymentMethodRow(
    method     : PaymentMethod,
    isSelected : Boolean,
    onSelect   : () -> Unit
) {
    val borderColor = if (isSelected) DiarioColors.PrimaryBlue else DiarioColors.Gray200
    val bgColor     = if (isSelected) DiarioColors.PrimaryBlue.copy(alpha = 0.05f) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DiarioRadius.md)
            .background(bgColor)
            .border(1.5.dp, borderColor, DiarioRadius.md)
            .clickable(onClick = onSelect)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icona/Emoji
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DiarioColors.Gray50),
            contentAlignment = Alignment.Center
        ) {
            Text(text = method.icon, fontSize = 20.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = method.label,
                style = DiarioTypography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            method.subtitle?.let {
                Text(
                    text  = it,
                    style = DiarioTypography.labelSmall.copy(color = DiarioColors.Gray500)
                )
            }
        }

        if (isSelected) {
            Icon(
                imageVector       = Icons.Default.CheckCircle,
                contentDescription = "Selezionato",
                tint              = DiarioColors.PrimaryBlue,
                modifier          = Modifier.size(20.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(1.5.dp, DiarioColors.Gray300, CircleShape)
            )
        }
    }
}
