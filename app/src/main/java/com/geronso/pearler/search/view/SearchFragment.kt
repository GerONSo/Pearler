package com.geronso.pearler.search.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geronso.pearler.R
import com.geronso.pearler.base.doOnApplyWindowInsets
import com.geronso.pearler.base.getMainComponent
import com.geronso.pearler.cocktail.data.CocktailData
import com.geronso.pearler.databinding.FragmentSearchBinding
import com.geronso.pearler.main.MainActivity
import com.geronso.pearler.main.MainFragmentProvider
import com.geronso.pearler.search.viewmodel.SearchEventManager
import com.geronso.pearler.search.viewmodel.SearchViewModelEvent
import com.geronso.pearler.search.viewmodel.SearchViewState
import javax.inject.Inject

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val searchComponent
        get() = getMainComponent().searchComponent().create()

    private val searchEventManager: SearchEventManager by viewModels {
        searchComponent.viewModelsFactory()
    }

    var searchCocktailsAdapter: SearchCocktailsAdapter = SearchCocktailsAdapter()

    @Inject
    lateinit var mainFragmentProvider: MainFragmentProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)
        searchComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
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
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            searchCocktailsAdapter.setData(searchEventManager.allCocktails.filter { cocktail ->
                cocktail.name.uppercase().startsWith(text.toString().uppercase())
            })
        }
        binding.cocktailsRecyclerView.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = searchCocktailsAdapter.also {
                it.onSelectedCocktail = { smallCocktail ->
                    val cocktailFragment =
                        mainFragmentProvider.provideFragment(
                            MainFragmentProvider.Fragments.COCKTAIL,
                            data = CocktailData(smallCocktail.id)
                        )
                    (requireActivity() as MainActivity).setCurrentFragment(
                        cocktailFragment,
                        addToBackstack = true
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sendEvent(SearchViewModelEvent.GetAllCocktails)
    }

    private fun setListeners() {
        searchEventManager.viewEventObservable.observe(viewLifecycleOwner) {
            when (it) {
                is SearchViewState.AllCocktailsResult -> {
                    searchCocktailsAdapter.setData(it.allCocktails)
                }
            }
        }
    }

    private fun sendEvent(event: SearchViewModelEvent) {
        searchEventManager.event(event)
    }
}