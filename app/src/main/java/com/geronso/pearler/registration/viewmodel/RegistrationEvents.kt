package com.geronso.pearler.registration.viewmodel

import android.app.Activity
import com.geronso.pearler.base.BaseViewModelEvent
import com.geronso.pearler.base.BaseViewState
import com.geronso.pearler.login.UserAuthenticationData
import com.geronso.pearler.registration.model.RegistrationData
import com.geronso.pearler.registration.model.RegistrationResultData

sealed class RegistrationViewState : BaseViewState {
    data class RegistrationSuccess(val registrationData: RegistrationData) : RegistrationViewState()
    data class RegistrationFailed(val error: RegistrationFailReason) : RegistrationViewState() {
        enum class RegistrationFailReason {
            EMPTY_EMAIL_OR_PASSWORD,
            FIREBASE_ERROR
        }
    }
}

sealed class RegistrationViewModelEvent : BaseViewModelEvent {
    data class Register(val userAuthenticationData: UserAuthenticationData) : RegistrationViewModelEvent()
}