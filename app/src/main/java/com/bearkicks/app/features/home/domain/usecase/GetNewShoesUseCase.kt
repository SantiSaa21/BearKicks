package com.bearkicks.app.features.home.domain.usecase

import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.home.domain.repository.IHomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNewShoesUseCase(private val repo: IHomeRepository) {
    operator fun invoke(): Flow<List<ShoeModel>> =
        repo.watchShoes().map { all ->
            val news = all.filter { it.isNew }
            news.ifEmpty { all.filter { !it.isFeatured && ((it.discountPrice ?: 0.0) == 0.0) }.take(10) }
        }
}