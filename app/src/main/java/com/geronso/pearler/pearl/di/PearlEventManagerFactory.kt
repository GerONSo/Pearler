package com.geronso.pearler.pearl.di

import androidx.lifecycle.ViewModel
import com.geronso.pearler.base.EventManagerFactory
import com.geronso.pearler.pearl.viewmodel.PearlEventManager
import com.geronso.pearler.search.viewmodel.SearchEventManager
import javax.inject.Inject
import javax.inject.Provider

class PearlEventManagerFactory @Inject constructor(
    myViewModelProvider: Provider<PearlEventManager>
) : EventManagerFactory() {

    override val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        PearlEventManager::class.java to myViewModelProvider
    )
}