package com.bearkicks.app.features.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bearkicks.app.ui.components.BKButton
import com.bearkicks.app.ui.components.BKTextField
import com.bearkicks.app.ui.components.BKTextFieldSize
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import com.bearkicks.app.features.auth.domain.model.vo.Name
import com.bearkicks.app.features.auth.domain.model.vo.LastName
import com.bearkicks.app.features.auth.domain.model.vo.Username
import com.bearkicks.app.features.auth.domain.model.vo.Email
import com.bearkicks.app.features.auth.domain.model.vo.Phone
import com.bearkicks.app.features.auth.domain.model.vo.Address
import com.bearkicks.app.features.auth.domain.model.vo.Password
import androidx.compose.ui.res.stringResource
import com.bearkicks.app.R

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val viewModel: RegisterViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsState()

    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
        // Errores por campo
        val firstNameError = remember { mutableStateOf<String?>(null) }
        val lastNameError = remember { mutableStateOf<String?>(null) }
        val usernameError = remember { mutableStateOf<String?>(null) }
        val emailError = remember { mutableStateOf<String?>(null) }
        val phoneError = remember { mutableStateOf<String?>(null) }
        val addressError = remember { mutableStateOf<String?>(null) }
        val passwordError = remember { mutableStateOf<String?>(null) }
    val birthDateEpoch = remember { mutableStateOf<Long?>(null) }
    val birthDateError = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(id = R.string.register_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        BKTextField(
            value = firstName.value,
            onValueChange = {
                firstName.value = it
                firstNameError.value = if (it.isBlank()) context.getString(R.string.required_first_name) else Name.create(it).exceptionOrNull()?.message
            },
            label = stringResource(id = R.string.field_first_name), size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth()
        )
        firstNameError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        BKTextField(
            value = lastName.value,
            onValueChange = {
                lastName.value = it
                lastNameError.value = if (it.isBlank()) context.getString(R.string.required_last_name) else LastName.create(it).exceptionOrNull()?.message
            },
            label = stringResource(id = R.string.field_last_name), size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth()
        )
        lastNameError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        BKTextField(
            value = username.value,
            onValueChange = {
                username.value = it
                usernameError.value = if (it.isBlank()) context.getString(R.string.required_username) else Username.create(it).exceptionOrNull()?.message
            },
            label = stringResource(id = R.string.field_username), size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth()
        )
        usernameError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        BKTextField(
            value = email.value,
            onValueChange = {
                email.value = it
                emailError.value = if (it.isBlank()) context.getString(R.string.required_email) else Email.create(it).exceptionOrNull()?.message
            },
            label = stringResource(id = R.string.field_email), size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth()
        )
        emailError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        BKTextField(
            value = phone.value,
            onValueChange = {
                phone.value = it
                phoneError.value = if (it.isBlank()) context.getString(R.string.required_phone) else Phone.create(it).exceptionOrNull()?.message
            },
            label = stringResource(id = R.string.field_phone), size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth()
        )
        phoneError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        BKTextField(
            value = address.value,
            onValueChange = {
                address.value = it
                addressError.value = if (it.isBlank()) context.getString(R.string.required_address) else Address.create(it).exceptionOrNull()?.message
            },
            label = stringResource(id = R.string.field_address), size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth()
        )
        addressError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = {
            val cal = Calendar.getInstance()
            val max = Calendar.getInstance().apply { add(Calendar.YEAR, -18) }
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val picked = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    birthDateEpoch.value = picked.timeInMillis
                    birthDateError.value = null
                },
                max.get(Calendar.YEAR),
                max.get(Calendar.MONTH),
                max.get(Calendar.DAY_OF_MONTH)
            ).apply { datePicker.maxDate = max.timeInMillis }.show()
        }, modifier = Modifier.fillMaxWidth()) {
            val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
            Text(birthDateEpoch.value?.let { stringResource(id = R.string.birthdate_selected, sdf.format(it)) } ?: stringResource(id = R.string.birthdate_select))
        }
        birthDateError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        BKTextField(
            value = password.value,
            onValueChange = {
                password.value = it
                passwordError.value = if (it.isBlank()) context.getString(R.string.required_password) else Password.create(it).exceptionOrNull()?.message
            },
            label = stringResource(id = R.string.field_password), isPassword = true, size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth()
        )
        passwordError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(16.dp))
        when (uiState) {
            is RegisterUiState.Loading -> CircularProgressIndicator()
            is RegisterUiState.Error -> Text((uiState as RegisterUiState.Error).message, color = MaterialTheme.colorScheme.error)
            is RegisterUiState.Success -> onRegistered()
            else -> {}
        }
        Spacer(Modifier.height(16.dp))
        BKButton(text = stringResource(id = R.string.register_action), modifier = Modifier.fillMaxWidth()) {
            // Ejecutar validaciones finales usando VO
            firstNameError.value = Name.create(firstName.value).exceptionOrNull()?.message
            lastNameError.value = LastName.create(lastName.value).exceptionOrNull()?.message
            usernameError.value = Username.create(username.value).exceptionOrNull()?.message
            emailError.value = Email.create(email.value).exceptionOrNull()?.message
            phoneError.value = Phone.create(phone.value).exceptionOrNull()?.message
            addressError.value = Address.create(address.value).exceptionOrNull()?.message
            passwordError.value = Password.create(password.value).exceptionOrNull()?.message
            val isAdult = birthDateEpoch.value?.let { it <= System.currentTimeMillis() - 567648000000 } ?: false
            birthDateError.value = if (!isAdult) context.getString(R.string.invalid_birthdate) else null

            val allOk = listOf(
                firstNameError.value,
                lastNameError.value,
                usernameError.value,
                emailError.value,
                phoneError.value,
                addressError.value,
                passwordError.value,
                birthDateError.value
            ).all { it == null } && birthDateEpoch.value != null

            if (allOk) {
                viewModel.onRegister(
                    firstName.value,
                    lastName.value,
                    username.value,
                    email.value,
                    phone.value,
                    address.value,
                    birthDateEpoch.value!!,
                    password.value,
                    null
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        BKButton(text = stringResource(id = R.string.back_to_login), modifier = Modifier.fillMaxWidth()) { onBackToLogin() }
    }
}
