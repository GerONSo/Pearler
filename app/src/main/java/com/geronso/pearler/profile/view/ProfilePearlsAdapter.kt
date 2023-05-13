package com.geronso.pearler.profile.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.geronso.pearler.R
import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.cocktail.model.data.FullCocktailData
import com.geronso.pearler.feed.model.data.PearlData
import com.squareup.picasso.Picasso

class ProfilePearlsAdapter : RecyclerView.Adapter<ProfilePearlsAdapter.ViewHolder>() {
    private var pearls: List<PearlData> = listOf()
    var cocktail: FullCocktailData? = null

    fun setData(pearls: List<PearlData>) {
        this.pearls = pearls
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_pearl_without_profile, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pearl = pearls[position]
        holder.cocktailReview.text = pearl.review
        holder.ratingBar.rating = pearl.grade.toFloat()
        holder.name.text = pearl.cocktail_name
        Picasso
            .get()
            .load(pearl.image_url)
            .into(holder.image)
    }

    override fun getItemCount(): Int = pearls.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.iv_pearl_without_profile_cocktail_photo)
        val ratingBar: RatingBar = view.findViewById(R.id.rb_pearl_without_profile_rating)
        val cocktailReview: TextView = view.findViewById(R.id.tv_pearl_without_profile_review)
        val name: TextView = view.findViewById(R.id.tv_pearl_without_profile_cocktail_name)
    }
}