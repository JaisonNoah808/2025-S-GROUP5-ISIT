package com.example.ingrediscan.BackEnd
import com.example.ingrediscan.model.Password
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/*
*   Unit Tests for the 'Password' class in 'Password.kt'
*
 */


class PasswordTest {

    @Test
    fun `valid password does not throw exception`(){
        Password("Password123!")
    }

    @Test
    fun `password too short throws exception`(){
        assertThrows(IllegalArgumentException::class.java){
            Password("Pass1!")
        }
    }

    @Test
    fun `password too long throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            Password("ThisPasswordisWayTooLong123!")
        }
    }

    @Test
    fun `password missing uppercase throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            Password("password123!")
        }
    }

    @Test
    fun `password missing lowercase throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            Password("PASSWORD123!")
        }
    }

    @Test
    fun `password missing digit throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            Password("Password!")
        }
    }

    @Test
    fun `password missing letter throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            Password("1234567890!")
        }
    }

    @Test
    fun `password missing special character throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            Password("Password123")
        }
    }

    @Test
    fun `password with minimum length is valid`() {
        Password("Pass123!")
    }

    @Test
    fun `password with maximum length is valid`() {
        Password("Password1234!")
    }
}