package com.bearkicks.app.features.cart.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.bearkicks.app.features.cart.presentation.qr.QrImage
import com.bearkicks.app.features.cart.presentation.qr.generateQrBitmap
import com.bearkicks.app.features.cart.presentation.qr.saveQrToGallery
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.*
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.bearkicks.app.features.cart.domain.model.CartItem
import com.bearkicks.app.features.cart.domain.model.payment.vo.CardNumber
import com.bearkicks.app.features.cart.domain.model.payment.vo.ExpiryDate
import com.bearkicks.app.features.cart.domain.model.payment.vo.Cvv
import com.bearkicks.app.features.cart.domain.model.payment.vo.CardHolderName
import com.bearkicks.app.core.errors.DomainException
import com.bearkicks.app.ui.strings.errorTextRes

@Composable
fun CheckoutBottomSheet(
    items: List<CartItem>,
    onDismiss: () -> Unit,
    onPayCard: (number: String, expiry: String, cvv: String, holder: String) -> Unit,
    onPayQr: (provider: String, timestamp: Long) -> Unit
) {
    var tab by remember { mutableStateOf(0) }
    val total = items.sumOf { it.subtotal }

    Surface(tonalElevation = 2.dp) {
        Column(Modifier.padding(20.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(stringResource(id = com.bearkicks.app.R.string.checkout_title), style = MaterialTheme.typography.titleMedium)
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab==0, onClick={tab=0}, text={Text(stringResource(id = com.bearkicks.app.R.string.checkout_tab_card))})
                Tab(selected = tab==1, onClick={tab=1}, text={Text(stringResource(id = com.bearkicks.app.R.string.checkout_tab_qr))})
            }
            if (tab==0) CardForm(total = total, onPay = onPayCard) else QrForm(total = total, onPay = onPayQr)
            TextButton(onClick = onDismiss) { Text(stringResource(id = com.bearkicks.app.R.string.common_cancel)) }
        }
    }
}

