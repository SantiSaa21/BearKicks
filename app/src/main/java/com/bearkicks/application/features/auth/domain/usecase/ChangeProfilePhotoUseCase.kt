package com.bearkicks.application.features.auth.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.bearkicks.application.features.auth.domain.model.UserModel
import com.bearkicks.application.features.auth.domain.repository.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream
import com.bearkicks.application.core.errors.DomainException
import com.bearkicks.application.core.errors.ErrorKey

class ChangeProfilePhotoUseCase(
    private val repo: IAuthRepository,
    private val auth: FirebaseAuth,
    private val context: Context
) {
    suspend operator fun invoke(uri: Uri): Result<UserModel> {
        val user = auth.currentUser ?: return Result.failure(DomainException(ErrorKey.NOT_AUTHENTICATED))
        return runCatching {
            val bytes = context.contentResolver.openInputStream(uri)?.use { input ->
                val opts = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
                val original = BitmapFactory.decodeStream(input, null, opts)
                    ?: throw DomainException(ErrorKey.IMAGE_READ_ERROR)
                val scaled = scaleDown(original, 512)
                val out = ByteArrayOutputStream()
                scaled.compress(Bitmap.CompressFormat.JPEG, 82, out)
                out.toByteArray()
            } ?: throw DomainException(ErrorKey.IMAGE_OPEN_ERROR)

            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            val dataUri = "data:image/jpeg;base64,$base64"
            repo.updateProfile(photoPath = dataUri).getOrThrow()
        }
    }

    private fun scaleDown(src: Bitmap, maxSize: Int): Bitmap {
        val w = src.width
        val h = src.height
        if (w <= maxSize && h <= maxSize) return src
        val ratio = if (w >= h) maxSize / w.toFloat() else maxSize / h.toFloat()
        val newW = (w * ratio).toInt()
        val newH = (h * ratio).toInt()
        return Bitmap.createScaledBitmap(src, newW, newH, true)
    }
}
