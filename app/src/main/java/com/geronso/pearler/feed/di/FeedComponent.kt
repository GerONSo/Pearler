package com.geronso.pearler.feed.di

import com.geronso.pearler.feed.view.FeedFragment
import com.geronso.pearler.profile.di.ProfileEventManagerFactory
import dagger.Subcomponent

@Subcomponent
interface FeedComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): FeedComponent
    }

    fun viewModelsFactory(): FeedEventManagerFactory

    fun inject(fragment: FeedFragment)
}