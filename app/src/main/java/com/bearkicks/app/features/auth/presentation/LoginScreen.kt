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

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onGoRegister: () -> Unit
) {
    val viewModel: LoginViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsState()

    val emailState = remember { mutableStateOf("") }
    val passState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        BKTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = "Email",
            size = BKTextFieldSize.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        BKTextField(
            value = passState.value,
            onValueChange = { passState.value = it },
            label = "Contraseña",
            isPassword = true,
            size = BKTextFieldSize.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        when (uiState) {
            is LoginUiState.Loading -> CircularProgressIndicator()
            is LoginUiState.Error -> Text((uiState as LoginUiState.Error).message, color = MaterialTheme.colorScheme.error)
            is LoginUiState.Success -> onLoggedIn()
            else -> {}
        }

        Spacer(Modifier.height(16.dp))

        BKButton(text = "Entrar", modifier = Modifier.fillMaxWidth()) {
            viewModel.onLogin(emailState.value, passState.value)
        }
        Spacer(Modifier.height(8.dp))
        BKButton(text = "Crear cuenta", modifier = Modifier.fillMaxWidth()) { onGoRegister() }
    }
}
