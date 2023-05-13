package com.geronso.pearler.cocktail.view

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.base.baseItemDiffCallback
import com.geronso.pearler.base.doOnApplyWindowInsets
import com.geronso.pearler.base.getMainComponent
import com.geronso.pearler.base.round
import com.geronso.pearler.cocktail.data.CocktailData
import com.geronso.pearler.cocktail.viewmodel.CocktailEventManager
import com.geronso.pearler.cocktail.viewmodel.CocktailViewModelEvent
import com.geronso.pearler.cocktail.viewmodel.CocktailViewState
import com.geronso.pearler.databinding.FragmentCocktailBinding
import com.geronso.pearler.feed.model.data.PearlData
import com.geronso.pearler.feed.model.data.mapProfileViewState
import com.geronso.pearler.feed.view.FeedEmptyListItem
import com.geronso.pearler.feed.view.feedAdapterDelegate
import com.geronso.pearler.feed.view.feedEmptyAdapterDelegate
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.main.MainActivity
import com.geronso.pearler.main.MainFragmentProvider
import com.geronso.pearler.pearl.data.PearlFragmentData
import com.geronso.pearler.pearl.view.PearlFragment
import com.geronso.pearler.profile.data.ProfileFragmentData
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.squareup.picasso.Picasso
import javax.inject.Inject

class CocktailFragment(
    private val cocktailData: CocktailData
) : Fragment() {

    private val cocktailComponent
        get() = getMainComponent().cocktailComponent().create()

    private val cocktailEventManager: CocktailEventManager by viewModels {
        cocktailComponent.viewModelsFactory()
    }

    private lateinit var binding: FragmentCocktailBinding

    @Inject
    lateinit var mainFragmentProvider: MainFragmentProvider

    private val cocktailPearlsAdapter: AsyncListDifferDelegationAdapter<BaseViewItem> =
        AsyncListDifferDelegationAdapter(
            AsyncDifferConfig.Builder(baseItemDiffCallback).build(),
            feedAdapterDelegate(
                openProfile = this::openProfile
            ),
            feedEmptyAdapterDelegate()
        )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cocktailComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCocktailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
        binding.root.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                top = padding.top + insets.systemWindowInsetTop,
                bottom = padding.bottom + insets.systemWindowInsetBottom
            )
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        sendEvent(CocktailViewModelEvent.GetCocktailById(cocktailData.cocktailId))
    }

    private fun setListeners() {
        binding.cocktailPearls.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = cocktailPearlsAdapter
        }
        binding.pearlButton.render {
            this.copy(
                text = getString(R.string.pearl)
            )
        }
        binding.descriptionPlaceholder.text = cocktailEventManager.cocktailData?.description ?: DEFAULT_DESCRIPTION
        binding.recipePlaceholder.text = cocktailEventManager.cocktailData?.recipe ?: DEFAULT_RECIPE
        cocktailEventManager.viewEventObservable.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is CocktailViewState.CocktailResult -> {
                    Log.d(Logger.COCKTAIL, "received cocktail for id: ${cocktailData.cocktailId}")
                    binding.cocktailLoaderBackground.visibility = View.GONE
                    val cocktailData = viewState.cocktail
                    binding.cocktailName.text = cocktailData.cocktail.name
                    binding.cocktailRating.text =
                        "${cocktailData.pearls_statistics.average_rating.round(2)}"
                    val pearlCount = cocktailData.pearls_statistics.pearls_amount
                    val pearlString = pearlCount.toString() + if (pearlCount == 1) {
                        " pearl"
                    } else {
                        " pearls"
                    }
                    binding.cocktailPearlsCount.text = pearlString
                    binding.stars.rating = cocktailData.pearls_statistics.average_rating.round(0)
                    cocktailPearlsAdapter.items =
                        if (viewState.cocktail.pearls.isNotEmpty()) viewState.cocktail.pearls.map(PearlData::mapProfileViewState)
                        else listOf(
                            FeedEmptyListItem(
                                getString(R.string.empty_pearl_list_text)
                            )
                        )
                    binding.descriptionPlaceholder.text = cocktailData.cocktail.description
                    binding.recipePlaceholder.text = cocktailData.cocktail.recipe
                    binding.pearlButton.setOnClickListener {
                        (requireActivity() as MainActivity).setPearlFragment(
                            PearlFragmentData(
                                cocktailId = cocktailData.cocktail.id,
                                cocktailName = cocktailData.cocktail.name
                            )
                        )
                    }
                    val imageAddress = cocktailData.cocktail.image_url
                    Log.d(Logger.COCKTAIL, imageAddress)
                    if (imageAddress.isNotEmpty()) {
                        Picasso
                            .get()
                            .load(imageAddress)
                            .placeholder(R.drawable.cocktail_placeholder_horizontal)
                            .error(R.drawable.cocktail_placeholder_horizontal)
                            .into(binding.cocktailImage)
                    } else {
                        Picasso
                            .get()
                            .load(R.drawable.cocktail_placeholder_horizontal)
                            .into(binding.cocktailImage)
                    }
                }
            }
        }
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

    private fun sendEvent(event: CocktailViewModelEvent) {
        cocktailEventManager.event(event)
    }

    companion object {

        const val DEFAULT_DESCRIPTION = "Seems like creator was too busy to leave any description :("
        const val DEFAULT_RECIPE = "Seems like creator was too busy to leave any recipe :("
    }
}