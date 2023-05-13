package com.geronso.pearler.profile.model

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import com.geronso.pearler.friends.model.FriendsApi
import com.geronso.pearler.friends.model.data.SubscriptionWrapper
import com.geronso.pearler.profile.model.data.EditProfileWrapper
import com.geronso.pearler.profile.model.data.GeneralProfileIdWrapper
import com.geronso.pearler.profile.model.data.ProfileIdWrapper
import com.geronso.pearler.profile.viewmodel.ProfileViewState
import io.reactivex.Observable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit

class ProfileInteractor @Inject constructor(
    private val retrofit: Retrofit
) {
    fun getSelfProfileById(profileId: String) : Observable<out ProfileViewState> =
        retrofit
            .create(ProfileApi::class.java)
            .getSelfProfileById(profileId)
            .map {
                ProfileViewState.SelfProfileResultData(it)
            }

    fun getProfileById(profileId: GeneralProfileIdWrapper): Observable<out ProfileViewState> =
        retrofit
            .create(ProfileApi::class.java)
            .getProfileById(
                profileId.id,
                profileId.selfId
            )
            .map {
                ProfileViewState.ProfileResultData(it)
            }

    fun subscribe(subscriptionWrapper: SubscriptionWrapper): Observable<out ProfileViewState> =
        retrofit
            .create(FriendsApi::class.java)
            .subscribe(subscriptionWrapper)
            .map {
                ProfileViewState.SubscriptionResult(it)
            }

    fun unsubscribe(subscriptionWrapper: SubscriptionWrapper): Observable<out ProfileViewState> =
        retrofit
            .create(FriendsApi::class.java)
            .unsubscribe(subscriptionWrapper)
            .map {
                ProfileViewState.UnsubscriptionResult(it)
            }

    fun editProfileData(
        profileData: MultipartBody.Part,
        imageData: MultipartBody.Part?,
        fileExtensionData: MultipartBody.Part
    ): Observable<out ProfileViewState> =
        retrofit
            .create(ProfileApi::class.java)
            .editProfile(
                imageData = profileData,
                objectId = imageData,
                fileExtension = fileExtensionData
            )
            .map {
                ProfileViewState.EditProfileResult(it)
            }

}