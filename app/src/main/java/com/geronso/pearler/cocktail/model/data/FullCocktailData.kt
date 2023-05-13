package com.geronso.pearler.cocktail.model.data

import com.geronso.pearler.cocktail.data.PearlStatisticsData
import com.geronso.pearler.feed.model.data.PearlData

data class FullCocktailData(
    val cocktail: CocktailData,
    val pearls: List<PearlData>,
    val pearls_statistics: PearlStatisticsData
)
