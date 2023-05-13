package com.geronso.pearler.pearl.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.geronso.pearler.R
import com.geronso.pearler.search.model.data.SmallCocktailEntity
import com.squareup.picasso.Picasso

class PearlVariantsAdapter : RecyclerView.Adapter<PearlVariantsAdapter.ViewHolder>() {

    private var allCocktails: List<SmallCocktailEntity> = listOf()
    lateinit var onSelectedCocktail: (cocktail: SmallCocktailEntity) -> Unit

    fun setData(allCocktails: List<SmallCocktailEntity>) {
        this.allCocktails = allCocktails
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_small_cocktail_variant, parent, false)
        )

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
                .into(holder.image)
        }
    }

    override fun getItemCount(): Int = allCocktails.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.small_cocktail_name)
        val image: ImageView = view.findViewById(R.id.small_cocktail_image)

    }
}