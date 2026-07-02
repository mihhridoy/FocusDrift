package com.radonshadow.focusdrift.data.remote.firebase

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/** Publishes the local display name/avatar color to `users/{uid}` so other room participants can look it up. */
class FirebaseUserManager @Inject constructor(
    private val database: DatabaseReference
) {
    suspend fun publishProfile(userId: String, displayName: String, avatarColor: String) {
        database.child("users").child(userId).updateChildren(
            mapOf(
                "displayName" to displayName,
                "avatarColor" to avatarColor,
                "lastSeen" to System.currentTimeMillis()
            )
        ).await()
    }
}
