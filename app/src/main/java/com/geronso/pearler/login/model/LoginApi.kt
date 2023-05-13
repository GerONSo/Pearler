package com.geronso.pearler.login.model

import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.base.QueryParameters
import com.geronso.pearler.login.model.data.CreateAccountWrapper
import com.geronso.pearler.login.model.data.ExistsData
import com.geronso.pearler.login.model.data.FirebaseUidWrapper
import com.geronso.pearler.login.model.data.LocalIdData
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApi {

    @GET("Account/GetIdByFirebaseUId/${NetworkDataProvider.API_VERSION}")
    fun getLocalIdByFirebaseUid(
        @Query(QueryParameters.FIREBASE_UID)
        firebaseUid: String
    ): Observable<LocalIdData>

    @POST("Account/Create/${NetworkDataProvider.API_VERSION}")
    fun createAccount(@Body account: CreateAccountWrapper): Observable<LocalIdData>
}