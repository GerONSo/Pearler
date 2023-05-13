package com.geronso.pearler.profile.di

import androidx.lifecycle.ViewModel
import com.geronso.pearler.base.EventManagerFactory
import com.geronso.pearler.profile.viewmodel.ProfileEventManager
import com.geronso.pearler.search.viewmodel.SearchEventManager
import javax.inject.Inject
import javax.inject.Provider

class ProfileEventManagerFactory @Inject constructor(
    myViewModelProvider: Provider<ProfileEventManager>
) : EventManagerFactory() {

    override val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        ProfileEventManager::class.java to myViewModelProvider
    )
}