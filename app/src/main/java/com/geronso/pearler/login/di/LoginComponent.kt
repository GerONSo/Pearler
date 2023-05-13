package com.geronso.pearler.login.di

import com.geronso.pearler.base.FirebaseAuthProvider
import com.geronso.pearler.login.view.LoginActivity
import com.geronso.pearler.login.model.LoginInteractor
import com.geronso.pearler.login.view.EmailConfirmationActivity
import com.geronso.pearler.login.view.SetNameActivity
import com.geronso.pearler.search.di.SearchEventManagerFactory
import dagger.Component
import dagger.Subcomponent

@Subcomponent
interface LoginComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    fun viewModelsFactory(): LoginEventManagerFactory

    fun inject(activity: LoginActivity)
    fun inject(activity: SetNameActivity)
    fun inject(activity: EmailConfirmationActivity)
}