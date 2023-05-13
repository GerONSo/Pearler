package com.geronso.pearler.profile.viewmodel

import android.content.Context
import android.graphics.Bitmap
import com.geronso.pearler.base.BaseViewModelEvent
import com.geronso.pearler.base.BaseViewState
import com.geronso.pearler.friends.model.data.SubscriptionIdWrapper
import com.geronso.pearler.friends.model.data.SubscriptionWrapper
import com.geronso.pearler.login.model.data.LocalIdData
import com.geronso.pearler.profile.model.data.EditProfileWrapper
import com.geronso.pearler.profile.model.data.FullGeneralProfileData
import com.geronso.pearler.profile.model.data.FullProfileData
import com.geronso.pearler.profile.model.data.GeneralProfileIdWrapper
import com.geronso.pearler.profile.model.data.ImageIdResponse
import com.geronso.pearler.profile.model.data.ProfileIdWrapper

sealed class ProfileViewState : BaseViewState {
    data class SelfProfileResultData(val profileData: FullProfileData) : ProfileViewState()
    data class ProfileResultData(val profileData: FullGeneralProfileData) : ProfileViewState()
    data class SubscriptionResult(val subscriptionIdWrapper: SubscriptionIdWrapper) :
        ProfileViewState()

    data class UnsubscriptionResult(val subscriptionIdWrapper: SubscriptionIdWrapper) :
        ProfileViewState()

    data class EditProfileResult(val id: LocalIdData) : ProfileViewState()
    data class EditAvatarResult(val id: ImageIdResponse) : ProfileViewState()
}

sealed class ProfileViewModelEvent : BaseViewModelEvent {
    data class GetSelfProfileById(val id: String) : ProfileViewModelEvent()
    data class GetProfileById(val id: GeneralProfileIdWrapper) : ProfileViewModelEvent()
    data class Subscribe(val subscriptionWrapper: SubscriptionWrapper) : ProfileViewModelEvent()
    data class Unsubscribe(val subscriptionWrapper: SubscriptionWrapper) : ProfileViewModelEvent()
    data class EditProfileData(
        val editProfileWrapper: EditProfileWrapper,
        val context: Context,
        val bitmap: Bitmap,
        val fileExtension: String,
        val wasBitmapChanged: Boolean,
    ) : ProfileViewModelEvent()
}