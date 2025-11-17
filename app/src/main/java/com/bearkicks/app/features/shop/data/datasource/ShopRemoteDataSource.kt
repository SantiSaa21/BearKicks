package com.bearkicks.app.features.shop.data.datasource

import com.bearkicks.app.features.shop.data.dto.ShoeDto
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ShopRemoteDataSource(private val ref: DatabaseReference) {
    init { normalizeLikeKeys() }
    fun watchShoesDto(): Flow<List<ShoeDto>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = buildList {
                    snapshot.children.forEach { child ->
                        val dto = child.getValue(ShoeDto::class.java)
                        if (dto != null) {
                            if (dto.id.isBlank()) dto.id = child.key.orEmpty()
                            add(dto)
                        }
                    }
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
    fun watchByIdDto(id: String): Flow<ShoeDto?> = callbackFlow {
        val node = ref.child(id)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(ShoeDto::class.java)?.apply { if (this.id.isBlank()) this.id = id })
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        node.addValueEventListener(listener)
        awaitClose { node.removeEventListener(listener) }
    }
    fun toggleLike(id: String, liked: Boolean) {
        val node = ref.child(id)
        node.child("isLiked").setValue(liked)
        node.child("isliked").removeValue()
    }
    private fun normalizeLikeKeys() {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { child ->
                    val legacy = child.child("isliked").getValue(Boolean::class.java)
                    val canonical = child.child("isLiked").getValue(Boolean::class.java)
                    if (legacy != null && canonical == null) child.ref.child("isLiked").setValue(legacy)
                    if (legacy != null) child.ref.child("isliked").removeValue()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}