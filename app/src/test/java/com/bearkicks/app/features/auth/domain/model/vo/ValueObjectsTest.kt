package com.bearkicks.app.features.auth.domain.model.vo

import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit
import com.bearkicks.app.core.errors.DomainException
import com.bearkicks.app.core.errors.ErrorKey

class ValueObjectsTest {

    @Test fun name_valid() {
        assertTrue(Name.create("Carlos").isSuccess)
    }
    @Test fun name_invalid_short() {
        val r = Name.create("C")
        assertTrue(r.isFailure)
        val ex = r.exceptionOrNull() as DomainException
        assertEquals(ErrorKey.INVALID_FIRST_NAME_RULES, ex.key)
    }
    @Test fun name_invalid_space() { assertTrue(Name.create("Juan Perez").isFailure) }

    @Test fun lastName_valid_compuesto() {
        assertTrue(LastName.create("Perez Garcia").isSuccess)
    }
    @Test fun lastName_invalid_part_short() { assertTrue(LastName.create("P Ga").isFailure) }

    @Test fun username_valid() { assertTrue(Username.create("carlos_01").isSuccess) }
    @Test fun username_invalid_chars() { assertTrue(Username.create("aa").isFailure) }

    @Test fun email_valid() { assertTrue(Email.create("test@example.com").isSuccess) }
    @Test fun email_invalid() { assertTrue(Email.create("bad@com").isFailure) }

    @Test fun phone_valid() { assertTrue(Phone.create("69503355").isSuccess) }
    @Test fun phone_invalid_start() { assertTrue(Phone.create("89503355").isFailure) }
    @Test fun phone_invalid_length() { assertTrue(Phone.create("6950335").isFailure) }

    @Test fun address_valid() { assertTrue(Address.create("Av America 123 cochabamba").isSuccess) }
    @Test fun address_missing_city() { assertTrue(Address.create("Av America 123 La Paz").isFailure) }

    @Test fun birthDate_valid_older_18() {
        val now = System.currentTimeMillis()
        val nineteenYearsMillis = 19L * 365L * 24L * 60L * 60L * 1000L
        assertTrue(BirthDate.create(now - nineteenYearsMillis, now).isSuccess)
    }
    @Test fun birthDate_invalid_under_18() {
        val now = System.currentTimeMillis()
        val fifteenYearsMillis = 15L * 365L * 24L * 60L * 60L * 1000L
        val r = BirthDate.create(now - fifteenYearsMillis, now)
        assertTrue(r.isFailure)
        val ex = r.exceptionOrNull() as DomainException
        assertEquals(ErrorKey.MUST_BE_ADULT, ex.key)
    }

    @Test fun password_valid() { assertTrue(Password.create("Abcdef12").isSuccess) }
    @Test fun password_invalid_missing_digit() { assertTrue(Password.create("Abcdefgh").isFailure) }
    @Test fun password_invalid_short() { assertTrue(Password.create("A1b2c").isFailure) }
}
