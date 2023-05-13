package com.geronso.pearler.cocktail.di

import com.geronso.pearler.cocktail.view.CocktailFragment
import com.geronso.pearler.search.di.SearchComponent
import com.geronso.pearler.search.di.SearchEventManagerFactory
import com.geronso.pearler.search.view.SearchFragment
import dagger.Subcomponent

@Subcomponent(modules = [CocktailModule::class])
interface CocktailComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): CocktailComponent
    }

    fun viewModelsFactory(): CocktailEventManagerFactory

    fun inject(fragment: CocktailFragment)
}