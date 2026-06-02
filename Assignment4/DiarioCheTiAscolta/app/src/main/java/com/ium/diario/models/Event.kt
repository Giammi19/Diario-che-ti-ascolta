// ============================================================
// Event.kt
// Il Diario che ti Ascolta — Modelli Evento
// ============================================================

package com.ium.diario.models

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Duration
import java.util.Locale

// ============================================================
// MARK: — Categoria Evento
// ============================================================

enum class EventCategory(val label: String, val emoji: String) {
    TUTTI("Tutti", "🌟"),
    MUSICA("Musica", "🎵"),
    TEATRO("Teatro", "🎭"),
    ARTE("Arte", "🎨"),
    CINEMA("Cinema", "🎬"),
    LETTURA("Lettura", "📚"),
    ALTRO("Altro", "✨");

    companion object {
        fun fromString(s: String): EventCategory {
            return values().firstOrNull { it.label.equals(s, ignoreCase = true) } ?: ALTRO
        }
    }
}

// ============================================================
// MARK: — Range Prezzo (Filtri)
// ============================================================

enum class PriceRange(val label: String, val maxPrice: Double) {
    FREE  ("Gratis",     0.0),
    LOW   ("€ (<15)",   15.0),
    MEDIUM("€€ (15-30)", 30.0),
    HIGH  ("€€€ (>30)",  9999.0);

    companion object {
        fun forPrice(p: Double): PriceRange = when {
            p <= 0.0  -> FREE
            p <= 15.0 -> LOW
            p <= 30.0 -> MEDIUM
            else      -> HIGH
        }
    }
}

// ============================================================
// MARK: — Modello Evento
// ============================================================

data class Event(
    val id          : String,
    val title       : String,
    val category    : EventCategory,
    val description : String,
    val shortDesc   : String,
    val location    : String,
    val city        : String,
    val date        : LocalDate,
    val timeStart   : LocalTime,
    val timeEnd     : LocalTime?    = null,
    val price       : Double        = 0.0,
    val isFree      : Boolean       = false,
    val rating      : Float         = 0.0f,
    val reviewCount : Int           = 0,
    val imageUrl    : String        = "",
    val tags        : List<String>  = emptyList(),
    val isFeatured  : Boolean       = false,
    val availableSeats: Int         = 100,
    val organizer   : String        = "",
    val website     : String        = "",
) {
    // ── Proprietà derivate ───────────────────────────────────

    val priceRange: PriceRange get() = PriceRange.forPrice(price)

    val formattedPrice: String get() = when {
        isFree || price <= 0.0 -> "Gratuito"
        else -> "€%.2f".format(price)
    }

    val formattedDate: String get() =
        date.format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ITALIAN))

    val formattedTime: String get() = when {
        timeEnd != null -> "${timeStart.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${timeEnd.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        else -> timeStart.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    val shortDate: String get() =
        date.format(DateTimeFormatter.ofPattern("d MMM", Locale.ITALIAN))

    val isUpcoming: Boolean get() = !date.isBefore(LocalDate.now())

    val isToday: Boolean get() = date.isEqual(LocalDate.now())

    val isSoldOut: Boolean get() = availableSeats <= 0

    // ── Extension for compatibility with UI ──────────────────
    val venue: String get() = location
    val address: String get() = "$location, $city"
    val emoji: String get() = category.emoji
    val pricePerPerson: Double get() = price
    val priceLabel: String get() = formattedPrice
    val maxSeats: Int get() = availableSeats
    val walkLabel: String get() = "10 min a piedi"
    val crowd: String get() = "Moderata"
    val atmosphere: List<String> get() = tags

    val time: String get() = timeStart.format(DateTimeFormatter.ofPattern("HH:mm"))
    val duration: String get() = if (timeEnd != null) {
        val diff = Duration.between(timeStart, timeEnd)
        val hours = diff.toHours()
        val mins = diff.toMinutes() % 60
        if (mins > 0) "${hours}h ${mins}m" else "${hours}h"
    } else "2h"
}

