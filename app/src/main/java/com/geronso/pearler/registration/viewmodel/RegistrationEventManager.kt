package com.geronso.pearler.registration.viewmodel

import android.util.Log
import com.geronso.pearler.base.BaseEventManager
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.registration.model.RegistrationInteractor
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RegistrationEventManager @Inject
constructor(private val registrationInteractor: RegistrationInteractor) :
    BaseEventManager<RegistrationViewModelEvent, RegistrationViewState>() {
    override fun onEvent(event: RegistrationViewModelEvent): ObservableSource<out RegistrationViewState> =
        when (event) {
            is RegistrationViewModelEvent.Register -> {
                if (event.userAuthenticationData.email.isEmpty() ||
                    event.userAuthenticationData.password.isEmpty()) {
                    Observable.just(
                        RegistrationViewState.RegistrationFailed(
                            RegistrationViewState.RegistrationFailed.RegistrationFailReason.EMPTY_EMAIL_OR_PASSWORD
                        )
                    )
                } else {
                    registrationInteractor.register(event.userAuthenticationData)
                }
            }
        }.subscribeOn(Schedulers.io())

    override fun onNext(value: RegistrationViewState?) = viewEventObservable.postValue(value)

    override fun onError(e: Throwable?) {
        reinit()
        Log.e(Logger.REGISTRATION, e.toString())
        viewEventObservable.postValue(
            RegistrationViewState.RegistrationFailed(RegistrationViewState.RegistrationFailed.RegistrationFailReason.FIREBASE_ERROR)
        )
    }

    override fun onComplete() {}
}