package com.geronso.pearler.search.model

import com.geronso.pearler.base.EmptyClass
import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.search.model.data.CocktailList
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SearchApi {
    @GET("Cocktail/GetAll/${NetworkDataProvider.API_VERSION}")
    fun getAllCocktails(): Observable<CocktailList>
}