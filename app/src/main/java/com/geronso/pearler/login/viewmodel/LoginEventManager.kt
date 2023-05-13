package com.geronso.pearler.login.viewmodel

import android.util.Log
import com.geronso.pearler.base.ActivityRouter
import com.geronso.pearler.base.BaseEventManager
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.login.model.LoginInteractor
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginEventManager @Inject
constructor(
    private val loginInteractor: LoginInteractor
) :
    BaseEventManager<LoginViewModelEvent, LoginViewState>() {

    override fun onEvent(event: LoginViewModelEvent): ObservableSource<out LoginViewState> =
        when (event) {
            is LoginViewModelEvent.FirebaseLogin -> {
                if (event.userAuthenticationData.email.isEmpty() ||
                    event.userAuthenticationData.password.isEmpty()) {
                    Observable.just(
                        LoginViewState.LoginFailed(
                            LoginViewState.LoginFailed.LoginFailReason.EMPTY
                        )
                    )
                } else {
                    loginInteractor.login(event.userAuthenticationData)
                }
            }
            is LoginViewModelEvent.GetLocalIdByFirebaseUid -> {
                event.firebaseUser?.let {
                    loginInteractor.getLocalIdByFirebaseUid(event.firebaseUser.uid)
                } ?: Observable.just(LoginViewState.LoginFailed(LoginViewState.LoginFailed.LoginFailReason.FIREBASE))
            }
            is LoginViewModelEvent.CreateAccount -> {
                loginInteractor.createAccount(event.account)
            }
        }.subscribeOn(Schedulers.io())

    override fun onNext(value: LoginViewState?) = viewEventObservable.postValue(value)

    override fun onError(e: Throwable?) {
        reinit()
        viewEventObservable.postValue(LoginViewState.LoginFailed(LoginViewState.LoginFailed.LoginFailReason.FIREBASE))
        Log.e(Logger.LOGIN, e.toString())
    }

    override fun onComplete() {}

}