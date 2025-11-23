package com.bearkicks.application.navigation

import androidx.annotation.StringRes
import com.bearkicks.application.R

sealed class Screen(val route: String, @StringRes val titleRes: Int) {
    object Shop       : Screen("shop",        R.string.nav_shop)
    object Wishlist   : Screen("wishlist",    R.string.nav_wishlist)
    object Home       : Screen("home",        R.string.nav_home)
    object Cart       : Screen("cart",        R.string.nav_cart)
    object Profile    : Screen("profile",     R.string.nav_profile)
    object Login      : Screen("login",       R.string.auth_login_title)
    object Register   : Screen("register",    R.string.register_title)
    object ShoeDetail : Screen("shoe_detail", R.string.detail_title)
    object Orders     : Screen("orders",      R.string.orders_title)

    companion object {
        val all = listOf(Shop, Wishlist, Home, Cart, Profile, Login, Register, ShoeDetail, Orders)
        // Devuelve la Screen correspondiente incluso si la ruta incluye parámetros (ej: "shoe_detail/123")
        // Maneja rutas nulas o vacías devolviendo null en lugar de provocar NPE.
        fun fromRoute(route: String?): Screen? {
            if (route.isNullOrBlank()) return null
            val base = route.substringBefore('/') // Soporta rutas con argumentos
            return all.firstOrNull { it?.route == base }
        }
    }
}

val BottomBarOrder = listOf(
    Screen.Shop.route,
    Screen.Wishlist.route,
    Screen.Home.route,
    Screen.Cart.route,
    Screen.Profile.route
)
