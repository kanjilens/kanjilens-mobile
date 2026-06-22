package com.example.kanjilens.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kanjilens.R
import com.example.kanjilens.auth.presentation.viewmodel.AuthViewModel
import com.example.kanjilens.ui.theme.AppPrimary
import com.example.kanjilens.ui.theme.AppSecondary
import com.example.kanjilens.ui.theme.AppTextMuted

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
) {
    var isCreateAccount by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val errorFillEmailPassword = stringResource(R.string.fill_email_password)
    val errorFillName = stringResource(R.string.fill_name)
    val errorPasswordsMismatch = stringResource(R.string.passwords_do_not_match)

    LaunchedEffect(viewModel.isLoggedIn) {
        if (viewModel.isLoggedIn) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            BrandHeader()
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    AuthSegmentedControl(
                        isCreateAccount = isCreateAccount,
                        onSelectLogin = {
                            isCreateAccount = false
                            localError = null
                            viewModel.clearError()
                        },
                        onSelectCreateAccount = {
                            isCreateAccount = true
                            localError = null
                            viewModel.clearError()
                        }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isCreateAccount) stringResource(R.string.create_account) else stringResource(R.string.welcome_back),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isCreateAccount) stringResource(R.string.create_account_description) else stringResource(R.string.login_description),
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppTextMuted
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        if (isCreateAccount) {
                            LabeledField(
                                label = stringResource(R.string.name),
                                value = name,
                                onValueChange = { name = it },
                                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = AppSecondary) }
                            )
                        }

                        LabeledField(
                            label = stringResource(R.string.email),
                            value = email,
                            onValueChange = { email = it },
                            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = AppSecondary) }
                        )

                        PasswordField(
                            label = stringResource(R.string.password),
                            value = password,
                            onValueChange = { password = it },
                            passwordVisible = passwordVisible,
                            onToggleVisibility = { passwordVisible = !passwordVisible }
                        )

                        if (isCreateAccount) {
                            PasswordField(
                                label = stringResource(R.string.confirm_password),
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                passwordVisible = confirmPasswordVisible,
                                onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible }
                            )
                        }
                    }

                    (localError ?: viewModel.errorMessage)?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            localError = null
                            viewModel.clearError()

                            when {
                                email.isBlank() || password.isBlank() -> localError = errorFillEmailPassword
                                isCreateAccount && name.isBlank() -> localError = errorFillName
                                isCreateAccount && confirmPassword != password -> localError = errorPasswordsMismatch
                                isCreateAccount -> viewModel.createAccount(name, email, password)
                                else -> viewModel.signIn(email, password)
                            }
                        },
                        enabled = !viewModel.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppSecondary)
                    ) {
                        Text(
                            text = if (viewModel.isLoading) {
                                if (isCreateAccount) stringResource(R.string.creating_account) else stringResource(R.string.logging_in)
                            } else {
                                if (isCreateAccount) stringResource(R.string.create_account_and_login) else stringResource(R.string.login_kanji_lens)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AppSecondary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "漢", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "漢字レンズ",
                style = MaterialTheme.typography.titleLarge,
                color = AppPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.bodyMedium,
                color = AppTextMuted
            )
        }
    }
}

@Composable
private fun AuthSegmentedControl(
    isCreateAccount: Boolean,
    onSelectLogin: () -> Unit,
    onSelectCreateAccount: () -> Unit,
) {
    BoxWithConstraints {
        val optionWidth = (maxWidth - 14.dp) / 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFF1F6F4))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AuthSegmentOption(
                label = stringResource(R.string.login),
                selected = !isCreateAccount,
                onClick = onSelectLogin,
                modifier = Modifier.width(optionWidth)
            )
            AuthSegmentOption(
                label = stringResource(R.string.create_account_button),
                selected = isCreateAccount,
                onClick = onSelectCreateAccount,
                modifier = Modifier.width(optionWidth)
            )
        }
    }
}

@Composable
private fun AuthSegmentOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) AppPrimary else AppTextMuted,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = leadingIcon,
            shape = RoundedCornerShape(14.dp)
        )
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onToggleVisibility: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = AppSecondary) },
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = stringResource(R.string.show_hide_password),
                        tint = AppTextMuted
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp)
        )
    }
}