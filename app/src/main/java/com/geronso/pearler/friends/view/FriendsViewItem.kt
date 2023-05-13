package com.geronso.pearler.friends.view

import com.geronso.pearler.base.BaseViewItem

data class FriendsViewItem(
    val id: String,
    val name: String,
    val image_url: String,
) : BaseViewItem()