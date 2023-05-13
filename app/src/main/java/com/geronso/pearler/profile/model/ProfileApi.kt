package com.geronso.pearler.profile.model

import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.base.QueryParameters
import com.geronso.pearler.login.model.data.CreateAccountWrapper
import com.geronso.pearler.login.model.data.LocalIdData
import com.geronso.pearler.profile.model.data.EditProfileWrapper
import com.geronso.pearler.profile.model.data.FullGeneralProfileData
import com.geronso.pearler.profile.model.data.FullProfileData
import com.geronso.pearler.profile.model.data.GeneralProfileIdWrapper
import com.geronso.pearler.profile.model.data.ImageIdResponse
import com.geronso.pearler.profile.model.data.ProfileIdWrapper
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ProfileApi {
    @GET("Account/SelfFullView/${NetworkDataProvider.API_VERSION}")
    fun getSelfProfileById(
        @Query(QueryParameters.ID) profileId: String
    ) : Observable<FullProfileData>

    @GET("Account/FullView/${NetworkDataProvider.API_VERSION}")
    fun getProfileById(
        @Query(QueryParameters.ID) id: String,
        @Query(QueryParameters.SELF_ID) selfId: String
    ) : Observable<FullGeneralProfileData>

    @Multipart
    @POST("Account/Update/${NetworkDataProvider.API_VERSION}")
    fun editProfile(
        @Part imageData: MultipartBody.Part,
        @Part objectId: MultipartBody.Part?,
        @Part fileExtension: MultipartBody.Part
    ): Observable<LocalIdData>
}