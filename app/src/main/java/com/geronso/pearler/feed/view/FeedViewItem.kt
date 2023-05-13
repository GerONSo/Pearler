package com.geronso.pearler.feed.view

import com.geronso.pearler.base.BaseViewItem

data class FeedViewItem(
    val cocktailReviewText: String,
    val rating: Float,
    val cocktailName: String,
    val userName: String,
    val pearlTime: String,
    val pearlImageUrl: String,
    val avatarImageUrl: String,
    val cocktailId: String,
    val profileId: String,
): BaseViewItem()