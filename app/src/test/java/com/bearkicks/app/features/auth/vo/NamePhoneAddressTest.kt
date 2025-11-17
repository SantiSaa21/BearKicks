package com.bearkicks.app.features.auth.vo

import com.bearkicks.app.features.auth.domain.model.vo.Name
import com.bearkicks.app.features.auth.domain.model.vo.LastName
import com.bearkicks.app.features.auth.domain.model.vo.Phone
import com.bearkicks.app.features.auth.domain.model.vo.Address
import org.junit.Assert.*
import org.junit.Test

class NamePhoneAddressTest {
    @Test fun nameValid() { assertTrue(Name.create("Juan").isSuccess) }
    @Test fun nameInvalid() { assertFalse(Name.create("J").isSuccess) }
    @Test fun lastNameValid() { assertTrue(LastName.create("Perez").isSuccess) }
    @Test fun lastNameInvalid() { assertFalse(LastName.create("P").isSuccess) }
    @Test fun phoneValid() { assertTrue(Phone.create("71234567").isSuccess) }
    @Test fun phoneInvalid() { assertFalse(Phone.create("123").isSuccess) }
    @Test fun addressValid() { assertTrue(Address.create("Calle 1 #123 Cochabamba").isSuccess) }
    @Test fun addressInvalid() { assertFalse(Address.create("@").isSuccess) }
}
