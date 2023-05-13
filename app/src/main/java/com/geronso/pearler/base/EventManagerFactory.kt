package com.geronso.pearler.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Provider

abstract class EventManagerFactory : ViewModelProvider.Factory {
    abstract val providers: Map<Class<*>, Provider<out ViewModel>>

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return providers[modelClass]!!.get() as T
    }
}