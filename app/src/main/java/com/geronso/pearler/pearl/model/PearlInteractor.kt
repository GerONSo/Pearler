package com.geronso.pearler.pearl.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.geronso.pearler.pearl.model.data.PublishPearlData
import com.geronso.pearler.pearl.viewmodel.PearlViewState
import com.geronso.pearler.profile.model.ProfileApi
import com.geronso.pearler.profile.model.data.EditProfileWrapper
import com.geronso.pearler.profile.viewmodel.ProfileViewState
import io.reactivex.Observable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit


class PearlInteractor @Inject constructor(
    private val retrofit: Retrofit
) {
    fun publishPearl(
        imageData: MultipartBody.Part,
        pearlData: MultipartBody.Part,
        fileExtensionData: MultipartBody.Part
    ): Observable<out PearlViewState> =
        retrofit
            .create(PearlApi::class.java)
            .publishPearl(imageData, pearlData, fileExtensionData)
            .map {
                PearlViewState.PublishPearlResult(it)
            }
}