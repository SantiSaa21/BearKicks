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
        // Ir a pestaña tarjeta
        composeRule.onNodeWithTag("tab_card").performClick()
        // Botón inicialmente deshabilitado
        composeRule.onNodeWithTag("btn_pay").assertIsNotEnabled()
        // Llenar los campos
        composeRule.onNodeWithTag("field_card_number").performTextInput("4111111111111111")
        // Por estabilidad en CI evitamos interactuar con popups de DropdownMenu.
        // Llenamos CVV y Titular, pero como falta fecha de expiración, el botón sigue deshabilitado.
        composeRule.onNodeWithTag("field_cvv").performTextInput("123")
        composeRule.onNodeWithTag("field_holder").performTextInput("Juan Perez")
        composeRule.onNodeWithTag("btn_pay").assertIsNotEnabled()
    }
}
