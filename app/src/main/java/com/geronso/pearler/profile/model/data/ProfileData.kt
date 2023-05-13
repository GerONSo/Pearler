package com.geronso.pearler.profile.model.data

import com.geronso.pearler.friends.view.AllUsersViewItem
import com.geronso.pearler.friends.view.FriendsViewItem

data class ProfileData(
    val id: String,
    val name: String,
    val email: String,
    val firebase_uid: String,
    val image_url: String,
    val description: String,
)

fun ProfileData.mapFriendsViewItem(): FriendsViewItem = FriendsViewItem(
    id = id,
    name = name,
    image_url = image_url
)

fun ProfileData.mapAllUsersViewItem(): AllUsersViewItem = AllUsersViewItem(
    id = id,
    name = name,
    imageUrl = image_url,
    isAddFriendLoading = false,
    isFriendAdded = false,
)
