package com.bearkicks.app.di

import androidx.room.Room
import com.bearkicks.app.features.auth.data.datastore.AuthDataStore
import com.bearkicks.app.features.auth.data.datasource.AuthFirebaseDataSource
import com.bearkicks.app.features.auth.data.repository.AuthRepository
import com.bearkicks.app.features.auth.domain.repository.IAuthRepository
import com.bearkicks.app.features.auth.domain.usecase.GetCurrentUserUseCase
import com.bearkicks.app.features.auth.domain.usecase.LoginUseCase
import com.bearkicks.app.features.auth.domain.usecase.LogoutUseCase
import com.bearkicks.app.features.auth.domain.usecase.ObserveAuthStateUseCase
import com.bearkicks.app.features.auth.domain.usecase.RegisterUseCase
import com.bearkicks.app.features.auth.presentation.LoginViewModel
import com.bearkicks.app.features.auth.presentation.RegisterViewModel
import com.bearkicks.app.features.profile.presentation.ProfileViewModel
import com.bearkicks.app.features.home.data.datasource.HomeRemoteDataSource
import com.bearkicks.app.features.home.data.repository.HomeRepository
import com.bearkicks.app.features.home.domain.repository.IHomeRepository
import com.bearkicks.app.features.home.domain.usecase.GetNewShoesUseCase
import com.bearkicks.app.features.home.domain.usecase.GetOfferShoesUseCase
import com.bearkicks.app.features.home.presentation.HomeViewModel
import com.bearkicks.app.features.shop.data.datasource.ShopRemoteDataSource
import com.bearkicks.app.features.shop.data.repository.ShopRepository
import com.bearkicks.app.features.shop.domain.repository.IShopRepository
import com.bearkicks.app.features.shop.domain.usecase.GetAllShoesUseCase
import com.bearkicks.app.features.shop.domain.usecase.GetShoeDetailUseCase
import com.bearkicks.app.features.shop.presentation.ShoeDetailViewModel
import com.bearkicks.app.features.shop.presentation.ShopViewModel
import com.bearkicks.app.features.wishlist.data.database.WishListRoomDataBase
import com.bearkicks.app.features.wishlist.data.database.MIGRATION_1_2
import com.bearkicks.app.features.wishlist.data.database.MIGRATION_2_3
import com.bearkicks.app.features.wishlist.data.database.MIGRATION_3_4
import com.bearkicks.app.features.wishlist.data.datasource.FavoritesRemoteDataSource
import com.bearkicks.app.features.wishlist.data.datasource.WishListLocalDataSource
import com.bearkicks.app.features.wishlist.data.repository.WishListRepository
import com.bearkicks.app.features.wishlist.domain.repository.IWishListRepository
import com.bearkicks.app.features.wishlist.domain.usecase.ObserveWishListUseCase
import com.bearkicks.app.features.wishlist.domain.usecase.ToggleWishListUseCase
import com.bearkicks.app.features.wishlist.presentation.WishListViewModel
import com.bearkicks.app.features.cart.data.CartRepository
import com.bearkicks.app.features.cart.data.ICartRepository
import com.bearkicks.app.features.cart.domain.usecase.AddToCartUseCase
import com.bearkicks.app.features.cart.domain.usecase.ClearCartUseCase
import com.bearkicks.app.features.cart.domain.usecase.ObserveCartUseCase
import com.bearkicks.app.features.cart.domain.usecase.PlaceOrderUseCase
import com.bearkicks.app.features.cart.domain.usecase.CheckoutWithCardUseCase
import com.bearkicks.app.features.cart.domain.usecase.CheckoutWithQrUseCase
import com.bearkicks.app.features.cart.domain.usecase.ObserveOrderPaymentUseCase
import com.bearkicks.app.features.cart.domain.usecase.DeleteOrderUseCase
import com.bearkicks.app.features.cart.domain.usecase.RemoveFromCartUseCase
import com.bearkicks.app.features.cart.domain.usecase.ObserveOrdersUseCase
import com.bearkicks.app.features.cart.domain.usecase.ObserveOrderItemsUseCase
import com.bearkicks.app.features.cart.domain.usecase.ObserveIsInCartUseCase
import com.bearkicks.app.features.cart.domain.usecase.ClearOrdersUseCase
import com.bearkicks.app.features.cart.presentation.CartViewModel
import com.bearkicks.app.features.auth.domain.usecase.UpdateProfileUseCase
import com.bearkicks.app.features.auth.domain.usecase.UpdateProfilePhotoUseCase
import com.bearkicks.app.features.auth.domain.usecase.ChangeProfilePhotoUseCase
import com.bearkicks.app.features.auth.domain.usecase.ChangePasswordUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import com.google.firebase.storage.FirebaseStorage
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single<DatabaseReference>(named("dbRoot")) { FirebaseDatabase.getInstance().reference }
    single<DatabaseReference>(named("shoesRef")) { get<DatabaseReference>(named("dbRoot")).child("shoes") }
    single<DatabaseReference>(named("usersRef")) { get<DatabaseReference>(named("dbRoot")).child("users") }
    single<DatabaseReference>(named("usernamesRef")) { get<DatabaseReference>(named("dbRoot")).child("usernames") }
    single { FirebaseAuth.getInstance() }
    // Be explicit about the default bucket to avoid config drift
    single { FirebaseStorage.getInstance("gs://bearkicks-210304.appspot.com") }

    single { AuthDataStore(get()) }
    single { AuthFirebaseDataSource(get(), get(named("dbRoot")), get(named("usersRef")), get(named("usernamesRef"))) }
    single<IAuthRepository> { AuthRepository(get(), get()) }
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { ObserveAuthStateUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { UpdateProfileUseCase(get()) }
    factory { UpdateProfilePhotoUseCase(get()) }
    factory { ChangeProfilePhotoUseCase(get(), get(), get()) }
    factory { ChangePasswordUseCase(get()) }

    single { HomeRemoteDataSource(get(named("shoesRef"))) }
    single<IHomeRepository> { HomeRepository(get()) }
    factory { GetOfferShoesUseCase(get()) }
    factory { GetNewShoesUseCase(get()) }

    single { ShopRemoteDataSource(get(named("shoesRef"))) }
    single<IShopRepository> { ShopRepository(get()) }
    factory { GetAllShoesUseCase(get()) }
    factory { GetShoeDetailUseCase(get()) }

    single {
        Room.databaseBuilder(get(), WishListRoomDataBase::class.java, "wishlist.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()
    }
    single { get<WishListRoomDataBase>().favoriteDao() }
    single { get<WishListRoomDataBase>().cartDao() }
    single { get<WishListRoomDataBase>().ordersDao() }
    single { WishListLocalDataSource(get()) }
    single {
        val provider: () -> String = { FirebaseAuth.getInstance().currentUser?.uid ?: "guest" }
        FavoritesRemoteDataSource(get(named("dbRoot")), provider)
    }
    single<IWishListRepository> { WishListRepository(get(), get()) }
    factory { ObserveWishListUseCase(get()) }
    factory { ToggleWishListUseCase(get()) }

    single<ICartRepository> { CartRepository(get(), get()) }
    factory { ObserveCartUseCase(get()) { FirebaseAuth.getInstance().currentUser?.uid ?: "guest" } }
    factory { AddToCartUseCase(get()) { FirebaseAuth.getInstance().currentUser?.uid ?: "guest" } }
    factory { RemoveFromCartUseCase(get()) }
    factory { ClearCartUseCase(get()) { FirebaseAuth.getInstance().currentUser?.uid ?: "guest" } }
    factory { PlaceOrderUseCase(get()) { FirebaseAuth.getInstance().currentUser?.uid ?: "guest" } }
    factory { CheckoutWithCardUseCase(get()) { FirebaseAuth.getInstance().currentUser?.uid ?: "guest" } }
    factory { CheckoutWithQrUseCase(get()) { FirebaseAuth.getInstance().currentUser?.uid ?: "guest" } }
    factory { ObserveOrderPaymentUseCase(get()) }
    factory { DeleteOrderUseCase(get()) }
    factory { ObserveOrdersUseCase(get()) { FirebaseAuth.getInstance().currentUser?.uid ?: "guest" } }
    factory { ObserveOrderItemsUseCase(get()) }
    factory { ObserveIsInCartUseCase(get()) { FirebaseAuth.getInstance().currentUser?.uid ?: "guest" } }
    factory { ClearOrdersUseCase(get()) { FirebaseAuth.getInstance().currentUser?.uid ?: "guest" } }

    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { ShopViewModel(get(), get(), get()) }
    viewModel { WishListViewModel(get(), get()) }
    viewModel { CartViewModel(get(), get(), get(), get(), get()) }
    viewModel { (id: String) -> ShoeDetailViewModel(id, get(), get(), get(), get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}
