package com.geronso.pearler.search.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.geronso.pearler.base.EventManagerFactory
import com.geronso.pearler.search.viewmodel.SearchEventManager
import javax.inject.Inject
import javax.inject.Provider

class SearchEventManagerFactory @Inject constructor(
    myViewModelProvider: Provider<SearchEventManager>
) : EventManagerFactory() {

    override val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        SearchEventManager::class.java to myViewModelProvider
    )
}