package com.shrimpdevs.digitalassistant.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.shrimpdevs.digitalassistant.dao.EventDao
import com.shrimpdevs.digitalassistant.dao.EventResult
import com.shrimpdevs.digitalassistant.models.Event
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseEventDao(private val db: FirebaseFirestore) : EventDao {
    private var listeners = mutableListOf<ListenerRegistration>()

    override suspend fun getAllEventsForUser(userId: String): EventResult<List<Event>> =
        suspendCoroutine { continuation ->
            try {
                db.collection("users")
                    .document(userId)
                    .collection("events")
                    .get()
                    .addOnSuccessListener { documents ->
                        val events = documents.mapNotNull { doc ->
                            doc.toObject(Event::class.java)
                        }.sortedBy { it.eventDate.toDate() }
                        continuation.resume(EventResult.Success(events))
                    }
                    .addOnFailureListener { e ->
                        continuation.resume(EventResult.Error(e))
                    }
            } catch (e: Exception) {
                continuation.resume(EventResult.Error(e))
            }
        }

    override suspend fun insertEvent(event: Event, userId: String): EventResult<Unit> =
        suspendCoroutine { continuation ->
            if (!event.isValid()) {
                continuation.resume(EventResult.Error(IllegalArgumentException("Datos de evento inválidos")))
                return@suspendCoroutine
            }

            try {
                db.collection("users")
                    .document(userId)
                    .collection("events")
                    .add(event)
                    .addOnSuccessListener {
                        continuation.resume(EventResult.Success(Unit))
                    }
                    .addOnFailureListener { e ->
                        continuation.resume(EventResult.Error(e))
                    }
            } catch (e: Exception) {
                continuation.resume(EventResult.Error(e))
            }
        }

    override suspend fun updateEvent(originalTitle: String, event: Event, userId: String): EventResult<Unit> =
        suspendCoroutine { continuation ->
            if (!event.isValid()) {
                continuation.resume(EventResult.Error(IllegalArgumentException("Datos de evento inválidos")))
                return@suspendCoroutine
            }

            try {
                db.collection("users")
                    .document(userId)
                    .collection("events")
                    .whereEqualTo("title", originalTitle)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            document.reference.set(event)
                        }
                        continuation.resume(EventResult.Success(Unit))
                    }
                    .addOnFailureListener { e ->
                        continuation.resume(EventResult.Error(e))
                    }
            } catch (e: Exception) {
                continuation.resume(EventResult.Error(e))
            }
        }

    override suspend fun deleteEvent(title: String, userId: String): EventResult<Unit> =
        suspendCoroutine { continuation ->
            try {
                db.collection("users")
                    .document(userId)
                    .collection("events")
                    .whereEqualTo("title", title)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            document.reference.delete()
                        }
                        continuation.resume(EventResult.Success(Unit))
                    }
                    .addOnFailureListener { e ->
                        continuation.resume(EventResult.Error(e))
                    }
            } catch (e: Exception) {
                continuation.resume(EventResult.Error(e))
            }
        }

    override fun observeEventsForUser(userId: String, onUpdate: (List<Event>) -> Unit) {
        val listener = db.collection("users")
            .document(userId)
            .collection("events")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                val events = snapshot?.documents?.mapNotNull {
                    it.toObject(Event::class.java)
                }?.sortedBy { it.eventDate.toDate() } ?: emptyList()

                onUpdate(events)
            }
        listeners.add(listener)
    }

    override fun clearListeners() {
        listeners.forEach { it.remove() }
        listeners.clear()
    }
}