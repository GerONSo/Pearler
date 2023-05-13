package com.geronso.pearler.friends.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.geronso.pearler.base.BaseEventManager
import com.geronso.pearler.friends.model.FriendsInteractor
import com.geronso.pearler.friends.view.AllUsersViewItem
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.profile.model.data.ProfileData
import com.geronso.pearler.profile.model.data.mapAllUsersViewItem
import io.reactivex.ObservableSource
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FriendsEventManager @Inject constructor(
    private val friendsInteractor: FriendsInteractor
) : BaseEventManager<FriendsViewModelEvent, FriendsViewState>() {

    var myFriends = MutableLiveData(listOf<ProfileData>())
    var allUsers = MutableLiveData(listOf<AllUsersViewItem>())

    override fun onEvent(event: FriendsViewModelEvent): ObservableSource<out FriendsViewState> =
        when (event) {
            is FriendsViewModelEvent.GetMyFriends -> {
                friendsInteractor.getMyFriends(event.profileId)
            }
            is FriendsViewModelEvent.GetAllUsersByPrefix -> {
                friendsInteractor.getAllUsersByPrefix(
                    accountId = event.accountId,
                    prefix = event.prefix,
                    limit = event.limit
                )
            }
            is FriendsViewModelEvent.Subscribe -> {
                setUsersAddingLoading(
                    id = event.subscriptionWrapper.target_id,
                    isLoading = true,
                    isAdded = false
                )
                friendsInteractor.subscribe(event.subscriptionWrapper)
            }
            is FriendsViewModelEvent.Unsubscribe -> {
                setUsersAddingLoading(
                    id = event.subscriptionWrapper.target_id,
                    isLoading = true,
                    isAdded = true
                )
                friendsInteractor.unsubscribe(event.subscriptionWrapper)
            }
        }.subscribeOn(Schedulers.io())

    override fun onNext(value: FriendsViewState?) {
        when (value) {
            is FriendsViewState.MyFriendsResult -> {
                myFriends.value = value.myFriends
            }
            is FriendsViewState.AllUsersResult -> {
                allUsers.value = value.allUsers.map(ProfileData::mapAllUsersViewItem)
            }
            is FriendsViewState.SubscriptionResult -> {
                setUsersAddingLoading(
                    id = value.subscriptionWrapper.target_id,
                    isLoading = false,
                    isAdded = true
                )
            }
            is FriendsViewState.UnsubscriptionResult -> {
                setUsersAddingLoading(
                    id = value.subscriptionWrapper.target_id,
                    isLoading = false,
                    isAdded = false
                )
            }
        }
        viewEventObservable.postValue(value)
    }

    override fun onError(e: Throwable?) {
        Log.e(Logger.FRIENDS, e.toString())
    }

    override fun onComplete() {}

    private fun setUsersAddingLoading(id: String, isLoading: Boolean, isAdded: Boolean) {
        allUsers.postValue(allUsers.value?.map {
            if (it.id == id) {
                allUsers.value?.find { findedItem ->
                    findedItem.id == id
                }?.copy(
                    isAddFriendLoading = isLoading,
                    isFriendAdded = isAdded
                ) ?: it
            } else {
                it
            }
        })
    }
}