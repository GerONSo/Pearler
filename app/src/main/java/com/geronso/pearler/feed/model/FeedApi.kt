package com.geronso.pearler.feed.model

import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.base.QueryParameters
import com.geronso.pearler.feed.model.data.FeedIdWrapper
import com.geronso.pearler.feed.model.data.PearlDataList
import com.geronso.pearler.profile.model.data.ProfileIdWrapper
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FeedApi {
    @GET("Account/GetFeed/${NetworkDataProvider.API_VERSION}")
    fun getFeed(
        @Query(QueryParameters.ACCOUNT_ID)
        accountId: String,
        @Query(QueryParameters.LIMIT)
        limit: Int
    ): Observable<PearlDataList>
}