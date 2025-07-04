package com.shrimpdevs.digitalassistant.dao

import com.shrimpdevs.digitalassistant.models.Event

sealed class EventResult<out T> {
    data class Success<T>(val data: T) : EventResult<T>()
    data class Error(val exception: Exception) : EventResult<Nothing>()
}

interface EventDao {
    suspend fun getAllEventsForUser(userId: String): EventResult<List<Event>>
    suspend fun insertEvent(event: Event, userId: String): EventResult<Unit>
    suspend fun updateEvent(originalTitle: String, event: Event, userId: String): EventResult<Unit>
    suspend fun deleteEvent(title: String, userId: String): EventResult<Unit>
    fun observeEventsForUser(userId: String, onUpdate: (List<Event>) -> Unit)
    fun clearListeners()
}