package com.geronso.pearler.pearl.di

import com.geronso.pearler.pearl.view.PearlFragment
import com.geronso.pearler.search.di.SearchComponent
import com.geronso.pearler.search.di.SearchEventManagerFactory
import com.geronso.pearler.search.view.SearchFragment
import dagger.Subcomponent

@Subcomponent(modules = [PearlModule::class])
interface PearlComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): PearlComponent
    }

    fun viewModelsFactory(): PearlEventManagerFactory

    fun searchViewModelsFactory(): SearchEventManagerFactory

    fun inject(fragment: PearlFragment)
}