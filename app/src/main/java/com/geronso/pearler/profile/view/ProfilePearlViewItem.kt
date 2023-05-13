package com.geronso.pearler.profile.view

import com.geronso.pearler.base.BaseViewItem

data class ProfilePearlViewItem(
    val cocktailReviewText: String,
    val rating: Float,
    val cocktailName: String,
    val pearlImageUrl: String,
    val cocktailId: String,
) : BaseViewItem()