/** Alias for compatibility with some views */
typealias AppEvent = Event

/** Alias for compatibility with some views */
object EventData {
    fun find(id: String) = SampleEvents.byId(id)
    fun filtered(by: EventCategory, query: String = ""): List<Event> {
        val base = if (by == EventCategory.TUTTI) SampleEvents.all else SampleEvents.byCategory(by)
        if (query.isBlank()) return base
        return base.filter { 
            it.title.contains(query, ignoreCase = true) || 
            it.location.contains(query, ignoreCase = true) 
        }
    }
}

// ============================================================
// MARK: — Filtri e Ordinamento
// ============================================================

data class EventFilter(
    val categories: Set<EventCategory> = emptySet(),
    val priceRanges: Set<PriceRange>   = emptySet(),
    val onlyFree: Boolean              = false,
    val onlyFeatured: Boolean          = false,
    val onlyUpcoming: Boolean          = true,
    val searchQuery: String            = "",
    val sortOrder: EventSortOrder      = EventSortOrder.DATE_ASC,
)

enum class EventSortOrder(val label: String) {
    DATE_ASC   ("Più recenti"),
    DATE_DESC  ("Meno recenti"),
    PRICE_ASC  ("Prezzo crescente"),
    PRICE_DESC ("Prezzo decrescente"),
    RATING_DESC("Valutazione migliore"),
    TITLE_ASC  ("Alfabetico (A-Z)");
}

fun List<Event>.applyFilter(filter: EventFilter): List<Event> {
    var result = this

    if (filter.searchQuery.isNotBlank()) {
        result = result.filter {
            it.title.contains(filter.searchQuery, ignoreCase = true) ||
            it.location.contains(filter.searchQuery, ignoreCase = true) ||
            it.tags.any { tag -> tag.contains(filter.searchQuery, ignoreCase = true) }
        }
    }

    if (filter.categories.isNotEmpty()) {
        result = result.filter { it.category in filter.categories }
    }

    if (filter.priceRanges.isNotEmpty()) {
        result = result.filter { it.priceRange in filter.priceRanges }
    }

    if (filter.onlyFree) {
        result = result.filter { it.isFree }
    }

    if (filter.onlyFeatured) {
        result = result.filter { it.isFeatured }
    }

    if (filter.onlyUpcoming) {
        result = result.filter { it.isUpcoming }
    }

    result = when (filter.sortOrder) {
        EventSortOrder.DATE_ASC    -> result.sortedBy { it.date }
        EventSortOrder.DATE_DESC   -> result.sortedByDescending { it.date }
        EventSortOrder.PRICE_ASC   -> result.sortedBy { it.price }
        EventSortOrder.PRICE_DESC  -> result.sortedByDescending { it.price }
        EventSortOrder.RATING_DESC -> result.sortedByDescending { it.rating }
        EventSortOrder.TITLE_ASC   -> result.sortedBy { it.title }
    }

    return result
}

// ============================================================
// MARK: — Dati hardcoded
// ============================================================

object SampleEvents {

