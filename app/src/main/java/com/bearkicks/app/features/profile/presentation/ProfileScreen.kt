package com.bearkicks.app.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import com.bearkicks.app.features.cart.domain.usecase.ObserveOrdersUseCase
import com.bearkicks.app.navigation.Screen
import com.bearkicks.app.features.cart.domain.usecase.ClearOrdersUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import com.bearkicks.app.features.auth.domain.model.vo.Name
import com.bearkicks.app.features.auth.domain.model.vo.LastName
import com.bearkicks.app.features.auth.domain.model.vo.Phone
import com.bearkicks.app.features.auth.domain.model.vo.Address
import com.bearkicks.app.features.auth.domain.model.vo.Password
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.bearkicks.app.ui.theme.BKBrandPrimary
import com.bearkicks.app.ui.theme.BKBrandPrimaryLight
import com.bearkicks.app.ui.theme.BKBrandSecondary
import com.bearkicks.app.ui.theme.BKBrandAccent
import com.bearkicks.app.ui.theme.BKNeutral95
import com.bearkicks.app.ui.theme.BKNeutral100

@Composable
fun ProfileScreen(onLoggedOut: () -> Unit, onSeeAllOrders: () -> Unit) {
    val viewModel: ProfileViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsState()

    when (val state = uiState) {
        is ProfileUiState.Authenticated -> ProfileContent(
            state = state,
            onChangePhoto = { },
            onLogout = { viewModel.onLogout(); onLoggedOut() },
            onClearHistory = { viewModel.onClearOrders() },
            onSeeAllOrders = onSeeAllOrders,
            onSaveProfile = { firstName, lastName, phone, address -> viewModel.onSaveProfile(firstName, lastName, phone, address) },
            onUpdatePhotoUrl = { path -> viewModel.onChangePhotoUrl(path) },
            onPickPhoto = { uri -> viewModel.onPickPhoto(uri) },
            onChangePassword = { current, new, confirm, cb -> viewModel.onChangePassword(current, new, confirm, cb) },
            onVerifyCurrentPassword = { curr, cb -> viewModel.onVerifyCurrentPassword(curr, cb) }
        )
        ProfileUiState.LoggedOut -> Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text(stringResource(id = com.bearkicks.app.R.string.profile_logged_out), style = MaterialTheme.typography.titleMedium)
        }
        is ProfileUiState.Error -> Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text(stringResource(id = com.bearkicks.app.ui.strings.errorTextRes(state.key)), color = MaterialTheme.colorScheme.error)
        }
        ProfileUiState.Loading -> Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text(stringResource(id = com.bearkicks.app.R.string.common_loading))
        }
    }
}

