package com.geronso.pearler.login.model

import android.util.Log
import com.geronso.pearler.base.FirebaseAuthProvider
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.login.UserAuthenticationData
import com.geronso.pearler.login.model.data.CreateAccountWrapper
import com.geronso.pearler.login.model.data.FirebaseUidWrapper
import com.geronso.pearler.login.model.data.LocalIdData
import com.geronso.pearler.login.viewmodel.LoginViewState
import io.reactivex.Observable
import javax.inject.Inject
import retrofit2.Retrofit

class LoginInteractor @Inject constructor(
    private val authProvider: FirebaseAuthProvider,
    private val retrofit: Retrofit
) {

    fun login(userAuthenticationData: UserAuthenticationData): Observable<out LoginViewState> =
        Observable.create {
            Log.d(Logger.LOGIN, "${Thread.currentThread().name}: try login...")
            authProvider.auth
                .signInWithEmailAndPassword(
                    userAuthenticationData.email,
                    userAuthenticationData.password
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(Logger.LOGIN, "signInWithEmailAndPassword:success")
                        it.onNext(LoginViewState.FirebaseLoginSuccess)
                    } else {
                        Log.d(Logger.LOGIN, "signInWithEmailAndPassword:failure", task.exception)
                        it.onNext(LoginViewState.LoginFailed(LoginViewState.LoginFailed.LoginFailReason.FIREBASE))
                    }
                }
        }

    fun getLocalIdByFirebaseUid(firebaseUid: String): Observable<out LoginViewState> =
        retrofit
            .create(LoginApi::class.java)
            .getLocalIdByFirebaseUid(firebaseUid)
            .map {
                LoginViewState.LoginSuccess(it)
            }

    fun createAccount(account: CreateAccountWrapper): Observable<out LoginViewState> =
        retrofit
            .create(LoginApi::class.java)
            .createAccount(account)
            .map {
                LoginViewState.AccountCreated(it)
            }
}