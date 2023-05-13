package com.geronso.pearler.feed.model.data

import com.geronso.pearler.base.PearlPublishTimeConverter
import com.geronso.pearler.feed.view.FeedViewItem
import com.geronso.pearler.profile.view.ProfilePearlViewItem

data class PearlData(
    val id: String,
    val account_id: String,
    val cocktail_id: String,
    val grade: Int,
    val review: String,
    val created_at: Long,
    val account_name: String,
    val cocktail_name: String,
    val image_url: String,
    val account_image_url: String
)

fun PearlData.mapProfileViewState(): FeedViewItem =
    FeedViewItem(
        cocktailReviewText = review,
        rating = grade.toFloat(),
        cocktailName = cocktail_name,
        userName = account_name,
        pearlTime = PearlPublishTimeConverter.convert(created_at),
        pearlImageUrl = image_url,
        avatarImageUrl = account_image_url,
        cocktailId = cocktail_id,
        profileId = account_id,
    )

fun PearlData.mapNoProfileViewState(): ProfilePearlViewItem =
    ProfilePearlViewItem(
        cocktailReviewText = review,
        rating = grade.toFloat(),
        cocktailName = cocktail_name,
        pearlImageUrl = image_url,
        cocktailId = cocktail_id,
    )