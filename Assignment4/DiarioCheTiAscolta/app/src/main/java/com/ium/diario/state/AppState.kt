// ============================================================
// AppState.kt
// Il Diario che ti Ascolta — Stato Globale
// ============================================================

package com.ium.diario.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ium.diario.models.*
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ============================================================
// MARK: — Tab enum
// ============================================================

enum class AppTab(val label: String, val icon: String, val route: String) {
    HOME    ("Home",        "home",             "home"),
    SEARCH  ("Cerca",       "search",           "search"),
    CALENDAR("Calendario",  "calendar_today",   "calendar"),
    PROFILE ("Profilo",     "person",           "profile"),
}

// ============================================================
// MARK: — Toast
// ============================================================

data class ToastMessage(
    val message : String,
    val type    : ToastType = ToastType.INFO,
)

enum class ToastType { INFO, SUCCESS, ERROR, WARNING }

// ============================================================
// MARK: — AppViewModel
// ============================================================

class AppViewModel(application: Application) : AndroidViewModel(application) {

    // ── Repository ───────────────────────────────────────────
    private val profileRepo = ProfileRepository(application)
    val bookingRepository = BookingRepository(application)
    private val bookingRepo get() = bookingRepository

    // ── Profilo utente ───────────────────────────────────────
    private val _profile = MutableStateFlow(profileRepo.load())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    // ── Prenotazioni ─────────────────────────────────────────
    private val _bookings = MutableStateFlow(bookingRepo.loadAll())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    val manualEvents: StateFlow<List<Booking>> =
        _bookings
            .map { list -> list.filter { it.isManual } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // ── Tab attiva ───────────────────────────────────────────
    private val _activeTab = MutableStateFlow(AppTab.HOME)
    val activeTab: StateFlow<AppTab> = _activeTab.asStateFlow()

    // ── Toast ────────────────────────────────────────────────
    private val _toast = MutableStateFlow<ToastMessage?>(null)
    val toast: StateFlow<ToastMessage?> = _toast.asStateFlow()

    // ── Loading globale ──────────────────────────────────────
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ── Filtro eventi attivo ─────────────────────────────────
    private val _eventFilter = MutableStateFlow(EventFilter())
    val eventFilter: StateFlow<EventFilter> = _eventFilter.asStateFlow()

    // ── Lista eventi filtrata (derived) ─────────────────────
    val filteredEvents: StateFlow<List<Event>> =
        _eventFilter
            .map { filter -> SampleEvents.all.applyFilter(filter) }
            .stateIn(viewModelScope, SharingStarted.Lazily, SampleEvents.upcoming)

    // ── Evento selezionato (per detail/booking flow) ─────────
    private val _selectedEvent = MutableStateFlow<Event?>(null)
    val selectedEvent: StateFlow<Event?> = _selectedEvent.asStateFlow()

    // ── Prenotazione in corso (checkout flow) ────────────────
    private val _pendingBooking = MutableStateFlow<Booking?>(null)
    val pendingBooking: StateFlow<Booking?> = _pendingBooking.asStateFlow()

    var bookingSeats: Int = 1
    var lastProfileDiff: ProfileDiff? = null

    // ============================================================
    // MARK: — Azioni profilo
    // ============================================================

    fun updateProfile(updated: UserProfile) {
        _profile.value = updated
        profileRepo.save(updated)
    }

    fun saveNotificationSettings(settings: NotificationSettings) {
        val updated = _profile.value.copy(notifications = settings)
        updateProfile(updated)
        showToast("Impostazioni salvate", ToastType.SUCCESS)
    }

    fun toggleFavorite(eventId: String) {
        val updated = _profile.value.toggleFavorite(eventId)
        updateProfile(updated)
        val isFav = updated.hasFavorite(eventId)
        showToast(if (isFav) "Aggiunto ai preferiti ❤️" else "Rimosso dai preferiti", ToastType.INFO)
    }

    fun saveEvent(eventId: String) {
        if (!_profile.value.hasFavorite(eventId)) {
            toggleFavorite(eventId)
        }
    }

    fun completeOnboarding() {
        val updated = _profile.value.copy(onboardingCompleted = true)
        updateProfile(updated)
    }

    // ============================================================
    // MARK: — Azioni prenotazioni
    // ============================================================

    fun addBooking(booking: Booking) {
        bookingRepo.add(booking)
        _bookings.value = bookingRepo.loadAll()
        showToast("Prenotazione confermata! ✅", ToastType.SUCCESS)
    }

    fun confirmBooking(eventId: String, seats: Int, paymentMethod: String? = null) {
        val event = SampleEvents.byId(eventId) ?: return
        val booking = Booking.fromEvent(
            event = event,
            seats = seats,
            payment = paymentMethod ?: "Gratuito"
        )
        addBooking(booking)
        setPendingBooking(booking)
    }

    fun cancelBooking(bookingId: String) {
        bookingRepo.cancel(bookingId)
        _bookings.value = bookingRepo.loadAll()
        showToast("Prenotazione annullata", ToastType.WARNING)
    }

    fun deleteBooking(bookingId: String) {
        bookingRepo.delete(bookingId)
        _bookings.value = bookingRepo.loadAll()
    }

    fun removeManualEvent(eventId: String) {
        deleteBooking(eventId)
        showToast("Appunto rimosso", ToastType.INFO)
    }

    fun addManualEvent(title: String, date: LocalDate, time: LocalTime, note: String?) {
        val manualEvent = ManualEvent(
            title = title,
            date = date,
            time = time,
            note = note ?: ""
        )
        addBooking(manualEvent.toBooking())
    }

    // Prenotazioni divise per stato
    val upcomingBookings: StateFlow<List<Booking>> =
        _bookings
            .map { list -> list.filter { it.isUpcoming && it.status != BookingStatus.CANCELLED } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pastBookings: StateFlow<List<Booking>> =
        _bookings
            .map { list -> list.filter { it.isPast || it.status == BookingStatus.CANCELLED } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // ============================================================
    // MARK: — Azioni navigazione / selezione
    // ============================================================

    fun setActiveTab(tab: AppTab) {
        _activeTab.value = tab
    }

    fun selectEvent(event: Event) {
        _selectedEvent.value = event
    }

    fun selectEvent(eventId: String) {
        _selectedEvent.value = SampleEvents.byId(eventId)
    }

    fun clearSelectedEvent() {
        _selectedEvent.value = null
    }

    fun setPendingBooking(booking: Booking) {
        _pendingBooking.value = booking
    }

    fun clearPendingBooking() {
        _pendingBooking.value = null
    }

    // ============================================================
    // MARK: — Filtro eventi
    // ============================================================

    fun updateFilter(filter: EventFilter) {
        _eventFilter.value = filter
    }

    fun resetFilter() {
        _eventFilter.value = EventFilter()
    }

    fun setSearchQuery(query: String) {
        _eventFilter.value = _eventFilter.value.copy(searchQuery = query)
    }

    fun toggleCategoryFilter(category: EventCategory) {
        val current = _eventFilter.value.categories.toMutableSet()
        if (category in current) current.remove(category) else current.add(category)
        _eventFilter.value = _eventFilter.value.copy(categories = current)
    }

    // ============================================================
    // MARK: — Toast
    // ============================================================

    fun showToast(message: String, type: ToastType = ToastType.INFO) {
        viewModelScope.launch {
            _toast.value = ToastMessage(message, type)
            delay(3000)
            _toast.value = null
        }
    }

    fun dismissToast() {
        _toast.value = null
    }

    // ============================================================
    // MARK: — Reset app (logout / pulizia dati)
    // ============================================================

    fun resetApp() {
        profileRepo.clear()
        bookingRepo.clear()
        _profile.value      = UserProfile()
        _bookings.value     = emptyList()
        _activeTab.value    = AppTab.HOME
        _eventFilter.value  = EventFilter()
        _selectedEvent.value = null
        _pendingBooking.value = null
    }
}
