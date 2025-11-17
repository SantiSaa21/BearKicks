package com.bearkicks.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bearkicks.app.features.home.presentation.HomeScreen
import com.bearkicks.app.features.auth.presentation.LoginScreen
import com.bearkicks.app.features.auth.presentation.RegisterScreen
import com.bearkicks.app.features.profile.presentation.ProfileScreen
import com.bearkicks.app.features.shop.presentation.ShoeDetailScreen
import com.bearkicks.app.features.shop.presentation.ShopScreen
import com.bearkicks.app.features.wishlist.presentation.WishListScreen
import com.bearkicks.app.features.cart.presentation.CartScreen
import com.bearkicks.app.features.cart.presentation.OrdersHistoryScreen
import com.bearkicks.app.ui.components.BKBottomBar
import com.bearkicks.app.ui.components.BKTopBar
import org.koin.compose.koinInject
import com.bearkicks.app.features.auth.domain.usecase.ObserveAuthStateUseCase
import androidx.compose.runtime.collectAsState
import com.google.firebase.auth.FirebaseAuth
import com.bearkicks.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val observeAuth: ObserveAuthStateUseCase = koinInject()
    val user by observeAuth().collectAsState(initial = null)
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    if (user == null) {
        if (firebaseUser != null) {
            LoadingScreen()
            return
        }
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screen.Login.route) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoggedIn = { },
                    onGoRegister = { navController.navigate(Screen.Register.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegistered = { },
                    onBackToLogin = { navController.popBackStack() }
                )
            }
        }
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Screen.Home.route

    val bottomRoutes = setOf(
        Screen.Shop.route, Screen.Wishlist.route, Screen.Home.route,
        Screen.Cart.route, Screen.Profile.route
    )
    val showBars = currentRoute in bottomRoutes

    Scaffold(
        topBar = {
            if (showBars) {
                val title = when (currentRoute) {
                    Screen.Shop.route -> Screen.Shop.title
                    Screen.Wishlist.route -> Screen.Wishlist.title
                    Screen.Home.route -> Screen.Home.title
                    Screen.Cart.route -> Screen.Cart.title
                    Screen.Profile.route -> Screen.Profile.title
                    else -> ""
                }
                BKTopBar(title = title)
            }
        },
        bottomBar = {
            if (showBars) {
                BKBottomBar(
                    currentRoute = currentRoute,
                    onItemSelected = { item ->
                        val route = item.route
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Shop.route) {
                ShopScreen(
                    onProductClick = { shoe ->
                        navController.navigate("${Screen.ShoeDetail.route}/${shoe.id}")
                    }
                )
            }
            composable("${Screen.ShoeDetail.route}/{shoeId}") { backStackEntry ->
                val shoeId = backStackEntry.arguments?.getString("shoeId") ?: return@composable
                ShoeDetailScreen(
                    shoeId = shoeId,
                    onBack = { navController.popBackStack() },
                    onAddToCartNavigate = {
                        navController.popBackStack()
                        navController.navigate(Screen.Cart.route)
                    },
                    isLoggedIn = true,
                    onRequireLogin = { }
                )
            }
            composable(Screen.Wishlist.route) {
                WishListScreen(
                    onProductClick = { shoe ->
                        navController.navigate("${Screen.ShoeDetail.route}/${shoe.id}")
                    }
                )
            }
            composable(Screen.Cart.route) {
                CartScreen { _ ->
                    navController.navigate(Screen.Profile.route)
                }
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLoggedOut = { },
                    onSeeAllOrders = { navController.navigate(Screen.Orders.route) }
                )
            }
            composable(Screen.Orders.route) { OrdersHistoryScreen(onBack = { navController.popBackStack() }) }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Image(painter = painterResource(id = R.drawable.logo_bearkicks), contentDescription = null, modifier = Modifier.size(120.dp))
            CircularProgressIndicator()
        }
    }
}
