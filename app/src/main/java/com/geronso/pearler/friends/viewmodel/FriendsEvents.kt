package com.geronso.pearler.friends.viewmodel

import com.geronso.pearler.base.BaseViewModelEvent
import com.geronso.pearler.base.BaseViewState
import com.geronso.pearler.friends.model.data.SubscriptionWrapper
import com.geronso.pearler.profile.model.data.ProfileData

sealed class FriendsViewState : BaseViewState {
    data class MyFriendsResult(val myFriends: List<ProfileData>) : FriendsViewState()
    data class AllUsersResult(val allUsers: List<ProfileData>) : FriendsViewState()
    data class SubscriptionResult(val subscriptionWrapper: SubscriptionWrapper) : FriendsViewState()
    data class UnsubscriptionResult(val subscriptionWrapper: SubscriptionWrapper) : FriendsViewState()
}

sealed class FriendsViewModelEvent : BaseViewModelEvent {
    data class GetMyFriends(val profileId: String) : FriendsViewModelEvent()
    data class GetAllUsersByPrefix(
        val accountId: String,
        val prefix: String,
        val limit: Int
    ) : FriendsViewModelEvent()

    data class Subscribe(val subscriptionWrapper: SubscriptionWrapper) : FriendsViewModelEvent()
    data class Unsubscribe(val subscriptionWrapper: SubscriptionWrapper) : FriendsViewModelEvent()
}