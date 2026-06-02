// ============================================================
// Booking.kt
// Il Diario che ti Ascolta — Prenotazioni
// ============================================================

package com.ium.diario.models

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

// ============================================================
// MARK: — Enum stato prenotazione
// ============================================================

enum class BookingStatus(val label: String, val emoji: String) {
    CONFIRMED("Confermata",  "✅"),
    PENDING  ("In attesa",   "⏳"),
    CANCELLED("Annullata",   "❌"),
    COMPLETED("Completata",  "🎉"),
}

// ============================================================
// MARK: — Modello Booking
// ============================================================

data class Booking(
    val id          : String        = UUID.randomUUID().toString(),
    val eventId     : String,
    val eventTitle  : String,
    val eventCategory: String,
    val eventDate   : LocalDate,
    val eventTime   : LocalTime,
    val eventLocation: String,
    val eventImageUrl: String       = "",
    val seats       : Int           = 1,
    val totalPrice  : Double,
    val status      : BookingStatus = BookingStatus.CONFIRMED,
    val bookedAt    : LocalDate     = LocalDate.now(),
    val paymentMethod: String       = "",
    val notes       : String        = "",
    val isManual    : Boolean       = false,    // true → ManualEvent aggiunto dall'utente
    val confirmationCode: String    = generateCode(),
) {
    // ── Proprietà derivate ───────────────────────────────────

    val isFree      : Boolean get() = totalPrice <= 0.0
    val isUpcoming  : Boolean get() = !eventDate.isBefore(LocalDate.now())
    val isPast      : Boolean get() = eventDate.isBefore(LocalDate.now())
    val canCancel   : Boolean get() = status == BookingStatus.CONFIRMED && isUpcoming

    val date: LocalDate get() = eventDate
    val time: LocalTime get() = eventTime
    val code: String get() = confirmationCode
    val totalLabel: String get() = formattedPrice

    val paymentMethodLabel: String get() = when {
        isManual -> "Appunto"
        paymentMethod.isNotBlank() -> paymentMethod
        else -> "Gratuito"
    }

    val formattedDate: String get() =
        eventDate.format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", java.util.Locale.ITALIAN))

    val shortDate: String get() =
        eventDate.format(DateTimeFormatter.ofPattern("d MMM", java.util.Locale.ITALIAN))

    val formattedTime: String get() =
        eventTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    val formattedPrice: String get() = when {
        isFree -> "Gratuito"
        else   -> "€%.2f".format(totalPrice)
    }

    val pricePerSeat: Double get() = if (seats > 0) totalPrice / seats else 0.0

    companion object {
        fun generateCode(): String =
            ('A'..'Z').shuffled().take(3).joinToString("") +
            (100..999).random().toString()

        /** Crea una Booking direttamente da un Event */
        fun fromEvent(
            event  : Event,
            seats  : Int    = 1,
            payment: String = "",
            notes  : String = "",
        ): Booking = Booking(
            eventId       = event.id,
            eventTitle    = event.title,
            eventCategory = event.category.label,
            eventDate     = event.date,
            eventTime     = event.timeStart,
            eventLocation = event.location,
            eventImageUrl = event.imageUrl,
            seats         = seats,
            totalPrice    = event.price * seats,
            paymentMethod = payment,
            notes         = notes,
            isManual      = false,
        )
    }
}

// ============================================================
// MARK: — ManualEvent
// Evento personalizzato aggiunto dall'utente (non nel catalogo)
// ============================================================

data class ManualEvent(
    val id       : String    = UUID.randomUUID().toString(),
    val title    : String,
    val category : String    = "Altro",
    val date     : LocalDate,
    val time     : LocalTime,
    val location : String    = "",
    val note     : String    = "", // rinominato da notes per matchare la View
    val price    : Double    = 0.0,
    val emoji    : String    = "📌",
) {
    fun toBooking(): Booking = Booking(
        eventId        = id,
        eventTitle     = title,
        eventCategory  = category,
        eventDate      = date,
        eventTime      = time,
        eventLocation  = location,
        seats          = 1,
        totalPrice     = price,
        notes          = note,
        isManual       = true,
        status         = BookingStatus.CONFIRMED,
    )
}

