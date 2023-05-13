package com.geronso.pearler.search.viewmodel

import android.util.Log
import com.geronso.pearler.base.BaseEventManager
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.search.model.SearchInteractor
import com.geronso.pearler.search.model.data.SmallCocktailEntity
import io.reactivex.ObservableSource
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchEventManager @Inject constructor(
    private val searchInteractor: SearchInteractor
) : BaseEventManager<SearchViewModelEvent, SearchViewState>() {

    var allCocktails: List<SmallCocktailEntity> = listOf()

    override fun onEvent(event: SearchViewModelEvent): ObservableSource<out SearchViewState> =
        when (event) {
            is SearchViewModelEvent.GetAllCocktails -> {
                searchInteractor.getAllCocktails()
            }
        }.subscribeOn(Schedulers.io())

    override fun onNext(value: SearchViewState?) {
        if (value is SearchViewState.AllCocktailsResult) {
            allCocktails = value.allCocktails
        }
        viewEventObservable.postValue(value)
    }

    override fun onError(e: Throwable?) {
        Log.e(Logger.SEARCH, e.toString())
    }

    override fun onComplete() {}
}