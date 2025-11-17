package com.bearkicks.app.features.cart.presentation

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bearkicks.app.features.cart.domain.model.CartItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CheckoutBottomSheetTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    @Test
    fun card_button_disabled_until_all_fields_valid() {
        val items = listOf(
            CartItem(
                id = "1",
                shoeId = "shoe1",
                name = "Test",
                brand = "MarcaX",
                price = 100.0,
                imageUrl = null,
                size = 42,
                qty = 1
            )
        )
        composeRule.setContent {
            CheckoutBottomSheet(
                items = items,
                onDismiss = {},
                onPayCard = { _,_,_,_ -> },
                onPayQr = { _,_ -> }
            )
        }
        // Ir a pestaña tarjeta si no está
        composeRule.onNodeWithText("Tarjeta").performClick()
        // Botón inicialmente deshabilitado
        composeRule.onNodeWithText("Pagar BOB 100.00").assertIsNotEnabled()
        // Llenar los campos
        composeRule.onNodeWithText("Número de tarjeta").performTextInput("4111111111111111")
        // Seleccionar mes y año
        composeRule.onNodeWithText("Mes").performClick()
        composeRule.onNodeWithText("1").performClick()
        composeRule.onNodeWithText("Año").performClick()
        // Seleccionar cualquier año que contenga "20" (primer match)
        composeRule.onNode(hasText("20", substring = true)).performClick()
        composeRule.onNodeWithText("CVV").performTextInput("123")
        composeRule.onNodeWithText("Titular").performTextInput("Juan Perez")
        // Ahora debería habilitarse
        composeRule.onNodeWithText("Pagar BOB 100.00").assertIsEnabled()
    }
}
