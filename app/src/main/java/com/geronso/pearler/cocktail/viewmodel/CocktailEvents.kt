package com.geronso.pearler.cocktail.viewmodel

import com.geronso.pearler.base.BaseViewModelEvent
import com.geronso.pearler.base.BaseViewState
import com.geronso.pearler.cocktail.model.data.FullCocktailData

sealed class CocktailViewState : BaseViewState {
    data class CocktailResult(val cocktail: FullCocktailData) : CocktailViewState()
}

sealed class CocktailViewModelEvent : BaseViewModelEvent {
    data class GetCocktailById(val id: String) : CocktailViewModelEvent()
}