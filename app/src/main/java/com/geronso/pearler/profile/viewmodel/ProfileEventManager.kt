package com.geronso.pearler.profile.viewmodel

import android.util.Log
import com.geronso.pearler.base.BaseEventManager
import com.geronso.pearler.base.ImageUtils
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.profile.model.ProfileInteractor
import com.google.gson.Gson
import io.reactivex.ObservableSource
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import javax.inject.Inject

class ProfileEventManager @Inject constructor(
    private val profileInteractor: ProfileInteractor
) : BaseEventManager<ProfileViewModelEvent, ProfileViewState>() {

    val gson = Gson()
    var isSubscribed: Boolean = false

    override fun onEvent(event: ProfileViewModelEvent): ObservableSource<out ProfileViewState> =
        when (event) {
            is ProfileViewModelEvent.GetSelfProfileById -> {
                profileInteractor.getSelfProfileById(event.id)
            }
            is ProfileViewModelEvent.GetProfileById -> {
                profileInteractor.getProfileById(event.id)
            }
            is ProfileViewModelEvent.Subscribe -> {
                profileInteractor.subscribe(event.subscriptionWrapper)
            }
            is ProfileViewModelEvent.Unsubscribe -> {
                profileInteractor.unsubscribe(event.subscriptionWrapper)
            }
            is ProfileViewModelEvent.EditProfileData -> {
                val profileData = gson.toJson(event.editProfileWrapper)
                profileInteractor.editProfileData(
                    imageData =
                    if (event.wasBitmapChanged) {
                        ImageUtils.compressImageToMultipart(
                            context = event.context,
                            bitmap = event.bitmap
                        )
                    } else null,
                    profileData = MultipartBody.Part.createFormData(
                        DATA_MULTIPART_TAG,
                        profileData
                    ),
                    fileExtensionData = MultipartBody.Part.createFormData(
                        FILE_EXTENSION_MULTIPART_TAG,
                        event.fileExtension
                    )
                )
            }
        }.subscribeOn(Schedulers.io())

    override fun onNext(value: ProfileViewState?) {
        when (value) {
            is ProfileViewState.SubscriptionResult -> {
                isSubscribed = true
            }
            is ProfileViewState.UnsubscriptionResult -> {
                isSubscribed = false
            }
            is ProfileViewState.ProfileResultData -> {
                isSubscribed = value.profileData.subscriptions_statistics.is_subscribed
            }
        }
        viewEventObservable.postValue(value)
    }

    override fun onError(e: Throwable?) {
        Log.e(Logger.PROFILE, e.toString())
    }

    override fun onComplete() {}

    private companion object {

        const val DATA_MULTIPART_TAG = "data"
        const val FILE_EXTENSION_MULTIPART_TAG = "file_extension"
    }
}