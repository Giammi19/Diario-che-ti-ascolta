// ============================================================
// UserProfile.kt
// Il Diario che ti Ascolta — Profilo Utente
// ============================================================

package com.ium.diario.models

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// ============================================================
// MARK: — Enums Profilo
// ============================================================

enum class Weekday(val label: String, val short: String) {
    LUN("Lunedì",    "L"),
    MAR("Martedì",   "M"),
    MER("Mercoledì", "M"),
    GIO("Giovedì",   "G"),
    VEN("Venerdì",   "V"),
    SAB("Sabato",    "S"),
    DOM("Domenica",  "D")
}

enum class TimeSlot(val label: String, val emoji: String) {
    MORNING  ("Mattina",   "🌅"),
    AFTERNOON("Pomeriggio", "☀️"),
    EVENING  ("Sera",      "🌙")
}

enum class ProfileValidationError {
    EMPTY_NAME,
    NO_INTERESTS,
    NO_DAYS
}

// ============================================================
// MARK: — Notifiche
// ============================================================

enum class NotificationFrequency(val label: String) {
    DAILY   ("Giornaliera"),
    WEEKLY  ("Settimanale"),
    REALTIME("In tempo reale")
}

data class NotificationSettings(
    val enabled         : Boolean = true,
    val newEvents       : Boolean = true,
    val bookingReminders: Boolean = true,
    val frequency       : NotificationFrequency = NotificationFrequency.DAILY,
    val focusMode       : Boolean = false,
    val focusStart      : String  = "22:00",
    val focusEnd        : String  = "08:00"
)

// ============================================================
// MARK: — Modello Profilo
// ============================================================

data class UserProfile(
    val name                : String       = "",
    val email               : String       = "",
    val city                : String       = "Milano",
    val job                 : String?      = null,
    val interests           : List<String> = emptyList(),
    val favoriteIds         : List<String> = emptyList(),
    val freeDays            : List<Weekday> = Weekday.values().toList(),
    val preferredTimeSlot   : TimeSlot      = TimeSlot.AFTERNOON,
    val notificationsEnabled: Boolean      = true,
    val onboardingCompleted : Boolean      = false,
    val avatarEmoji         : String       = "👤",
    val notifications       : NotificationSettings = NotificationSettings()
) {
    // ── Proprietà derivate ───────────────────────────────────

    val isNameValid       : Boolean get() = name.length >= 2
    val isEmailValid      : Boolean get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isCityValid       : Boolean get() = city.isNotBlank()
    val isProfileComplete : Boolean get() = isNameValid && isEmailValid && isCityValid

    fun validate(): List<ProfileValidationError> {
        val errors = mutableListOf<ProfileValidationError>()
        if (name.isBlank()) errors.add(ProfileValidationError.EMPTY_NAME)
        if (interests.isEmpty()) errors.add(ProfileValidationError.NO_INTERESTS)
        if (freeDays.isEmpty()) errors.add(ProfileValidationError.NO_DAYS)
        return errors
    }

    fun validationErrors(): List<String> {
        val errors = mutableListOf<String>()
        if (!isNameValid)  errors.add("Nome troppo breve")
        if (!isEmailValid) errors.add("Email non valida")
        if (!isCityValid)  errors.add("Seleziona una città")
        return errors
    }

    // Computed property 'displayName'
    val displayName: String get() = if (name.isBlank()) "Utente" else name

    val initials: String get() {
        val parts = name.trim().split(" ")
        return when {
            parts.isEmpty() || parts[0].isEmpty() -> "?"
            parts.size == 1 -> parts[0].take(1).uppercase()
            else -> (parts[0].take(1) + parts.last().take(1)).uppercase()
        }
    }

    fun hasFavorite(eventId: String): Boolean = favoriteIds.contains(eventId)

    fun toggleFavorite(eventId: String): UserProfile {
        val newFavorites = if (hasFavorite(eventId)) favoriteIds - eventId else favoriteIds + eventId
        return this.copy(favoriteIds = newFavorites)
    }
}

// ============================================================
// MARK: — Differenze Profilo (per Save logic)
// ============================================================

data class ProfileDiff(val changedFields: List<String>) {
    val hasChanges: Boolean get() = changedFields.isNotEmpty()

    val summary: String get() = when {
        !hasChanges -> "Nessuna modifica"
        else        -> "Modificato: " + changedFields.joinToString(", ")
    }

    companion object {
        fun between(old: UserProfile, new: UserProfile): ProfileDiff {
            val changes = mutableListOf<String>()
            if (old.name != new.name)   changes.add("Nome")
            if (old.email != new.email)  changes.add("Email")
            if (old.city != new.city)   changes.add("Città")
            if (old.job != new.job)     changes.add("Professione")
            if (old.interests != new.interests) changes.add("Interessi")
            if (old.freeDays != new.freeDays)   changes.add("Giorni liberi")
            if (old.preferredTimeSlot != new.preferredTimeSlot) changes.add("Fascia oraria")
            if (old.avatarEmoji != new.avatarEmoji) changes.add("Avatar")
            if (old.notifications != new.notifications) changes.add("Notifiche")
            return ProfileDiff(changes)
        }
    }
}

// ============================================================
// MARK: — Repository (SharedPreferences)
// ============================================================

class ProfileRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    // ── Lettura ──────────────────────────────────────────────

    fun load(): UserProfile {
        val name        = prefs.getString(KEY_NAME,         "") ?: ""
        val email       = prefs.getString(KEY_EMAIL,        "") ?: ""
        val city        = prefs.getString(KEY_CITY,         "Milano") ?: "Milano"
        val job         = prefs.getString(KEY_JOB,          null)
        val avatar      = prefs.getString(KEY_AVATAR,       "👤") ?: "👤"
        val notify      = prefs.getBoolean(KEY_NOTIFICATIONS, true)
        val onboarding  = prefs.getBoolean(KEY_ONBOARDING,   false)

        val interestsJson = prefs.getString(KEY_INTERESTS, null)
        val favoritesJson = prefs.getString(KEY_FAVORITES, null)
        val settingsJson  = prefs.getString(KEY_SETTINGS,  null)
        val daysJson      = prefs.getString(KEY_DAYS,      null)
        val slot          = prefs.getString(KEY_SLOT,      TimeSlot.AFTERNOON.name) ?: TimeSlot.AFTERNOON.name

        val listType = object : TypeToken<List<String>>() {}.type
        val interests: List<String> = if (interestsJson != null)
            gson.fromJson(interestsJson, listType) else emptyList()

        val favorites: List<String> = if (favoritesJson != null)
            gson.fromJson(favoritesJson, listType) else emptyList()

        val daysType = object : TypeToken<List<Weekday>>() {}.type
        val freeDays: List<Weekday> = if (daysJson != null)
            gson.fromJson(daysJson, daysType) else Weekday.values().toList()

        val settings: NotificationSettings = if (settingsJson != null)
            gson.fromJson(settingsJson, NotificationSettings::class.java) else NotificationSettings()

        return UserProfile(
            name                 = name,
            email                = email,
            city                 = city,
            job                  = job,
            interests            = interests,
            favoriteIds          = favorites,
            freeDays             = freeDays,
            preferredTimeSlot    = try { TimeSlot.valueOf(slot) } catch(e: Exception) { TimeSlot.AFTERNOON },
            notificationsEnabled = notify,
            onboardingCompleted  = onboarding,
            avatarEmoji          = avatar,
            notifications        = settings
        )
    }

    // ── Scrittura ────────────────────────────────────────────

    fun save(profile: UserProfile) {
        prefs.edit {
            putString(KEY_NAME,          profile.name)
            putString(KEY_EMAIL,         profile.email)
            putString(KEY_CITY,          profile.city)
            putString(KEY_JOB,           profile.job)
            putString(KEY_AVATAR,        profile.avatarEmoji)
            putBoolean(KEY_NOTIFICATIONS, profile.notificationsEnabled)
            putBoolean(KEY_ONBOARDING,    profile.onboardingCompleted)
            putString(KEY_INTERESTS,     gson.toJson(profile.interests))
            putString(KEY_FAVORITES,     gson.toJson(profile.favoriteIds))
            putString(KEY_SETTINGS,      gson.toJson(profile.notifications))
            putString(KEY_DAYS,          gson.toJson(profile.freeDays))
            putString(KEY_SLOT,          profile.preferredTimeSlot.name)
        }
    }

    fun clear() {
        prefs.edit { clear() }
    }

    // ── Helper Onboarding ────────────────────────────────────

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean(KEY_ONBOARDING, false)

    fun markOnboardingCompleted() {
        prefs.edit { putBoolean(KEY_ONBOARDING, true) }
    }

    companion object {
        private const val PREFS_NAME        = "diario_user_profile"
        private const val KEY_NAME          = "user_name"
        private const val KEY_EMAIL         = "user_email"
        private const val KEY_CITY          = "user_city"
        private const val KEY_JOB           = "user_job"
        private const val KEY_AVATAR        = "user_avatar"
        private const val KEY_NOTIFICATIONS = "user_notify_enabled"
        private const val KEY_ONBOARDING    = "user_onboarding_done"
        private const val KEY_INTERESTS     = "user_interests_json"
        private const val KEY_FAVORITES     = "user_favorites_json"
        private const val KEY_SETTINGS      = "user_notif_settings_json"
        private const val KEY_DAYS          = "user_free_days_json"
        private const val KEY_SLOT          = "user_time_slot"
    }
}

// ============================================================
// MARK: — Dati Statici Interessi
// ============================================================

object AvailableInterests {
    data class InterestItem(val label: String, val emoji: String, val category: String)

    val all = listOf(
        InterestItem("Musica Live", "🎸", "Arte"),
        InterestItem("Teatro",      "🎭", "Arte"),
        InterestItem("Cinema",      "🎬", "Arte"),
        InterestItem("Mostre",      "🎨", "Arte"),
        InterestItem("Lettura",     "📚", "Cultura"),
        InterestItem("Workshop",    "🛠️", "Cultura"),
        InterestItem("Sport",       "⚽", "Lifestyle"),
        InterestItem("Cucina",      "🍳", "Lifestyle"),
        InterestItem("Natura",      "🌲", "Lifestyle")
    )

    val labels: List<String> get() = all.map { it.label }
}

object AvailableAvatars {
    val emojis = listOf("👤", "👩‍💻", "👨‍💻", "🦊", "🦁", "🐨", "✨", "🎨", "🎸")
}
