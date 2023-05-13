package com.geronso.pearler.profile.di

import com.geronso.pearler.profile.view.ProfileFragment
import com.geronso.pearler.search.di.SearchComponent
import com.geronso.pearler.search.di.SearchEventManagerFactory
import com.geronso.pearler.search.view.SearchFragment
import dagger.Subcomponent

@Subcomponent(modules = [ProfileModule::class])
interface ProfileComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ProfileComponent
    }

    fun viewModelsFactory(): ProfileEventManagerFactory

    fun inject(fragment: ProfileFragment)
}