// ============================================================
// MARK: — PaymentMethod
// ============================================================

enum class PaymentMethod(val label: String, val icon: String, val subtitle: String? = null) {
    CARTA       ("Carta di credito", "💳", "Visa, Mastercard, AMEX"),
    PAYPAL      ("PayPal",           "🅿️", "Paga con il tuo account"),
    APPLE_PAY   ("Google Pay",       "📱", "Veloce e sicuro"),
    BANK_TRANSFER("Bonifico",        "🏦", "Elaborazione in 2-3 giorni");

    companion object {
        val CREDIT_CARD = CARTA // alias per compatibilità se necessario
    }
}

// ============================================================
// MARK: — BookingRepository (persistenza SharedPreferences)
// ============================================================

class BookingRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    // ── Booking serializzabile (LocalDate/Time → String) ────

    private data class BookingDto(
        val id              : String,
        val eventId         : String,
        val eventTitle      : String,
        val eventCategory   : String,
        val eventDate       : String,   // ISO "yyyy-MM-dd"
        val eventTime       : String,   // "HH:mm"
        val eventLocation   : String,
        val eventImageUrl   : String,
        val seats           : Int,
        val totalPrice      : Double,
        val status          : String,
        val bookedAt        : String,
        val paymentMethod   : String,
        val notes           : String,
        val isManual        : Boolean,
        val confirmationCode: String,
    )

    private fun Booking.toDto() = BookingDto(
        id, eventId, eventTitle, eventCategory,
        eventDate.toString(), eventTime.format(DateTimeFormatter.ofPattern("HH:mm")),
        eventLocation, eventImageUrl, seats, totalPrice,
        status.name, bookedAt.toString(), paymentMethod, notes, isManual, confirmationCode,
    )

    private fun BookingDto.toModel() = Booking(
        id               = id,
        eventId          = eventId,
        eventTitle       = eventTitle,
        eventCategory    = eventCategory,
        eventDate        = LocalDate.parse(eventDate),
        eventTime        = LocalTime.parse(eventTime),
        eventLocation    = eventLocation,
        eventImageUrl    = eventImageUrl,
        seats            = seats,
        totalPrice       = totalPrice,
        status           = BookingStatus.valueOf(status),
        bookedAt         = LocalDate.parse(bookedAt),
        paymentMethod    = paymentMethod,
        notes            = notes,
        isManual         = isManual,
        confirmationCode = confirmationCode,
    )

    // ── CRUD ─────────────────────────────────────────────────

    fun loadAll(): List<Booking> {
        val json = prefs.getString(KEY_BOOKINGS, null) ?: return emptyList()
        val type = object : TypeToken<List<BookingDto>>() {}.type
        return try {
            val dtos: List<BookingDto> = gson.fromJson(json, type)
            dtos.map { it.toModel() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveAll(bookings: List<Booking>) {
        val dtos = bookings.map { it.toDto() }
        prefs.edit { putString(KEY_BOOKINGS, gson.toJson(dtos)) }
    }

    fun add(booking: Booking) {
        val current = loadAll().toMutableList()
        current.add(booking)
        saveAll(current)
    }

    fun update(booking: Booking) {
        val current = loadAll().toMutableList()
        val idx = current.indexOfFirst { it.id == booking.id }
        if (idx >= 0) current[idx] = booking
        saveAll(current)
    }

    fun cancel(bookingId: String) {
        val current = loadAll().toMutableList()
        val idx = current.indexOfFirst { it.id == bookingId }
        if (idx >= 0) current[idx] = current[idx].copy(status = BookingStatus.CANCELLED)
        saveAll(current)
    }

    fun delete(bookingId: String) {
        val current = loadAll().filter { it.id != bookingId }
        saveAll(current)
    }

    fun findById(id: String): Booking? = loadAll().firstOrNull { it.id == id }

    fun clear() { prefs.edit { remove(KEY_BOOKINGS) } }

    companion object {
        private const val PREFS_NAME   = "diario_bookings"
        private const val KEY_BOOKINGS = "bookings_json"
    }
}
