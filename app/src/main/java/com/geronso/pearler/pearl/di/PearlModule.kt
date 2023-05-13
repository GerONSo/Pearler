package com.geronso.pearler.pearl.di

import com.geronso.pearler.pearl.view.PearlVariantsAdapter
import com.geronso.pearler.search.view.SearchCocktailsAdapter
import dagger.Module
import dagger.Provides

@Module
class PearlModule {
    @Provides
    fun provideAdapter(): PearlVariantsAdapter = PearlVariantsAdapter()
}