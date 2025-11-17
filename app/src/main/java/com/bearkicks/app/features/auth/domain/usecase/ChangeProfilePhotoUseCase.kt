package com.bearkicks.app.features.auth.domain.usecase

import android.net.Uri
import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.repository.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ChangeProfilePhotoUseCase(
    private val storage: FirebaseStorage,
    private val repo: IAuthRepository,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(uri: Uri): Result<UserModel> {
        val user = auth.currentUser ?: return Result.failure(IllegalStateException("No autenticado"))
        return try {
            val ref = storage.reference.child("profilePhotos/${user.uid}/original.jpg")
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            repo.updateProfile(photoPath = downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
