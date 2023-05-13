package com.geronso.pearler.friends.di

import androidx.lifecycle.ViewModel
import com.geronso.pearler.base.EventManagerFactory
import com.geronso.pearler.friends.viewmodel.FriendsEventManager
import com.geronso.pearler.search.viewmodel.SearchEventManager
import javax.inject.Inject
import javax.inject.Provider

class FriendsEventManagerFactory @Inject constructor(
    myViewModelProvider: Provider<FriendsEventManager>
) : EventManagerFactory() {

    override val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        FriendsEventManager::class.java to myViewModelProvider
    )
}