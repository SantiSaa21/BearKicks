package com.bearkicks.app.features.auth.vo

import com.bearkicks.app.features.auth.domain.model.vo.Email
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class EmailTest {
    @Test
    fun validEmail() {
        val res = Email.create("user@example.com")
        assertTrue(res.isSuccess)
    }
    @Test
    fun invalidEmail() {
        val res = Email.create("bad-email")
        assertFalse(res.isSuccess)
    }
}
