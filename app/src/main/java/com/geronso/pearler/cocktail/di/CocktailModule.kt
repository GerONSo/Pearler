package com.geronso.pearler.cocktail.di

import com.geronso.pearler.cocktail.view.CocktailPearlsAdapter
import com.geronso.pearler.search.view.SearchCocktailsAdapter
import dagger.Module
import dagger.Provides
@Module
class CocktailModule {
    @Provides
    fun provideAdapter(): CocktailPearlsAdapter = CocktailPearlsAdapter()
}