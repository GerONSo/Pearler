package com.geronso.pearler.feed.viewmodel

import com.geronso.pearler.base.BaseViewModelEvent
import com.geronso.pearler.base.BaseViewState
import com.geronso.pearler.feed.model.data.FeedIdWrapper
import com.geronso.pearler.feed.model.data.PearlData
import com.geronso.pearler.profile.model.data.ProfileIdWrapper

sealed class FeedViewState : BaseViewState {
    data class FeedResult(val pearls: List<PearlData>) : FeedViewState()
}

sealed class FeedViewModelEvent : BaseViewModelEvent {
    data class GetFeed(
        val accountId: String,
        val limit: Int
    ) : FeedViewModelEvent()
}