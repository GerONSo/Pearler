package com.geronso.pearler.login.viewmodel

import com.geronso.pearler.base.BaseViewModelEvent
import com.geronso.pearler.base.BaseViewState
import com.geronso.pearler.login.UserAuthenticationData
import com.geronso.pearler.login.model.data.CreateAccountWrapper
import com.geronso.pearler.login.model.data.ExistsData
import com.geronso.pearler.login.model.data.LocalIdData
import com.google.firebase.auth.FirebaseUser

sealed class LoginViewState : BaseViewState {
    object FirebaseLoginSuccess : LoginViewState()
    data class LoginFailed(val error: LoginFailReason) : LoginViewState() {
        enum class LoginFailReason {
            FIREBASE, EMPTY
        }
    }
    data class LoginSuccess(val localIdData: LocalIdData) : LoginViewState()
    data class AccountCreated(val localIdData: LocalIdData) : LoginViewState()
}

sealed class LoginViewModelEvent : BaseViewModelEvent {
    data class FirebaseLogin(val userAuthenticationData: UserAuthenticationData) :
        LoginViewModelEvent()

    data class GetLocalIdByFirebaseUid(val firebaseUser: FirebaseUser?) : LoginViewModelEvent()
    data class CreateAccount(val account: CreateAccountWrapper) : LoginViewModelEvent()
}