package com.geronso.pearler.main.di

import android.app.Application
import com.geronso.pearler.base.ActivityRouter
import com.geronso.pearler.base.FirebaseAuthProvider
import com.geronso.pearler.cocktail.di.CocktailComponent
import com.geronso.pearler.feed.di.FeedComponent
import com.geronso.pearler.friends.di.FriendsComponent
import com.geronso.pearler.login.di.LoginComponent
import com.geronso.pearler.login.di.LoginEventManagerFactory
import com.geronso.pearler.main.MainActivity
import com.geronso.pearler.pearl.di.PearlComponent
import com.geronso.pearler.profile.di.ProfileComponent
import com.geronso.pearler.registration.di.RegistrationComponent
import com.geronso.pearler.search.di.SearchComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainSubcomponentsModule::class])
interface MainComponent {
    fun inject(activity: MainActivity)

    fun viewModelsFactory(): MainViewModelFactory

    fun loginComponent(): LoginComponent.Factory
    fun registrationComponent(): RegistrationComponent.Factory
    fun searchComponent(): SearchComponent.Factory
    fun cocktailComponent(): CocktailComponent.Factory
    fun profileComponent(): ProfileComponent.Factory
    fun friendsComponent(): FriendsComponent.Factory
    fun pearlComponent(): PearlComponent.Factory
    fun feedComponent(): FeedComponent.Factory
}

class MyApplication: Application() {
    val mainComponent: MainComponent = DaggerMainComponent.create()
}