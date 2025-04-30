package com.example.ingrediscan.BackEnd
import com.example.ingrediscan.model.Email
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/*
*   Unit Tests for the Email class
*
 */

class EmailTest {

    @Test
    fun `valid email does not throw exception`() {
        Email("test@example.com")
    }

    @Test
    fun `email with no '@' symbol throws exception`(){
        assertThrows(IllegalArgumentException::class.java) {
            Email("testexample.com")
        }
    }

    @Test
    fun `email with no domain throws exception`(){
        assertThrows(IllegalArgumentException::class.java) {
            Email("test@")
        }
    }

    @Test
    fun `email with no username throws exception`(){
        assertThrows(IllegalArgumentException::class.java) {
            Email("@example.com")
        }
    }

    @Test
    fun `email with multiple sysmbols throws exception`(){
        assertThrows(IllegalArgumentException::class.java) {
            Email("test@example@com")
        }
    }

    @Test
    fun `email with invalid characters throws exception`(){
        assertThrows(IllegalArgumentException::class.java) {
            Email("test!#\$%^&*()@example.com")
        }
    }

    @Test
    fun `email with no top-level domain throws exception`(){
        assertThrows(IllegalArgumentException::class.java) {
            Email("test@example")
        }
    }

    @Test
    fun `email with numeric domain`(){
        Email("test@123.com")
    }

    @Test
    fun `email with subdomain`(){
        Email("test@subdomain.example.com")
    }

    @Test
    fun `email with long top-level domain`(){
        Email("test@example.organizationaldomain.com")
    }

    @Test
    fun `email with hyphen in username`() {
        Email("test-user@example.com")
    }

    @Test
    fun `email toString returns original value`() {
        val email = Email("test@example.com")
        assertEquals("test@example.com", email.toString())
    }

    @Test
    fun `empty string throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            Email("")
        }
    }
}