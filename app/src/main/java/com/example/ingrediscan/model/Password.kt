package com.example.ingrediscan.model

/**
 * Represents a user's password.
 *
 * @property value The password value.
 *
 * @throws IllegalArgumentException if the password length is less than 8 characters.
 */
class Password(private val value: String) {
    init {
        validate (value)
    }

    companion object {
        const val MIN_LENGTH = 8
        const val MAX_LENGTH = 16
        val specialChars = "!@#$%^&*()_+{}[];'<>?,./-"

        fun validate(password: String){
            require (password.length in MIN_LENGTH..MAX_LENGTH) {
                "Password must be between $MIN_LENGTH and $MAX_LENGTH characters long."
            }
            require(password.any { it.isUpperCase() }) {
                "Password must contain at least one uppercase letter."
            }
            require(password.any { it.isLowerCase() }) {
                "Password must contain at least one lowercase letter."
            }
            require(password.any { it.isDigit() }) {
                "Password must contain at least one digit."
            }
            require(password.any { it.isLetter() }) {
                "Password must contain at least one letter."
            }
            require(password.any { it in specialChars }) {
                "Password must contain at least one special character: $specialChars."
            }
        }
    }

    override fun toString(): String {
        return value
    }
}