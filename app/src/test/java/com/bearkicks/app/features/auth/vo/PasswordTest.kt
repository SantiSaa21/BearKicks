package com.bearkicks.app.features.auth.vo

import com.bearkicks.app.features.auth.domain.model.vo.Password
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class PasswordTest {
    @Test
    fun validPassword() {
        assertTrue(Password.create("abc12345").isSuccess)
    }
    @Test
    fun invalidPassword() {
        assertFalse(Password.create("short").isSuccess)
        assertFalse(Password.create("allletters").isSuccess)
    }
}