@Composable
private fun ProfileContent(
    state: ProfileUiState.Authenticated,
    onChangePhoto: () -> Unit,
    onLogout: () -> Unit,
    onClearHistory: () -> Unit,
    onSeeAllOrders: () -> Unit,
    onSaveProfile: (String?, String?, String?, String?) -> Unit,
    onUpdatePhotoUrl: (String) -> Unit,
    onPickPhoto: (Uri) -> Unit,
    onChangePassword: (String, String, String, (Result<Unit>) -> Unit) -> Unit,
    onVerifyCurrentPassword: (String, (Boolean, com.bearkicks.app.core.errors.ErrorKey?) -> Unit) -> Unit
) {
    var editOpen = rememberSaveable { mutableStateOf(false) }
    var passwordOpen = rememberSaveable { mutableStateOf(false) }
    var passwordResult = rememberSaveable { mutableStateOf<Result<Unit>?>(null) }
    // Picker ya no usa diálogo manual
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { onPickPhoto(it) }
    }

    val observeOrders: ObserveOrdersUseCase = koinInject()
    val orders by observeOrders().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header minimalista
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Avatar(
                    url = state.user.photoPath,
                    name = "${state.user.firstName} ${state.user.lastName}",
                    onPickPhoto = { imagePicker.launch("image/*") }
                )
                Text(
                    state.user.username.ifBlank { "(username)" },
                    style = MaterialTheme.typography.titleLarge,
                    color = BKBrandPrimary
                )
                Text(
                    state.user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(BKBrandPrimaryLight.copy(alpha = 0.25f))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    color = BKBrandPrimary
                )
            }
        }

        // Datos contacto
        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = BKNeutral100)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                state.user.birthDate?.let { bd ->
                    InfoRow(icon = Icons.Filled.DateRange, label = stringResource(id = com.bearkicks.app.R.string.profile_birthdate), value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(bd)))
                }
                state.user.phone?.takeIf { it.isNotBlank() }?.let {
                    InfoRow(icon = Icons.Filled.Phone, label = stringResource(id = com.bearkicks.app.R.string.field_phone), value = it)
                }
                InfoRow(icon = Icons.Filled.Person, label = stringResource(id = com.bearkicks.app.R.string.field_first_name), value = "${state.user.firstName} ${state.user.lastName}".trim())
                state.user.address?.takeIf { it.isNotBlank() }?.let {
                    InfoRow(icon = Icons.Filled.Home, label = stringResource(id = com.bearkicks.app.R.string.field_address), value = it)
                } ?: Text(stringResource(id = com.bearkicks.app.R.string.profile_add_address_hint), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Acciones rápidas
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = BKBrandSecondary.copy(alpha = 0.55f))) {
            Column(Modifier.fillMaxWidth().padding(8.dp)) {
                ActionItem(text = stringResource(id = com.bearkicks.app.R.string.profile_action_edit), icon = Icons.Filled.Edit) { editOpen.value = true }
                ActionItem(text = stringResource(id = com.bearkicks.app.R.string.profile_action_change_password), icon = Icons.Filled.Lock) { passwordOpen.value = true }
                ActionItem(text = stringResource(id = com.bearkicks.app.R.string.profile_action_orders), icon = Icons.Filled.ShoppingBag) { onSeeAllOrders() }
                ActionItem(text = stringResource(id = com.bearkicks.app.R.string.profile_action_logout), icon = Icons.Filled.ExitToApp, danger = true) { onLogout() }
            }
        }

        if (editOpen.value) {
            EditProfileDialog(
                firstNameInit = state.user.firstName,
                lastNameInit = state.user.lastName,
                phoneInit = state.user.phone.orEmpty(),
                addressInit = state.user.address.orEmpty(),
                onDismiss = { editOpen.value = false },
                onSave = { f, l, p, a ->
                    onSaveProfile(f, l, p, a)
                    editOpen.value = false
                }
            )
        }

        if (passwordOpen.value) {
            ChangePasswordDialog(
                onDismiss = { passwordOpen.value = false },
                result = passwordResult.value,
                onSubmit = { current, new, confirm ->
                    onChangePassword(current, new, confirm) { r ->
                        passwordResult.value = r
                        if (r.isSuccess) passwordOpen.value = false
                    }
                },
                onVerifyCurrent = { pwd, cb -> onVerifyCurrentPassword(pwd, cb) }
            )
        }

        // Diálogo manual de foto eliminado (usamos picker)
    }
}

@Composable
private fun EditProfileDialog(
    firstNameInit: String,
    lastNameInit: String,
    phoneInit: String,
    addressInit: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val firstName = rememberSaveable { mutableStateOf(firstNameInit) }
    val lastName = rememberSaveable { mutableStateOf(lastNameInit) }
    val phone = rememberSaveable { mutableStateOf(phoneInit) }
    val address = rememberSaveable { mutableStateOf(addressInit) }
    val firstNameError = rememberSaveable { mutableStateOf<String?>(null) }
    val lastNameError = rememberSaveable { mutableStateOf<String?>(null) }
    val phoneError = rememberSaveable { mutableStateOf<String?>(null) }
    val addressError = rememberSaveable { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = com.bearkicks.app.R.string.profile_edit_title), style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = firstName.value,
                    onValueChange = {
                        firstName.value = it
                        firstNameError.value = validateName(it)?.let { key -> context.getString(com.bearkicks.app.ui.strings.errorTextRes(key)) }
                    },
                    isError = firstNameError.value != null,
                    label = { Text(stringResource(id = com.bearkicks.app.R.string.field_first_name)) },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    singleLine = true
                )
                firstNameError.value?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                OutlinedTextField(
                    value = lastName.value,
                    onValueChange = {
                        lastName.value = it
                        lastNameError.value = validateLastName(it)?.let { key -> context.getString(com.bearkicks.app.ui.strings.errorTextRes(key)) }
                    },
                    isError = lastNameError.value != null,
                    label = { Text(stringResource(id = com.bearkicks.app.R.string.field_last_name)) },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    singleLine = true
                )
                lastNameError.value?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                OutlinedTextField(
                    value = phone.value,
                    onValueChange = {
                        phone.value = it
                        phoneError.value = validatePhone(it)?.let { key -> context.getString(com.bearkicks.app.ui.strings.errorTextRes(key)) }
                    },
                    isError = phoneError.value != null,
                    label = { Text(stringResource(id = com.bearkicks.app.R.string.field_phone)) },
                    leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                    singleLine = true
                )
                phoneError.value?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                OutlinedTextField(
                    value = address.value,
                    onValueChange = {
                        address.value = it
                        addressError.value = validateAddress(it)?.let { key -> context.getString(com.bearkicks.app.ui.strings.errorTextRes(key)) }
                    },
                    isError = addressError.value != null,
                    label = { Text(stringResource(id = com.bearkicks.app.R.string.field_address)) },
                    leadingIcon = { Icon(Icons.Filled.Home, contentDescription = null) }
                )
                addressError.value?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            val disabled = listOf(firstNameError.value, lastNameError.value, phoneError.value, addressError.value).any { it != null }
            TextButton(onClick = { if (!disabled) onSave(firstName.value, lastName.value, phone.value, address.value) }, enabled = !disabled) { Text(stringResource(id = com.bearkicks.app.R.string.common_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(id = com.bearkicks.app.R.string.common_cancel)) }
        }
    )
}

