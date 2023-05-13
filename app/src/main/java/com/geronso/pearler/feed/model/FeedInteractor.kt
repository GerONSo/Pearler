package com.geronso.pearler.feed.model

import com.geronso.pearler.feed.model.data.FeedIdWrapper
import com.geronso.pearler.feed.viewmodel.FeedViewState
import com.geronso.pearler.profile.model.ProfileApi
import com.geronso.pearler.profile.model.data.ProfileIdWrapper
import com.geronso.pearler.profile.viewmodel.ProfileViewState
import io.reactivex.Observable
import javax.inject.Inject
import retrofit2.Retrofit

class FeedInteractor @Inject constructor(
    private val retrofit: Retrofit
) {
    fun getFeed(accountId: String, limit: Int): Observable<out FeedViewState> =
        retrofit
            .create(FeedApi::class.java)
            .getFeed(
                accountId = accountId,
                limit = limit
            )
            .map {
                FeedViewState.FeedResult(it.pearls)
            }
}