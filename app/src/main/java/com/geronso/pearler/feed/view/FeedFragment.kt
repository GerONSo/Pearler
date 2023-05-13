package com.geronso.pearler.feed.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.geronso.pearler.R
import com.geronso.pearler.base.BaseViewItem
import com.geronso.pearler.base.baseItemDiffCallback
import com.geronso.pearler.base.doOnApplyWindowInsets
import com.geronso.pearler.base.getMainComponent
import com.geronso.pearler.base.showSnackbar
import com.geronso.pearler.cocktail.data.CocktailData
import com.geronso.pearler.databinding.FragmentHomeBinding
import com.geronso.pearler.feed.model.data.PearlData
import com.geronso.pearler.feed.model.data.mapProfileViewState
import com.geronso.pearler.feed.viewmodel.FeedEventManager
import com.geronso.pearler.feed.viewmodel.FeedViewModelEvent
import com.geronso.pearler.feed.viewmodel.FeedViewState
import com.geronso.pearler.main.MainActivity
import com.geronso.pearler.main.MainFragmentProvider
import com.geronso.pearler.main.MainViewModel
import com.geronso.pearler.profile.data.ProfileFragmentData
import com.geronso.pearler.widgets.Snackbar
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import javax.inject.Inject

class FeedFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val feedComponent
        get() = getMainComponent().feedComponent().create()
    private val feedEventManager: FeedEventManager by viewModels {
        feedComponent.viewModelsFactory()
    }
    private val mainViewModel: MainViewModel by viewModels {
        getMainComponent().viewModelsFactory()
    }
    @Inject
    lateinit var mainFragmentProvider: MainFragmentProvider

    private val feedAdapter: AsyncListDifferDelegationAdapter<BaseViewItem> = AsyncListDifferDelegationAdapter(
        AsyncDifferConfig.Builder(baseItemDiffCallback).build(),
        feedAdapterDelegate(
            openCocktail = this::openCocktail,
            openProfile = this::openProfile,
        ),
        feedEmptyAdapterDelegate()
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        feedComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        feedEventManager.viewEventObservable.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is FeedViewState.FeedResult -> {
                    feedAdapter.items =
                        if (viewState.pearls.isNotEmpty()) viewState.pearls.map(PearlData::mapProfileViewState)
                        else listOf(
                            FeedEmptyListItem(
                                getString(R.string.feed_empty_pearl_list_text)
                            )
                        )
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.feed.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = feedAdapter
        }
        binding.root.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                top = padding.top + insets.systemWindowInsetTop,
                bottom = padding.bottom + insets.systemWindowInsetBottom
            )
            insets
        }
        sendEvent(
            FeedViewModelEvent.GetFeed(
                accountId = mainViewModel.localId!!,
                limit = 10
            )
        )
    }

    private fun openCocktail(cocktailId: String) {
        val cocktailFragment =
            mainFragmentProvider.provideFragment(
                MainFragmentProvider.Fragments.COCKTAIL,
                data = CocktailData(cocktailId)
            )
        (requireActivity() as MainActivity).setCurrentFragment(
            cocktailFragment,
            addToBackstack = true
        )
    }

    private fun openProfile(profileId: String) {
        val profileFragment =
            mainFragmentProvider.provideFragment(
                MainFragmentProvider.Fragments.PROFILE,
                data = ProfileFragmentData(profileId)
            )
        (requireActivity() as MainActivity).setCurrentFragment(
            profileFragment,
            addToBackstack = true
        )
    }

    private fun sendEvent(event: FeedViewModelEvent) {
        feedEventManager.event(event)
    }
}