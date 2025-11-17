package com.bearkicks.app.features.wishlist.data

import com.google.firebase.database.DatabaseReference

class FavoritesRemoteDataSource(private val shoesRef: DatabaseReference) {
    fun setFavorite(id: String, liked: Boolean) {
        val node = shoesRef.child(id)
        node.child("isLiked").setValue(liked)
        node.child("isliked").removeValue()
    }
}
