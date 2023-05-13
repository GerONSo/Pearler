package com.geronso.pearler.friends.view

import com.geronso.pearler.base.BaseViewItem

data class AllUsersViewItem(
    val id: String,
    val name: String,
    val imageUrl: String,
    val isAddFriendLoading: Boolean,
    val isFriendAdded: Boolean,
) : BaseViewItem()