package com.bearkicks.app.features.wishlist.data.datasource

import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.google.firebase.database.DatabaseReference
class FavoritesRemoteDataSource(
    private val dbRoot: DatabaseReference,
    private val userIdProvider: () -> String
) {
    private fun favsRef(): DatabaseReference = dbRoot.child("favorites")
    private fun shoesRef(): DatabaseReference = dbRoot.child("shoes")
    private fun userFavsRef(uid: String): DatabaseReference = dbRoot.child("userFavorites").child(uid)

    suspend fun setFavorite(shoe: ShoeModel, favored: Boolean) {
        val userId = userIdProvider()
        val shoeNode = shoesRef().child(shoe.id)
        shoeNode.child("isLiked").setValue(favored)
        shoeNode.child("isliked").removeValue()

        val favoriteNode = favsRef().child(shoe.id)
        if (favored) {
            val payload = mapOf(
                "id" to shoe.id,
                "name" to shoe.name,
                "brand" to shoe.brand,
                "price" to shoe.price,
                "discountPrice" to shoe.discountPrice,
                "imageUrl" to shoe.imageUrl,
                "isLiked" to true,
                "savedAt" to System.currentTimeMillis()
            )
            favoriteNode.setValue(payload)
            userFavsRef(userId).child(shoe.id).setValue(true)
        } else {
            favoriteNode.removeValue()
            userFavsRef(userId).child(shoe.id).removeValue()
        }
    }
}
