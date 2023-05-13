package com.geronso.pearler.registration.di

import com.geronso.pearler.registration.view.RegistrationActivity
import com.geronso.pearler.registration.model.RegistrationInteractor
import dagger.Subcomponent

@Subcomponent
interface RegistrationComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): RegistrationComponent
    }

    fun getRegistrationInteractor(): RegistrationInteractor

    fun inject(activity: RegistrationActivity)
}