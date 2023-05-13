package com.geronso.pearler.profile.view

import androidx.core.view.isVisible
import com.geronso.pearler.base.BaseViewItem
import com.geronso.pearler.databinding.CardPearlWithProfileBinding
import com.geronso.pearler.feed.view.FeedViewItem
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.squareup.picasso.Picasso

fun profileAdapterDelegate(
    openCocktail: (String) -> Unit
) = adapterDelegateViewBinding<ProfilePearlViewItem, BaseViewItem, CardPearlWithProfileBinding>(
    { inflater, parent -> CardPearlWithProfileBinding.inflate(inflater, parent, false) },
) {
    bind {
        with(binding) {
            cocktailReview.text = item.cocktailReviewText
            rating.rating = item.rating
            cocktailName.text = item.cocktailName
            userName.isVisible = false
            pearlTime.isVisible = false
            root.setOnClickListener {
                openCocktail(item.cocktailId)
            }
            Picasso
                .get()
                .load(item.pearlImageUrl)
                .into(cocktailImage)
            avatar.isVisible = false
        }
    }
}