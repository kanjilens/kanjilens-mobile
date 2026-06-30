package com.example.kanjilens.auth.domain
import com.example.kanjilens.auth.domain.AuthError

object PasswordValidator {

    fun validate(password: String): AuthError? {

        if (password.length < 8)
            return AuthError.PasswordTooShort

        if (!password.any { it.isUpperCase() })
            return AuthError.PasswordMissingUppercase

        if (!password.any { it.isDigit() })
            return AuthError.PasswordMissingNumber

        if (!password.any { !it.isLetterOrDigit() })
            return AuthError.PasswordMissingSpecialCharacter

        return null
    }
}