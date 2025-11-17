package com.bearkicks.app.navigation

sealed class Screen(val route: String, val title: String) {
    object Shop       : Screen("shop",       "Tienda")
    object Wishlist   : Screen("wishlist",   "Favoritos")
    object Home       : Screen("home",       "BearKicks")
    object Cart       : Screen("cart",       "Carrito")
    object Profile    : Screen("profile",    "Perfil")
    object Login      : Screen("login",      "Login")
    object Register   : Screen("register",   "Registro")
    object ShoeDetail : Screen("shoe_detail","Detalle")
    object Orders     : Screen("orders",     "Pedidos")
}
val BottomBarOrder = listOf(
    Screen.Shop.route,
    Screen.Wishlist.route,
    Screen.Home.route,
    Screen.Cart.route,
    Screen.Profile.route
)