@Composable
private fun CardForm(total: Double, onPay: (String,String,String,String)->Unit) {
    val context = LocalContext.current
    var numberDigits by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var holder by remember { mutableStateOf("") }
    var numberErr by remember { mutableStateOf<String?>(null) }
    var expiryErr by remember { mutableStateOf<String?>(null) }
    var cvvErr by remember { mutableStateOf<String?>(null) }
    var holderErr by remember { mutableStateOf<String?>(null) }
    var showCvv by remember { mutableStateOf(false) }
    var monthExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    val currentYear = remember { java.time.Year.now().value }
    var month by remember { mutableStateOf<Int?>(null) }
    var year by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(month, year) {
        if (month != null && year != null) {
            val mm = month!!.toString().padStart(2,'0')
            val yy = year!!.toString().takeLast(2)
            expiry = "$mm/$yy"
            expiryErr = ExpiryDate.create(expiry).exceptionOrNull()?.let { e ->
                (e as? DomainException)?.let { de -> context.getString(errorTextRes(de.key)) } ?: e.message
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = numberDigits,
            onValueChange = { raw ->
                val digits = raw.filter { it.isDigit() }.take(19)
                numberDigits = digits
                val grouped = groupCardDigits(digits)
                numberErr = if (digits.isNotBlank()) {
                    CardNumber.create(grouped).exceptionOrNull()?.let { e ->
                        (e as? DomainException)?.let { de -> context.getString(errorTextRes(de.key)) } ?: e.message
                    }
                } else context.getString(com.bearkicks.app.R.string.invalid_number)
            },
            label={Text(stringResource(id = com.bearkicks.app.R.string.field_card_number))},
            isError = numberErr!=null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = CardNumberSpacingVisualTransformation()
        )
        numberErr?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = month?.toString() ?: stringResource(id = com.bearkicks.app.R.string.field_month),
                    onValueChange = {},
                    label = { Text(stringResource(id = com.bearkicks.app.R.string.field_month)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { TextButton(onClick = { monthExpanded = !monthExpanded }) { Text(if (monthExpanded) "▲" else "▼") } }
                )
                DropdownMenu(expanded = monthExpanded, onDismissRequest = { monthExpanded = false }) {
                    (1..12).forEach { m ->
                        DropdownMenuItem(onClick = { month = m; monthExpanded = false }, text = { Text(m.toString()) })
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = year?.toString() ?: stringResource(id = com.bearkicks.app.R.string.field_year),
                    onValueChange = {},
                    label = { Text(stringResource(id = com.bearkicks.app.R.string.field_year)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { TextButton(onClick = { yearExpanded = !yearExpanded }) { Text(if (yearExpanded) "▲" else "▼") } }
                )
                DropdownMenu(expanded = yearExpanded, onDismissRequest = { yearExpanded = false }) {
                    (currentYear..currentYear+15).forEach { y ->
                        DropdownMenuItem(onClick = { year = y; yearExpanded = false }, text = { Text(y.toString()) })
                    }
                }
            }
        }
        if (expiry.isNotBlank()) {
            expiryErr?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        }
        OutlinedTextField(cvv, {
            cvv = it; cvvErr = Cvv.create(it).exceptionOrNull()?.let { e ->
                (e as? DomainException)?.let { de -> context.getString(errorTextRes(de.key)) } ?: e.message
            }
        }, label={Text(stringResource(id = com.bearkicks.app.R.string.field_cvv))}, isError = cvvErr!=null, visualTransformation = if (showCvv) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon={
            TextButton(onClick={showCvv=!showCvv}) { Text(if(showCvv) stringResource(id = com.bearkicks.app.R.string.common_hide) else stringResource(id = com.bearkicks.app.R.string.common_show)) }
        })
        cvvErr?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        OutlinedTextField(
            value = holder,
            onValueChange = { raw ->
                val filtered = raw.filter { it.isLetter() || it.isWhitespace() }
                    .replace(Regex("\\s+"), " ")
                    .trimStart()
                holder = filtered
                holderErr = if (filtered.isNotBlank()) {
                    CardHolderName.create(filtered).exceptionOrNull()?.let { e ->
                        (e as? DomainException)?.let { de -> context.getString(errorTextRes(de.key)) } ?: e.message
                    }
                } else context.getString(com.bearkicks.app.R.string.invalid_name)
            },
            label={Text(stringResource(id = com.bearkicks.app.R.string.field_card_holder))},
            isError = holderErr!=null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        holderErr?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        val groupedNumber = groupCardDigits(numberDigits)
        val disabled = listOf(numberErr, expiryErr, cvvErr, holderErr).any { it!=null } || groupedNumber.isBlank() || expiry.isBlank() || cvv.isBlank() || holder.isBlank()
        Button(onClick = { if(!disabled) onPay(groupedNumber, expiry, cvv, holder) }, enabled = !disabled) { Text(stringResource(id = com.bearkicks.app.R.string.checkout_pay_bob, String.format("%.2f", total))) }
    }
}

@Composable
private fun QrForm(total: Double, onPay: (String, Long)->Unit) {
    val provider = "BearKicksCompany"
    var generatedAt by remember { mutableStateOf<Long?>(null) }
    var payload by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    Text(stringResource(id = com.bearkicks.app.R.string.checkout_qr_hint))
    OutlinedTextField(value = provider, onValueChange = {}, readOnly = true, label={Text(stringResource(id = com.bearkicks.app.R.string.checkout_qr_provider))})
    if (generatedAt == null) {
        Button(onClick = {
            generatedAt = System.currentTimeMillis()
            payload = "$provider:$total:$generatedAt"
        }) { Text(stringResource(id = com.bearkicks.app.R.string.checkout_generate_qr_bob, String.format("%.2f", total))) }
    } else {
        payload?.let { p -> QrPreview(p) }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { onPay(provider, generatedAt!!) }) { Text(stringResource(id = com.bearkicks.app.R.string.checkout_confirm_payment_bob, String.format("%.2f", total))) }
            Button(onClick = {
                payload?.let { p ->
                    val bmp = generateQrBitmap(p)
                    val saved = saveQrToGallery(context, bmp, "qr_$generatedAt.png")
                    Toast.makeText(context, if (saved) context.getString(com.bearkicks.app.R.string.checkout_qr_saved) else context.getString(com.bearkicks.app.R.string.common_error_saving), Toast.LENGTH_SHORT).show()
                }
            }) { Text(stringResource(id = com.bearkicks.app.R.string.checkout_save_qr)) }
        }
    }
}

@Composable
private fun QrPreview(payload: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(id = com.bearkicks.app.R.string.checkout_qr_generated), style = MaterialTheme.typography.titleSmall)
        QrImage(payload = payload, size = 160.dp)
        Text(payload, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
