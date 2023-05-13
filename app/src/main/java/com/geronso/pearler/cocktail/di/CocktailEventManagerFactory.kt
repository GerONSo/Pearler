package com.geronso.pearler.cocktail.di

import androidx.lifecycle.ViewModel
import com.geronso.pearler.base.EventManagerFactory
import com.geronso.pearler.cocktail.viewmodel.CocktailEventManager
import javax.inject.Inject
import javax.inject.Provider

class CocktailEventManagerFactory @Inject constructor(
    myViewModelProvider: Provider<CocktailEventManager>
) : EventManagerFactory() {

    override val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        CocktailEventManager::class.java to myViewModelProvider
    )
}