// Eliminado UpdatePhotoPathDialog (ya no se necesita)
@Composable
private fun Avatar(url: String?, name: String, onPickPhoto: () -> Unit) {
    val initials = name.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
    // Shimmer animation for placeholder
    val shimmerTransition = rememberInfiniteTransition(label = "avatarShimmer")
    val shimmerAlpha by shimmerTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "avatarAlpha"
    )
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(BKNeutral95)
                .border(width = 2.dp, color = BKBrandPrimaryLight, shape = CircleShape)
                .height(110.dp)
                .width(110.dp),
            contentAlignment = Alignment.Center
        ) {
            if (url.isNullOrBlank()) {
                Text(initials, style = MaterialTheme.typography.titleLarge, color = BKBrandPrimary, modifier = Modifier.alpha(shimmerAlpha))
            } else {
                AsyncImage(
                    model = url,
                    contentDescription = stringResource(id = com.bearkicks.app.R.string.profile_avatar_cd),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(2.dp, BKBrandPrimaryLight, CircleShape)
                )
            }
        }
        IconButton(
            onClick = onPickPhoto,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 6.dp, y = 6.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(BKBrandPrimary)
        ) {
            Icon(Icons.Filled.PhotoCamera, contentDescription = stringResource(id = com.bearkicks.app.R.string.profile_change_photo_cd), tint = Color.White)
        }
    }
}

@Composable
private fun rememberDateFormatter(): (Long) -> String {
    val sdf = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    return { millis -> sdf.format(Date(millis)) }
}

private fun validateName(v: String): com.bearkicks.app.core.errors.ErrorKey? =
    (Name.create(v).exceptionOrNull() as? com.bearkicks.app.core.errors.DomainException)?.key
private fun validateLastName(v: String): com.bearkicks.app.core.errors.ErrorKey? =
    (LastName.create(v).exceptionOrNull() as? com.bearkicks.app.core.errors.DomainException)?.key
private fun validatePhone(v: String): com.bearkicks.app.core.errors.ErrorKey? =
    (Phone.create(v).exceptionOrNull() as? com.bearkicks.app.core.errors.DomainException)?.key
private fun validateAddress(v: String): com.bearkicks.app.core.errors.ErrorKey? =
    (Address.create(v).exceptionOrNull() as? com.bearkicks.app.core.errors.DomainException)?.key
