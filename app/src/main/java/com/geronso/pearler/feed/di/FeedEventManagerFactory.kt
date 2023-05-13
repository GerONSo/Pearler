package com.geronso.pearler.feed.di

import androidx.lifecycle.ViewModel
import com.geronso.pearler.base.EventManagerFactory
import com.geronso.pearler.feed.viewmodel.FeedEventManager
import com.geronso.pearler.profile.viewmodel.ProfileEventManager
import javax.inject.Inject
import javax.inject.Provider

class FeedEventManagerFactory @Inject constructor(
    myViewModelProvider: Provider<FeedEventManager>
) : EventManagerFactory() {

    override val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        FeedEventManager::class.java to myViewModelProvider
    )
}