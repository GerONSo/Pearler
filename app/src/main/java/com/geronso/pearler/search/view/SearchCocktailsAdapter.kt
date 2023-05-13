package com.geronso.pearler.search.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.geronso.pearler.R
import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.search.model.data.SmallCocktailEntity
import com.geronso.pearler.widgets.BottomCropImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class SearchCocktailsAdapter : RecyclerView.Adapter<SearchCocktailsAdapter.ViewHolder>() {

    private var allCocktails: List<SmallCocktailEntity> = listOf()
    lateinit var onSelectedCocktail: (cocktail: SmallCocktailEntity) -> Unit

    fun setData(allCocktails: List<SmallCocktailEntity>) {
        this.allCocktails = allCocktails
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_small_cocktail, parent, false)
        )

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cocktail = allCocktails[position]
        holder.name.text = cocktail.name
        holder.itemView.setOnClickListener {
            onSelectedCocktail(cocktail)
        }
        if (cocktail.image_url.isNotEmpty()) {
            Picasso
                .get()
                .load(cocktail.image_url)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.cocktail_placeholder_horizontal)
                .error(R.drawable.cocktail_placeholder_horizontal)
                .into(holder.image)
        } else {
            Picasso
                .get()
                .load(R.drawable.cocktail_placeholder_horizontal)
                .fit()
                .into(holder.image)
        }
    }

    override fun getItemCount(): Int = allCocktails.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.small_cocktail_name)
        val image: BottomCropImageView = view.findViewById(R.id.small_cocktail_image)
    }
}