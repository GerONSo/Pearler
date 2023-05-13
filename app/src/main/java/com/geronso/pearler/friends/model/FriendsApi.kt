package com.geronso.pearler.friends.model

import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.base.QueryParameters
import com.geronso.pearler.friends.model.data.AllUsersPrefixRequest
import com.geronso.pearler.friends.model.data.AllUsersPrefixResult
import com.geronso.pearler.friends.model.data.MyFriendsResultData
import com.geronso.pearler.friends.model.data.SubscriptionIdWrapper
import com.geronso.pearler.friends.model.data.SubscriptionWrapper
import com.geronso.pearler.profile.model.data.ProfileIdWrapper
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendsApi {
    @GET("Account/GetAllByNamePrefix/${NetworkDataProvider.API_VERSION}")
    fun getAllUsersByPrefix(
        @Query(QueryParameters.ACCOUNT_ID)
        accountId: String,
        @Query(QueryParameters.PREFIX)
        prefix: String?,
        @Query(QueryParameters.LIMIT)
        limit: Int
    ): Observable<AllUsersPrefixResult>

    @GET("Subscription/GetAccountSubscriptions/${NetworkDataProvider.API_VERSION}")
    fun getMyFriends(
        @Query(QueryParameters.ID)
        profileId: String
    ): Observable<MyFriendsResultData>

    @POST("Subscription/Create/${NetworkDataProvider.API_VERSION}")
    fun subscribe(@Body subscriptionWrapper: SubscriptionWrapper): Observable<SubscriptionIdWrapper>

    @POST("Subscription/Delete/${NetworkDataProvider.API_VERSION}")
    fun unsubscribe(@Body subscriptionWrapper: SubscriptionWrapper): Observable<SubscriptionIdWrapper>
}