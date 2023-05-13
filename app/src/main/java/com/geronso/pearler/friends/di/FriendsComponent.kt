package com.geronso.pearler.friends.di

import com.geronso.pearler.friends.view.FriendsFragment
import com.geronso.pearler.search.di.SearchComponent
import com.geronso.pearler.search.di.SearchEventManagerFactory
import com.geronso.pearler.search.view.SearchFragment
import dagger.Subcomponent

@Subcomponent(modules = [FriendsModule::class])
interface FriendsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): FriendsComponent
    }

    fun viewModelsFactory(): FriendsEventManagerFactory

    fun inject(fragment: FriendsFragment)
}