package com.geronso.pearler.registration.model

import com.geronso.pearler.base.NetworkDataProvider
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationApi {
    @POST("Account/Create/${NetworkDataProvider.API_VERSION}")
    fun register(@Body registrationData: RegistrationData): Observable<RegistrationResultData>
}