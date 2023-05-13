package com.geronso.pearler.cocktail.model

import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.base.QueryParameters
import com.geronso.pearler.cocktail.model.data.CocktailIdWrapper
import com.geronso.pearler.cocktail.model.data.FullCocktailData
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CocktailApi {
    @GET("Cocktail/FullView/${NetworkDataProvider.API_VERSION}")
    fun getCocktailById(
        @Query(QueryParameters.ID)
        cocktailId: String
    ): Observable<FullCocktailData>
}