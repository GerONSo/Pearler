package com.geronso.pearler.cocktail.model

import com.geronso.pearler.cocktail.model.data.CocktailData
import com.geronso.pearler.cocktail.model.data.CocktailIdWrapper
import com.geronso.pearler.cocktail.viewmodel.CocktailViewState
import com.geronso.pearler.search.model.SearchApi
import com.geronso.pearler.search.viewmodel.SearchViewState
import io.reactivex.Observable
import javax.inject.Inject
import retrofit2.Retrofit

class CocktailInteractor @Inject constructor(
    private val retrofit: Retrofit
) {

    fun getCocktailById(id: String): Observable<out CocktailViewState> =
        retrofit
            .create(CocktailApi::class.java)
            .getCocktailById(
                cocktailId = id
            )
            .map {
                CocktailViewState.CocktailResult(it)
            }
}