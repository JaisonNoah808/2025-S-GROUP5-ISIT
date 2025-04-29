package com.example.ingrediscan.ui.createAccount

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class CreateAccountViewModel : ViewModel() {
    // TODO: Use "username" somewhere in the project
    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> get() = _currentUser

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "CreateAccountViewModel"
    }

    /**
     * Create a new user account with the provided email and password.
     * This method uses Firebase's createUserWithEmailAndPassword method to create a new user account.
     *
     * @Source https://firebase.google.com/docs/auth/android/password-auth
     * @param email The user's email address.
     * @param password The user's password.
     * @return A LiveData object representing the result of the sign-up operation.
     */
    fun createAccount(email: String, password: String) {
        _username.value = email
        _password.value = password

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    _currentUser.value = user
                    _errorMessage.value = null
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    _errorMessage.value = task.exception?.message ?: "Sign-up failed"
                    _currentUser.value = null
                }
            }
    }
}