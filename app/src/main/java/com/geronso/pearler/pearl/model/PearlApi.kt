package com.geronso.pearler.pearl.model

import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.pearl.model.data.PublishPearlData
import com.geronso.pearler.pearl.model.data.PublishPearlResultData
import com.geronso.pearler.profile.model.data.ImageIdResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PearlApi {
    @Multipart
    @POST("Pearl/Create/${NetworkDataProvider.API_VERSION}")
    fun publishPearl(
        @Part imageData: MultipartBody.Part,
        @Part pearlData: MultipartBody.Part,
        @Part fileExtension: MultipartBody.Part,
    ): Observable<PublishPearlResultData>
}