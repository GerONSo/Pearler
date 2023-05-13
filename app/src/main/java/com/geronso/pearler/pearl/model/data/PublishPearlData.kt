package com.geronso.pearler.pearl.model.data

import android.net.Uri

data class PublishPearlData(
    val account_id: String,
    val cocktail_id: String,
    val grade: Int,
    val review: String,
)