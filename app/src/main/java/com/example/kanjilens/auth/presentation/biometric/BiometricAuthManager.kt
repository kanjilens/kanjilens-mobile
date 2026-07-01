package com.example.kanjilens.auth.presentation.biometric

import android.content.Context
import androidx.biometric.AuthenticationRequest
import androidx.biometric.BiometricManager

class BiometricAuthManager(
    private val context: Context
) {

    fun isBiometricAvailable(): Boolean {
        return biometricStatus() == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun biometricStatus(): Int {
        return BiometricManager.from(context).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        )
    }

    fun createAuthenticationRequest(): AuthenticationRequest.Biometric {
        return AuthenticationRequest.biometricRequest(
            title = "Entrar com biometria",
            AuthenticationRequest.Biometric.Fallback.DeviceCredential
        ) {
            setSubtitle("Confirme sua identidade para acessar o Kanji Lens")
            setMinStrength(AuthenticationRequest.Biometric.Strength.Class3())
            setIsConfirmationRequired(false)
        }
    }
}