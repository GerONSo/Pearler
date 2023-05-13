package com.geronso.pearler.friends.model

import android.util.Log
import com.geronso.pearler.friends.model.data.SubscriptionWrapper
import com.geronso.pearler.friends.viewmodel.FriendsViewState
import com.geronso.pearler.logging.Logger
import io.reactivex.Observable
import javax.inject.Inject
import retrofit2.Retrofit

class FriendsInteractor @Inject constructor(
    private val retrofit: Retrofit
) {

    fun getMyFriends(profileId: String): Observable<out FriendsViewState> {
        Log.d(Logger.FRIENDS, "My friends responsed")
        return retrofit
            .create(FriendsApi::class.java)
            .getMyFriends(profileId = profileId)
            .map {
                Log.d(Logger.FRIENDS, "My friends result received")
                FriendsViewState.MyFriendsResult(it.accounts)
            }
    }

    fun getAllUsersByPrefix(
        accountId: String,
        prefix: String,
        limit: Int
    ): Observable<out FriendsViewState> =
        retrofit
            .create(FriendsApi::class.java)
            .getAllUsersByPrefix(
                accountId,
                prefix.ifEmpty { null },
                limit
            )
            .map {
                FriendsViewState.AllUsersResult(it.accounts)
            }

    fun subscribe(subscriptionWrapper: SubscriptionWrapper): Observable<out FriendsViewState> =
        retrofit
            .create(FriendsApi::class.java)
            .subscribe(subscriptionWrapper)
            .map {
                FriendsViewState.SubscriptionResult(subscriptionWrapper)
            }

    fun unsubscribe(subscriptionWrapper: SubscriptionWrapper): Observable<out FriendsViewState> =
        retrofit
            .create(FriendsApi::class.java)
            .unsubscribe(subscriptionWrapper)
            .map {
                FriendsViewState.UnsubscriptionResult(subscriptionWrapper)
            }
}