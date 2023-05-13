package com.geronso.pearler.login.di

import androidx.lifecycle.ViewModel
import com.geronso.pearler.base.EventManagerFactory
import com.geronso.pearler.login.viewmodel.LoginEventManager
import com.geronso.pearler.search.viewmodel.SearchEventManager
import javax.inject.Inject
import javax.inject.Provider

class LoginEventManagerFactory @Inject constructor(
    myViewModelProvider: Provider<LoginEventManager>
) : EventManagerFactory() {

    override val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        LoginEventManager::class.java to myViewModelProvider
    )
}