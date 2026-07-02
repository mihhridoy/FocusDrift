package com.radonshadow.focusdrift.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/** Anonymous auth is enough for body doubling — rooms only need a stable, disposable uid. */
class FirebaseAuthManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    suspend fun ensureSignedIn(): String {
        firebaseAuth.currentUser?.let { return it.uid }
        val result = firebaseAuth.signInAnonymously().await()
        return result.user?.uid ?: error("Anonymous sign-in returned no user")
    }
}
