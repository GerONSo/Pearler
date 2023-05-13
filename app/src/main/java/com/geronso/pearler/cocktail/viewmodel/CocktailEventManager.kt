package com.geronso.pearler.cocktail.viewmodel

import android.util.Log
import com.geronso.pearler.base.BaseEventManager
import com.geronso.pearler.cocktail.model.CocktailInteractor
import com.geronso.pearler.cocktail.model.data.CocktailData
import com.geronso.pearler.logging.Logger
import io.reactivex.ObservableSource
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CocktailEventManager @Inject constructor(
    private val cocktailInteractor: CocktailInteractor
) : BaseEventManager<CocktailViewModelEvent, CocktailViewState>() {

    var cocktailData: CocktailData? = null

    override fun onEvent(event: CocktailViewModelEvent): ObservableSource<out CocktailViewState> =
        when (event) {
            is CocktailViewModelEvent.GetCocktailById -> {
                cocktailInteractor.getCocktailById(event.id)
            }
        }.subscribeOn(Schedulers.io())

    override fun onNext(value: CocktailViewState) {
        if (value is CocktailViewState.CocktailResult) {
            cocktailData = value.cocktail.cocktail
        }
        viewEventObservable.postValue(value)
    }

    override fun onError(e: Throwable?) {
        Log.e(Logger.COCKTAIL, e.toString())
    }

    override fun onComplete() {}
}