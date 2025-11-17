package com.bearkicks.app.features.auth.data.datasource

import com.bearkicks.app.features.auth.domain.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class ProfileDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val birthDate: Long? = null,
    val photoPath: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)

class AuthFirebaseDataSource(
    private val auth: FirebaseAuth,
    private val rootRef: DatabaseReference,
    private val usersRef: DatabaseReference,
    private val usernamesRef: DatabaseReference
) {
    private fun userNode(uid: String) = usersRef.child(uid)
    private fun usernameNode(username: String) = usernamesRef.child(username)

    fun observeUser(): Flow<UserModel?> = callbackFlow {
        val authListener = FirebaseAuth.AuthStateListener { fba ->
            val user = fba.currentUser
            if (user == null) {
                trySend(null)
            } else {
                userNode(user.uid).get().addOnSuccessListener { snap ->
                    val dto = snap.getValue(ProfileDto::class.java)
                    trySend(
                        UserModel(
                            id = user.uid,
                            firstName = dto?.firstName ?: "",
                            lastName = dto?.lastName ?: "",
                            username = dto?.username ?: "",
                            email = dto?.email ?: user.email.orEmpty(),
                            phone = dto?.phone,
                            address = dto?.address,
                            birthDate = dto?.birthDate,
                            photoPath = dto?.photoPath,
                            createdAt = dto?.createdAt,
                            updatedAt = dto?.updatedAt
                        )
                    )
                }.addOnFailureListener { trySend(null) }
            }
        }
        auth.addAuthStateListener(authListener)
        awaitClose { auth.removeAuthStateListener(authListener) }
    }

    suspend fun getCurrentUser(): UserModel? {
        val user = auth.currentUser ?: return null
        val dto = userNode(user.uid).get().await().getValue(ProfileDto::class.java)
        return UserModel(
            id = user.uid,
            firstName = dto?.firstName ?: "",
            lastName = dto?.lastName ?: "",
            username = dto?.username ?: "",
            email = dto?.email ?: user.email.orElse(),
            phone = dto?.phone,
            address = dto?.address,
            birthDate = dto?.birthDate,
            photoPath = dto?.photoPath,
            createdAt = dto?.createdAt,
            updatedAt = dto?.updatedAt
        )
    }

    suspend fun login(email: String, password: String): UserModel {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("No se pudo iniciar sesión")
        val dto = userNode(user.uid).get().await().getValue(ProfileDto::class.java)
        return UserModel(
            id = user.uid,
            firstName = dto?.firstName ?: "",
            lastName = dto?.lastName ?: "",
            username = dto?.username ?: "",
            email = dto?.email ?: user.email.orElse(),
            phone = dto?.phone,
            address = dto?.address,
            birthDate = dto?.birthDate,
            photoPath = dto?.photoPath,
            createdAt = dto?.createdAt,
            updatedAt = dto?.updatedAt
        )
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        phone: String?,
        address: String?,
        birthDate: Long,
        password: String,
        photoPath: String?
    ): UserModel {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("No se pudo crear el usuario")
        val now = System.currentTimeMillis()
        // Multi-path update para nodo usuario + índice de username
        val updates = mapOf(
            "users/${user.uid}/firstName" to firstName,
            "users/${user.uid}/lastName" to lastName,
            "users/${user.uid}/username" to username,
            "users/${user.uid}/email" to email,
            "users/${user.uid}/phone" to phone,
            "users/${user.uid}/address" to address,
            "users/${user.uid}/birthDate" to birthDate,
            "users/${user.uid}/photoPath" to photoPath,
            "users/${user.uid}/createdAt" to now,
            "users/${user.uid}/updatedAt" to now,
            "usernames/$username" to user.uid
        )
        rootRef.updateChildren(updates).await()
        return UserModel(
            id = user.uid,
            firstName = firstName,
            lastName = lastName,
            username = username,
            email = email,
            phone = phone,
            address = address,
            birthDate = birthDate,
            photoPath = photoPath,
            createdAt = now,
            updatedAt = now
        )
    }

    suspend fun logout() { auth.signOut() }

    suspend fun updateProfile(
        firstName: String? = null,
        lastName: String? = null,
        phone: String? = null,
        address: String? = null,
        photoPath: String? = null
    ): UserModel {
        val user = auth.currentUser ?: throw IllegalStateException("No autenticado")
        val updates = mutableMapOf<String, Any?>()
        if (firstName != null) updates["firstName"] = firstName
        if (lastName != null) updates["lastName"] = lastName
        if (phone != null) updates["phone"] = phone
        if (address != null) updates["address"] = address
        if (photoPath != null) updates["photoPath"] = photoPath
        updates["updatedAt"] = System.currentTimeMillis()
        if (updates.isNotEmpty()) userNode(user.uid).updateChildren(updates).await()
        val dto = userNode(user.uid).get().await().getValue(ProfileDto::class.java)
        return UserModel(
            id = user.uid,
            firstName = dto?.firstName ?: "",
            lastName = dto?.lastName ?: "",
            username = dto?.username ?: "",
            email = dto?.email ?: user.email.orElse(),
            phone = dto?.phone,
            address = dto?.address,
            birthDate = dto?.birthDate,
            photoPath = dto?.photoPath,
            createdAt = dto?.createdAt,
            updatedAt = dto?.updatedAt
        )
    }
}

private fun String?.orElse(default: String = ""): String = this ?: default
