package com.corestack.khidmatai.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corestack.khidmatai.domain.model.AuthState
import com.corestack.khidmatai.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val s = LocalAppStrings.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf(viewModel.lastEmail) }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state.authState) {
        if (state.authState is AuthState.Success) {
            onLoginSuccess()
            viewModel.onAction(AuthIntent.Reset)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars),
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(MaterialTheme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back",
                style = AppTypography.displayLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Text(
                text = "Login to continue",
                style = AppTypography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            if (state.authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                Text(
                    text = (state.authState as AuthState.Error).message,
                    color = Error,
                    style = AppTypography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Button(
                onClick = { viewModel.onAction(AuthIntent.Login(email, password)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = state.authState !is AuthState.Loading
            ) {
                if (state.authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Surface,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login", color = Surface, style = AppTypography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Don't have an account? ",
                    style = AppTypography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "Register",
                    style = AppTypography.labelMedium,
                    color = Primary,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}
