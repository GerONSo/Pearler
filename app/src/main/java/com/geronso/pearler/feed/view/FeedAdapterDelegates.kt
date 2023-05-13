package com.geronso.pearler.feed.view

import com.geronso.pearler.base.BaseViewItem
import com.geronso.pearler.databinding.CardEmptyElementBinding
import com.geronso.pearler.databinding.CardPearlWithProfileBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.squareup.picasso.Picasso

fun feedAdapterDelegate(
    openCocktail: (cocktailId: String) -> Unit = { },
    openProfile: (profileId: String) -> Unit = {},
) = adapterDelegateViewBinding<FeedViewItem, BaseViewItem, CardPearlWithProfileBinding>(
    { inflater, parent -> CardPearlWithProfileBinding.inflate(inflater, parent, false) },
) {
    bind {
        with(binding) {
            cocktailReview.text = item.cocktailReviewText
            rating.rating = item.rating
            cocktailName.text = item.cocktailName
            userName.text = item.userName
            pearlTime.text = item.pearlTime
            binding.avatar.setOnClickListener { openProfile(item.profileId) }
            binding.userName.setOnClickListener { openProfile(item.profileId) }
            root.setOnClickListener { openCocktail(item.cocktailId) }
            Picasso
                .get()
                .load(item.pearlImageUrl)
                .into(cocktailImage)
            Picasso
                .get()
                .load(item.avatarImageUrl)
                .into(avatar)
        }
    }
}

fun feedEmptyAdapterDelegate() = adapterDelegateViewBinding<FeedEmptyListItem, BaseViewItem, CardEmptyElementBinding>(
    { inflater, parent -> CardEmptyElementBinding.inflate(inflater, parent, false) }
) {
    bind {
        binding.text.text = item.text
    }
}