package com.geronso.pearler.profile.di

import com.geronso.pearler.cocktail.view.CocktailPearlsAdapter
import com.geronso.pearler.profile.view.ProfilePearlsAdapter
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
class ProfileModule {
    @Provides
    fun provideAdapter(): ProfilePearlsAdapter = ProfilePearlsAdapter()
}