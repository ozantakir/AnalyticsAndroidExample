package com.example.core.analytics.destination

/**
 * Interface for events that should be sent to Firebase.
 */
interface FirebaseEvent {
    fun toFirebaseParams(referenceData: Map<String, Any?>): Map<String, Any?>
}

/**
 * Interface for events that should be sent to Insider.
 */
interface InsiderEvent {
    fun toInsiderParams(referenceData: Map<String, Any?>): Map<String, Any?>
}

/**
 * Interface for events that should be sent to Adjust.
 */
interface AdjustEvent {
    fun toAdjustParams(referenceData: Map<String, Any?>): Map<String, Any?>
}
