package com.geronso.pearler.main.di

import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.base.OkHttpProvider
import com.geronso.pearler.cocktail.di.CocktailComponent
import com.geronso.pearler.feed.di.FeedComponent
import com.geronso.pearler.friends.di.FriendsComponent
import com.geronso.pearler.login.di.LoginComponent
import com.geronso.pearler.profile.di.ProfileComponent
import com.geronso.pearler.registration.di.RegistrationComponent
import com.geronso.pearler.search.di.SearchComponent
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module(subcomponents = [
    LoginComponent::class,
    RegistrationComponent::class,
    SearchComponent::class,
    CocktailComponent::class,
    ProfileComponent::class,
    FriendsComponent::class,
    FeedComponent::class
])
class MainSubcomponentsModule {
    @Provides
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkDataProvider.provideAddressProd())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(OkHttpProvider.provideClient())
            .build()
    }
}