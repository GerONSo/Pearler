package com.geronso.pearler.cocktail.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.geronso.pearler.R
import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.base.PearlPublishTimeConverter
import com.geronso.pearler.cocktail.model.data.FullCocktailData
import com.geronso.pearler.feed.model.data.PearlData
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CocktailPearlsAdapter : RecyclerView.Adapter<CocktailPearlsAdapter.ViewHolder>() {

    private var pearls: List<PearlData> = listOf()
    var cocktail: FullCocktailData? = null

    fun setData(pearls: List<PearlData>) {
        this.pearls = pearls
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_cocktail_pearl_with_profile, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pearl = pearls[position]
        holder.pearlUserName.text = pearl.account_name
        holder.pearlTiming.text = PearlPublishTimeConverter.convert(pearl.created_at)
        holder.cocktailReview.text = pearl.review
        holder.ratingBar.rating = pearl.grade.toFloat()
        Picasso
            .get()
            .load(pearl.image_url)
            .into(holder.image)
        Picasso
            .get()
            .load(pearl.account_image_url)
            .into(holder.avatar)
    }

    override fun getItemCount(): Int = pearls.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: CircleImageView = view.findViewById(R.id.iv_pearl_with_profile_avatar)
        val pearlUserName: TextView = view.findViewById(R.id.tv_pearl_with_profile_nickname)
        val pearlTiming: TextView = view.findViewById(R.id.tv_pearl_with_profile_timing)
        val image: ImageView = view.findViewById(R.id.iv_pearl_without_profile_cocktail_photo)
        val ratingBar: RatingBar = view.findViewById(R.id.rb_pearl_without_profile_rating)
        val cocktailReview: TextView = view.findViewById(R.id.tv_pearl_without_profile_review)
    }
}