package com.geronso.pearler.search.viewmodel

import com.geronso.pearler.base.BaseViewModelEvent
import com.geronso.pearler.base.BaseViewState
import com.geronso.pearler.search.model.data.SmallCocktailEntity

sealed class SearchViewState : BaseViewState {
    data class AllCocktailsResult(val allCocktails: List<SmallCocktailEntity>): SearchViewState()
}

sealed class SearchViewModelEvent : BaseViewModelEvent {
    object GetAllCocktails : SearchViewModelEvent()
}