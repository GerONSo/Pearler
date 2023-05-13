package com.geronso.pearler.friends.model.data

data class AllUsersPrefixRequest(
    val account_id: String,
    val prefix: String,
    val limit: Int
)
