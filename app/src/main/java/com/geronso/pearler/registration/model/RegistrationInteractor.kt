package com.geronso.pearler.registration.model

import android.util.Log
import com.geronso.pearler.base.FirebaseAuthProvider
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.login.UserAuthenticationData
import com.geronso.pearler.registration.model.RegistrationApi
import com.geronso.pearler.registration.model.RegistrationData
import com.geronso.pearler.registration.viewmodel.RegistrationViewState
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class RegistrationInteractor @Inject constructor(
    private val authProvider: FirebaseAuthProvider,
    private val api: Retrofit
) {
    fun register(userAuthenticationData: UserAuthenticationData): Observable<out RegistrationViewState> =
        Observable.create {
            Log.d(Logger.REGISTRATION, "${Thread.currentThread().name}: try register...")
            authProvider.auth
                .createUserWithEmailAndPassword(
                    userAuthenticationData.email,
                    userAuthenticationData.password
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(Logger.REGISTRATION, "createUserWithEmail:success")
                        it.onNext(
                            RegistrationViewState.RegistrationSuccess(
                                RegistrationData(
                                    userAuthenticationData.email,
                                    task.result.user?.uid!!
                                )
                            )
                        )
                    } else {
                        Log.d(Logger.REGISTRATION, "createUserWithEmail:failure", task.exception)
                        it.onNext(RegistrationViewState.RegistrationFailed(
                            RegistrationViewState.RegistrationFailed.RegistrationFailReason.FIREBASE_ERROR)
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    it.onNext(
                        RegistrationViewState.RegistrationFailed(RegistrationViewState.RegistrationFailed.RegistrationFailReason.FIREBASE_ERROR)
                    )
                }
                .addOnCanceledListener {
                    it.onNext(RegistrationViewState.RegistrationFailed(
                        RegistrationViewState.RegistrationFailed.RegistrationFailReason.FIREBASE_ERROR
                    ))
                }

        }
}