    val all: List<Event> = listOf(

        Event(
            id           = "evt-001",
            title        = "Concerto Jazz al Chiaro di Luna",
            category     = EventCategory.MUSICA,
            description  = "Una serata magica con il quartetto di Marco Ferretti. Il jazz contemporaneo incontra le melodie classiche in un'atmosfera intima e raffinata sotto le stelle del parco.",
            shortDesc    = "Quartetto jazz in concerto all'aperto",
            location     = "Parco Sempione, Anfiteatro",
            city         = "Milano",
            date         = LocalDate.now().plusDays(3),
            timeStart    = LocalTime.of(21, 0),
            timeEnd      = LocalTime.of(23, 30),
            price        = 18.0,
            rating       = 4.7f,
            reviewCount  = 142,
            imageUrl     = "https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=800",
            tags         = listOf("jazz", "live", "outdoor", "estate"),
            isFeatured   = true,
            availableSeats = 200,
            organizer    = "Jazz Milano APS",
        ),

        Event(
            id           = "evt-002",
            title        = "Mostra: Frammenti di Luce",
            category     = EventCategory.ARTE,
            description  = "Un viaggio attraverso 50 opere di artisti emergenti italiani. Dipinti, sculture e installazioni multimediali che esplorano il tema della luce e dell'ombra nella vita contemporanea.",
            shortDesc    = "Esposizione di artisti italiani emergenti",
            location     = "Galleria d'Arte Moderna, Sala C",
            city         = "Milano",
            date         = LocalDate.now().plusDays(1),
            timeStart    = LocalTime.of(10, 0),
            timeEnd      = LocalTime.of(19, 0),
            price        = 0.0,
            rating       = 4.5f,
            reviewCount  = 89,
            imageUrl     = "https://images.unsplash.com/photo-1541367777708-7905fe3296c0?w=800",
            tags         = listOf("arte contemporanea", "gratuito", "installazioni"),
            isFeatured   = true,
            availableSeats = 500,
            organizer    = "GAM Milano",
        ),

        Event(
            id           = "evt-003",
            title        = "Il Nome della Rosa — Adattamento Teatrale",
            category     = EventCategory.TEATRO,
            description  = "L'iconico romanzo di Umberto Eco rivive sul palco in una produzione straordinaria. Con effetti speciali d'avanguardia e un cast di 20 attori professionisti.",
            shortDesc    = "Adattamento dal romanzo di Umberto Eco",
            location     = "Teatro alla Scala, Sala Piermarini",
            city         = "Milano",
            date         = LocalDate.now().plusDays(7),
            timeStart    = LocalTime.of(20, 30),
            timeEnd      = LocalTime.of(23, 0),
            price        = 45.0,
            rating       = 4.9f,
            reviewCount  = 310,
            imageUrl     = "https://images.unsplash.com/photo-1503095396549-807759245b35?w=800",
            tags         = listOf("teatro", "classici", "eco", "adattamento"),
            isFeatured   = true,
            availableSeats = 50,
            organizer    = "Teatro alla Scala",
        ),

        Event(
            id           = "evt-004",
            title        = "Proiezione: Il Viaggio di Chihiro",
            category     = EventCategory.CINEMA,
            description  = "Serata speciale dedicata al capolavoro di Hayao Miyazaki in versione restaurata 4K. Introdotta dal critico cinematografico Luca Bianchi con un'analisi del film.",
            shortDesc    = "Classico Ghibli in versione 4K restaurata",
            location     = "Cinema Anteo",
            city         = "Milano",
            date         = LocalDate.now().plusDays(2),
            timeStart    = LocalTime.of(20, 0),
            timeEnd      = LocalTime.of(22, 15),
            price        = 10.0,
            rating       = 4.8f,
            reviewCount  = 224,
            imageUrl     = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=800",
            tags         = listOf("ghibli", "animazione", "giappone", "4K"),
            isFeatured   = false,
            availableSeats = 30,
            organizer    = "Cinema Anteo",
        ),

        Event(
            id           = "evt-005",
            title        = "Club del Libro: Calvino 100",
            category     = EventCategory.LETTURA,
            description  = "Incontro mensile del club letterario dedicato a Italo Calvino nel centenario della nascita. Discussione de 'Le Città Invisibili' con l'accademico Prof. Rossi.",
            shortDesc    = "Discussione letteraria su Italo Calvino",
            location     = "Libreria Hoepli, Sala Lettura",
            city         = "Milano",
            date         = LocalDate.now().plusDays(5),
            timeStart    = LocalTime.of(18, 30),
            timeEnd      = LocalTime.of(20, 30),
            price        = 0.0,
            rating       = 4.6f,
            reviewCount  = 67,
            imageUrl     = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=800",
            tags         = listOf("letteratura", "calvino", "gratuito", "club"),
            isFeatured   = false,
            availableSeats = 40,
            organizer    = "Libreria Hoepli",
        ),

        Event(
            id           = "evt-006",
            title        = "Festival Street Food & Musica",
            category     = EventCategory.ALTRO,
            description  = "Due giorni di gastronomia, artigianato e musica dal vivo nel cuore di Navigli. Oltre 30 stand gastronomici, 10 band emergenti e laboratori per bambini.",
            shortDesc    = "Street food e musica live ai Navigli",
            location     = "Navigli, Alzaia Naviglio Grande",
            city         = "Milano",
            date         = LocalDate.now().plusDays(10),
            timeStart    = LocalTime.of(12, 0),
            timeEnd      = LocalTime.of(23, 0),
            price        = 0.0,
            rating       = 4.3f,
            reviewCount  = 512,
            imageUrl     = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800",
            tags         = listOf("street food", "musica", "outdoor", "famiglia"),
            isFeatured   = false,
            availableSeats = 9999,
            organizer    = "Comune di Milano",
        ),

        Event(
            id           = "evt-007",
            title        = "Piano Recital — Beethoven & Chopin",
            category     = EventCategory.MUSICA,
            description  = "Il giovane pianista Lorenzo De Luca esegue sonate di Beethoven e notturni di Chopin. Un'ora di musica classica in un ambiente raccolto e intimo.",
            shortDesc    = "Recital pianistico di musica classica",
            location     = "Conservatorio G. Verdi, Sala Puccini",
            city         = "Milano",
            date         = LocalDate.now().plusDays(4),
            timeStart    = LocalTime.of(19, 0),
            timeEnd      = LocalTime.of(20, 30),
            price        = 25.0,
            rating       = 4.7f,
            reviewCount  = 98,
            imageUrl     = "https://images.unsplash.com/photo-1520523839897-bd0b52f945a0?w=800",
            tags         = listOf("classica", "pianoforte", "beethoven", "chopin"),
            isFeatured   = false,
            availableSeats = 80,
            organizer    = "Conservatorio Verdi",
        ),

        Event(
            id           = "evt-008",
            title        = "Laboratorio di Acquerello",
            category     = EventCategory.ARTE,
            description  = "Workshop pratico per principianti e intermedi. La pittrice Giulia Moretti guida i partecipanti nelle tecniche base dell'acquerello. Materiali inclusi nel prezzo.",
            shortDesc    = "Workshop pratico di pittura ad acquerello",
            location     = "Atelier Brera, Via Fiori Chiari 12",
            city         = "Milano",
            date         = LocalDate.now().plusDays(6),
            timeStart    = LocalTime.of(10, 0),
            timeEnd      = LocalTime.of(13, 0),
            price        = 35.0,
            rating       = 4.5f,
            reviewCount  = 44,
            imageUrl     = "https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?w=800",
            tags         = listOf("workshop", "pittura", "principianti", "brera"),
            isFeatured   = false,
            availableSeats = 12,
            organizer    = "Atelier Brera",
        ),
    )

    // ── Accesso rapido ───────────────────────────────────────

    val featured : List<Event> get() = all.filter { it.isFeatured }
    val upcoming : List<Event> get() = all.filter { it.isUpcoming }.sortedBy { it.date }
    val free     : List<Event> get() = all.filter { it.isFree }

    fun byCategory(cat: EventCategory): List<Event> = all.filter { it.category == cat }

    fun byId(id: String): Event? = all.firstOrNull { it.id == id }

    /** Suggerimenti personalizzati: priorità a categorie preferite dall'utente */
    fun recommended(interests: List<String>): List<Event> {
        val preferred = interests.map { EventCategory.fromString(it) }.toSet()
        return all
            .filter { it.isUpcoming }
            .sortedWith(compareByDescending<Event> { it.category in preferred }
                .thenByDescending { it.rating })
    }
}
