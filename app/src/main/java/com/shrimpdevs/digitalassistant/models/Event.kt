package com.shrimpdevs.digitalassistant.models

import com.google.firebase.Timestamp
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class Event(
    var title: String = "",
    var description: String = "",
    var eventDate: Timestamp = Timestamp.now(),
    var location: String = "",
    var alarm: Boolean = false
) : Serializable {
    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(eventDate.toDate())
    }

    fun isValid(): Boolean {
        return title.isNotBlank() &&
                description.isNotBlank() &&
                location.isNotBlank()
    }
}