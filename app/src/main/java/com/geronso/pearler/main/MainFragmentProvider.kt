package com.geronso.pearler.main

import androidx.fragment.app.Fragment
import com.geronso.pearler.cocktail.data.CocktailData
import com.geronso.pearler.friends.view.FriendsFragment
import com.geronso.pearler.cocktail.view.CocktailFragment
import com.geronso.pearler.feed.view.FeedFragment
import com.geronso.pearler.main.data.FragmentData
import com.geronso.pearler.pearl.data.PearlFragmentData
import com.geronso.pearler.pearl.view.PearlFragment
import com.geronso.pearler.profile.data.ProfileFragmentData
import com.geronso.pearler.profile.view.ProfileFragment
import com.geronso.pearler.search.view.SearchFragment
import javax.inject.Inject

class MainFragmentProvider @Inject constructor() {

    fun provideFragment(fragment: Fragments, data: FragmentData? = null): Fragment =
        when (fragment) {
            Fragments.FEED -> FeedFragment()
            Fragments.SEARCH -> SearchFragment()
            Fragments.FRIENDS -> FriendsFragment()
            Fragments.PROFILE -> ProfileFragment(data as ProfileFragmentData)
            Fragments.COCKTAIL -> CocktailFragment(data as CocktailData)
            Fragments.PEARL -> PearlFragment(data as? PearlFragmentData)
        }

    enum class Fragments {
        FEED, SEARCH, FRIENDS, PROFILE, COCKTAIL, PEARL
    }
}