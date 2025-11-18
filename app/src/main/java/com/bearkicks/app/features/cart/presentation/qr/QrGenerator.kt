package com.bearkicks.app.features.cart.presentation.qr

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.stringResource
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

fun generateQrBitmap(payload: String, sizePx: Int = 512): Bitmap {
    val writer = QRCodeWriter()
    val hints = mapOf(
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
        EncodeHintType.MARGIN to 1
    )
    val bitMatrix = writer.encode(payload, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    for (x in 0 until sizePx) {
        for (y in 0 until sizePx) {
            bmp.setPixel(x, y, if (bitMatrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
        }
    }
    return bmp
}

@Composable
fun QrImage(payload: String, size: Dp = 128.dp, modifier: Modifier = Modifier) {
    val bmp = remember(payload) { generateQrBitmap(payload, sizePx = size.value.toInt() * 4) }
    Image(
        bitmap = bmp.asImageBitmap(),
        contentDescription = stringResource(id = com.bearkicks.app.R.string.cd_qr_code),
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}
