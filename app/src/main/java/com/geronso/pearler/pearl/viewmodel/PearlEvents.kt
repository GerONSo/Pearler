package com.geronso.pearler.pearl.viewmodel

import android.content.Context
import android.graphics.Bitmap
import com.geronso.pearler.base.BaseViewModelEvent
import com.geronso.pearler.base.BaseViewState
import com.geronso.pearler.pearl.model.data.PublishPearlData
import com.geronso.pearler.pearl.model.data.PublishPearlResultData
import com.geronso.pearler.profile.model.data.ImageIdResponse

sealed class PearlViewState : BaseViewState {
    data class PublishPearlResult(val publishPearlResult: PublishPearlResultData): PearlViewState()
}

sealed class PearlViewModelEvent : BaseViewModelEvent {
    data class PublishPearl(
        val publishPearl: PublishPearlData,
        val context: Context,
        val bitmap: Bitmap,
        val fileExtension: String,
    ) : PearlViewModelEvent()
}