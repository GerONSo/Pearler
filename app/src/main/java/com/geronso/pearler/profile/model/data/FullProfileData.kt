package com.geronso.pearler.profile.model.data

import com.geronso.pearler.cocktail.data.PearlStatisticsData
import com.geronso.pearler.feed.model.data.PearlData

data class FullProfileData(
    val account: ProfileData,
    val pearls: List<PearlData>,
    val pearls_statistics: PearlStatisticsData,
    val subscriptions_statistics: ProfileSubscriptionsData
)
