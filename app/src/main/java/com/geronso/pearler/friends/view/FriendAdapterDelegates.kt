package com.geronso.pearler.friends.view

import androidx.core.view.isVisible
import com.geronso.pearler.R
import com.geronso.pearler.base.BaseViewItem
import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.databinding.CardFriendBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.squareup.picasso.Picasso

fun friendsAdapterDelegate(
    onSelectedProfile: (id: String) -> Unit,
) = adapterDelegateViewBinding<FriendsViewItem, BaseViewItem, CardFriendBinding>(
    { inflater, parent -> CardFriendBinding.inflate(inflater, parent, false) },
) {
    bind {
        binding.friendName.text = item.name
        binding.addFriend.isVisible = false
        binding.root.setOnClickListener {
            onSelectedProfile(item.id)
        }
        if (item.image_url != "") {
            Picasso
                .get()
                .load(item.image_url)
                .placeholder(R.drawable.small_placeholder)
                .error(R.drawable.small_placeholder)
                .fit()
                .into(binding.friendAvatar)
        } else {
            Picasso
                .get()
                .load(R.drawable.small_placeholder)
                .fit()
                .into(binding.friendAvatar)
        }
    }
}

fun allUsersAdapterDelegate(
    onSelectedProfile: (id: String) -> Unit,
    onAddFriend: (id: String) -> Unit,
    onRemoveFriend: (id: String) -> Unit,
) = adapterDelegateViewBinding<AllUsersViewItem, BaseViewItem, CardFriendBinding>(
    { inflater, parent -> CardFriendBinding.inflate(inflater, parent, false) },
) {
    bind {
        binding.friendName.text = item.name
        binding.addFriend.isVisible = !item.isAddFriendLoading
        binding.addFriend.setOnClickListener {
            if (!item.isFriendAdded) {
                onAddFriend(item.id)
            } else {
                onRemoveFriend(item.id)
            }
        }
        Picasso
            .get()
            .load(R.drawable.add_friend)
            .error(
                if (!item.isFriendAdded) R.drawable.add_friend
                else R.drawable.ic_check
            )
            .into(binding.addFriend)
        binding.root.setOnClickListener {
            onSelectedProfile(item.id)
        }
        binding.addFriendProgress.isVisible = item.isAddFriendLoading
        if (item.imageUrl.isNotEmpty()) {
            Picasso
                .get()
                .load(item.imageUrl)
                .placeholder(R.drawable.small_placeholder)
                .error(R.drawable.small_placeholder)
                .into(binding.friendAvatar)
        } else {
            Picasso
                .get()
                .load(R.drawable.small_placeholder)
                .into(binding.friendAvatar)
        }
    }
}