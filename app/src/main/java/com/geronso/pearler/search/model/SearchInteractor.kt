package com.geronso.pearler.search.model

import com.geronso.pearler.base.EmptyClass
import com.geronso.pearler.registration.model.RegistrationApi
import com.geronso.pearler.registration.viewmodel.RegistrationViewState
import com.geronso.pearler.search.viewmodel.SearchViewState
import io.reactivex.Observable
import javax.inject.Inject
import retrofit2.Retrofit

class SearchInteractor @Inject constructor(
    private val retrofit: Retrofit,
) {
    fun getAllCocktails(): Observable<out SearchViewState> =
        retrofit
            .create(SearchApi::class.java)
            .getAllCocktails()
            .map {
                SearchViewState.AllCocktailsResult(it.cocktails)
            }
}