private fun validatePassword(v: String): com.bearkicks.app.core.errors.ErrorKey? =
    (Password.create(v).exceptionOrNull() as? com.bearkicks.app.core.errors.DomainException)?.key

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    result: Result<Unit>?,
    onSubmit: (String, String, String) -> Unit,
    onVerifyCurrent: (String, (Boolean, com.bearkicks.app.core.errors.ErrorKey?) -> Unit) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val current = rememberSaveable { mutableStateOf("") }
    val newPass = rememberSaveable { mutableStateOf("") }
    val confirm = rememberSaveable { mutableStateOf("") }
    val newError = rememberSaveable { mutableStateOf<String?>(null) }
    val confirmError = rememberSaveable { mutableStateOf<String?>(null) }
    val currentError = rememberSaveable { mutableStateOf<com.bearkicks.app.core.errors.ErrorKey?>(null) }
    val currentValid = rememberSaveable { mutableStateOf(false) }
    val verifying = rememberSaveable { mutableStateOf(false) }
    val showCurrent = rememberSaveable { mutableStateOf(false) }
    val showNew = rememberSaveable { mutableStateOf(false) }
    val showConfirm = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var verifyJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    val statusMessage = result?.fold(
        onSuccess = { stringResource(id = com.bearkicks.app.R.string.password_updated) },
        onFailure = {
            val key = (it as? com.bearkicks.app.core.errors.DomainException)?.key
                ?: com.bearkicks.app.core.errors.ErrorKey.GENERIC_ERROR
            stringResource(id = com.bearkicks.app.ui.strings.errorTextRes(key))
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = com.bearkicks.app.R.string.profile_change_password_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = current.value,
                    onValueChange = {
                        current.value = it
                        currentError.value = null
                        currentValid.value = false
                        verifyJob?.cancel()
                        if (it.isNotBlank()) {
                            verifying.value = true
                            verifyJob = scope.launch {
                                delay(500)
                                onVerifyCurrent(it) { ok, error ->
                                    verifying.value = false
                                    currentValid.value = ok
                                    currentError.value = error
                                }
                            }
                        } else {
                            verifying.value = false
                        }
                    },
                    label = { Text(stringResource(id = com.bearkicks.app.R.string.field_current_password)) },
                    isError = currentError.value != null,
                    singleLine = true,
                    visualTransformation = if (showCurrent.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showCurrent.value = !showCurrent.value }) {
                            Icon(if (showCurrent.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = if (showCurrent.value) stringResource(id = com.bearkicks.app.R.string.common_hide) else stringResource(id = com.bearkicks.app.R.string.common_show))
                        }
                    }
                )
                when {
                    verifying.value -> Text(stringResource(id = com.bearkicks.app.R.string.password_verifying), style = MaterialTheme.typography.bodySmall)
                    currentValid.value && currentError.value == null -> Text(stringResource(id = com.bearkicks.app.R.string.password_current_ok), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                    currentError.value != null -> Text(stringResource(id = com.bearkicks.app.ui.strings.errorTextRes(currentError.value!!)), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = newPass.value,
                    onValueChange = {
                        newPass.value = it
                        newError.value = validatePassword(it)?.let { key -> context.getString(com.bearkicks.app.ui.strings.errorTextRes(key)) }
                        if (currentValid.value && it == current.value) {
                            newError.value = context.getString(com.bearkicks.app.R.string.password_new_same_as_current)
                        }
                        if (confirm.value.isNotEmpty()) confirmError.value = if (confirm.value == it) null else context.getString(com.bearkicks.app.R.string.password_confirm_mismatch)
                    },
                    isError = newError.value != null,
                    label = { Text(stringResource(id = com.bearkicks.app.R.string.field_new_password)) },
                    singleLine = true,
                    visualTransformation = if (showNew.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showNew.value = !showNew.value }) {
                            Icon(if (showNew.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = if (showNew.value) stringResource(id = com.bearkicks.app.R.string.common_hide) else stringResource(id = com.bearkicks.app.R.string.common_show))
                        }
                    }
                )
                newError.value?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                OutlinedTextField(
                    value = confirm.value,
                    onValueChange = {
                        confirm.value = it
                        confirmError.value = if (it == newPass.value) null else context.getString(com.bearkicks.app.R.string.password_confirm_mismatch)
                    },
                    isError = confirmError.value != null,
                    label = { Text(stringResource(id = com.bearkicks.app.R.string.field_confirm_password)) },
                    singleLine = true,
                    visualTransformation = if (showConfirm.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirm.value = !showConfirm.value }) {
                            Icon(if (showConfirm.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = if (showConfirm.value) stringResource(id = com.bearkicks.app.R.string.common_hide) else stringResource(id = com.bearkicks.app.R.string.common_show))
                        }
                    }
                )
                confirmError.value?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                statusMessage?.let { Text(it, color = if (result?.isSuccess == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            val disabled = listOf(newError.value, confirmError.value, currentError.value).any { it != null } || current.value.isBlank() || newPass.value.isBlank() || confirm.value.isBlank() || !currentValid.value || verifying.value
            TextButton(onClick = { if (!disabled) onSubmit(current.value, newPass.value, confirm.value) }, enabled = !disabled) { Text(stringResource(id = com.bearkicks.app.R.string.common_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(id = com.bearkicks.app.R.string.common_cancel)) } }
    )
}

@Composable
private fun ActionItem(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, danger: Boolean = false, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (pressed) 0.96f else 1f, animationSpec = tween(180, easing = FastOutSlowInEasing), label = "scale")
    TextButton(
        onClick = onClick,
        interactionSource = interaction,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .scale(scale)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = if (danger) BKBrandAccent else BKBrandPrimary)
            Spacer(Modifier.width(12.dp))
            Text(text, color = if (danger) BKBrandAccent else BKBrandPrimary, modifier = Modifier.weight(1f))
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = BKBrandPrimary)
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    AnimatedVisibility(visible = true, enter = fadeIn(animationSpec = tween(300)), exit = fadeOut()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = BKBrandPrimary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = BKBrandPrimaryLight)
                Text(value, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, color = BKBrandPrimary)
            }
        }
    }
}