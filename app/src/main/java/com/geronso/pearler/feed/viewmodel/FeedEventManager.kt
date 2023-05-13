package com.geronso.pearler.feed.viewmodel

import android.util.Log
import com.geronso.pearler.base.BaseEventManager
import com.geronso.pearler.feed.model.FeedInteractor
import com.geronso.pearler.logging.Logger
import io.reactivex.ObservableSource
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FeedEventManager @Inject constructor(
    private val feedInteractor: FeedInteractor
) : BaseEventManager<FeedViewModelEvent, FeedViewState>() {
    override fun onEvent(event: FeedViewModelEvent): ObservableSource<out FeedViewState> =
        when (event) {
            is FeedViewModelEvent.GetFeed -> {
                feedInteractor.getFeed(
                    event.accountId,
                    event.limit
                )
            }
        }.subscribeOn(Schedulers.io())

    override fun onNext(value: FeedViewState) {
        viewEventObservable.postValue(value)
    }

    override fun onError(e: Throwable?) {
        viewEventObservable.postValue(FeedViewState.FeedResult(listOf()))
        Log.e(Logger.FEED, e.toString())
    }

    override fun onComplete() {}
}