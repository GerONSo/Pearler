package com.geronso.pearler.search.di

import com.geronso.pearler.pearl.view.PearlFragment
import com.geronso.pearler.search.view.SearchFragment
import dagger.Subcomponent

@Subcomponent()
interface SearchComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SearchComponent
    }

    fun viewModelsFactory(): SearchEventManagerFactory

    fun inject(fragment: SearchFragment)
}