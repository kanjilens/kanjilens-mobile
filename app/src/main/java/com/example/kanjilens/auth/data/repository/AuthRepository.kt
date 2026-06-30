package com.example.kanjilens.auth.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.example.kanjilens.auth.domain.AuthError
import com.google.firebase.auth.FirebaseAuthException
import com.example.kanjilens.auth.data.mapper.FirebaseAuthErrorMapper



class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (AuthError) -> Unit,
    ) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                if (exception is FirebaseAuthException) {
                    onError(FirebaseAuthErrorMapper.map(exception.errorCode))
                } else {
                    onError(AuthError.Unknown)
                }
            }
    }

    fun createAccount(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (AuthError) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val user = result.user ?: run {
                    onError(AuthError.Unknown)
                    return@addOnSuccessListener
                }

                val profile = UserProfileChangeRequest.Builder()
                    .setDisplayName(name.trim())
                    .build()

                user.updateProfile(profile)
                    .addOnCompleteListener {
                        firestore.collection("usuarios")
                            .document(user.uid)
                            .set(
                                mapOf(
                                    "uid" to user.uid,
                                    "name" to name.trim(),
                                    "email" to email.trim(),
                                    "createdAt" to FieldValue.serverTimestamp(),
                                )
                            )
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { exception ->
                                if (exception is FirebaseAuthException) {
                                    onError(FirebaseAuthErrorMapper.map(exception.errorCode))
                                } else {
                                    onError(AuthError.Unknown)
                                }
                            }
                    }
            }

    }

    fun sendVerificationEmail(onDone: () -> Unit) {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { onDone() }
            ?: onDone()
    }

    fun signOut() {
        try {
            auth.signOut()
            Log.d("AuthRepository", "Logout ok")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro ao deslogar: ${e.message}")
        }
    }
    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (AuthError) -> Unit
    ) {
        auth.sendPasswordResetEmail(email.trim())
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                if (exception is FirebaseAuthException) {
                    onError(FirebaseAuthErrorMapper.map(exception.errorCode))
                } else {
                    onError(AuthError.Unknown)
                }
            }
    }
}
