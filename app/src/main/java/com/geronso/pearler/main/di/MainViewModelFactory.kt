package com.geronso.pearler.main.di

import androidx.lifecycle.ViewModel
import com.geronso.pearler.base.EventManagerFactory
import com.geronso.pearler.login.viewmodel.LoginEventManager
import com.geronso.pearler.main.MainViewModel
import javax.inject.Inject
import javax.inject.Provider

class MainViewModelFactory @Inject constructor(
    myViewModelProvider: Provider<MainViewModel>
) : EventManagerFactory() {

    override val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        MainViewModel::class.java to myViewModelProvider
    )
}