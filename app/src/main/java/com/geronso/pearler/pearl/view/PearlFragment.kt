package com.geronso.pearler.pearl.view

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.geronso.pearler.R
import com.geronso.pearler.base.*
import com.geronso.pearler.databinding.FragmentPearlBinding
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.main.MainFragmentProvider
import com.geronso.pearler.main.MainViewModel
import com.geronso.pearler.main.di.MyApplication
import com.geronso.pearler.pearl.data.PearlFragmentData
import com.geronso.pearler.pearl.model.data.PublishPearlData
import com.geronso.pearler.pearl.viewmodel.PearlEventManager
import com.geronso.pearler.pearl.viewmodel.PearlViewModelEvent
import com.geronso.pearler.pearl.viewmodel.PearlViewState
import com.geronso.pearler.search.viewmodel.SearchEventManager
import com.geronso.pearler.search.viewmodel.SearchViewModelEvent
import com.geronso.pearler.search.viewmodel.SearchViewState
import com.geronso.pearler.widgets.LoadingButton
import com.squareup.picasso.Picasso
import javax.inject.Inject

class PearlFragment(
    private val data: PearlFragmentData? = null,
) : Fragment(), OnKeyboardVisibilityListener {

    private val pearlComponent
        get() = getMainComponent().pearlComponent().create()

    private lateinit var binding: FragmentPearlBinding

    private val pearlEventManager: PearlEventManager by viewModels {
        pearlComponent.viewModelsFactory()
    }
    private val searchEventManager: SearchEventManager by viewModels {
        pearlComponent.searchViewModelsFactory()
    }
    private val mainViewModel: MainViewModel by viewModels {
        (requireActivity().applicationContext as MyApplication).mainComponent.viewModelsFactory()
    }
    private var currentUri: Uri? = null

    @Inject
    lateinit var pearlVariantsAdapter: PearlVariantsAdapter

    @Inject
    lateinit var mainFragmentProvider: MainFragmentProvider

    private var cocktailId: String = ""
    private val getImageResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            currentUri = uri
            Picasso
                .get()
                .load(uri)
                .transform(OrientationTransformation(uri = uri, context = requireContext()))
                .fit()
                .centerCrop()
                .into(binding.pearlPhoto)
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pearlComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPearlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        OnKeyboardVisibilityImpl.setKeyboardVisibilityListener(requireActivity(), this)
        if (data != null) {
            cocktailId = data.cocktailId
            binding.pearlCocktailName.setText(data.cocktailName)
        }
        binding.addPearlCocktailVariants.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = pearlVariantsAdapter.also {
                it.onSelectedCocktail = { cocktail ->
                    binding.pearlCocktailName.setText(cocktail.name)
                    cocktailId = cocktail.id
                    this@PearlFragment.hideKeyboard()
                }
            }
        }
        binding.pearlPublish.render {
            LoadingButton.State.DEFAULT.copy(
                text = getString(R.string.publish)
            )
        }
        binding.scrollView.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                top = padding.top + insets.systemWindowInsetTop,
                bottom = padding.bottom + insets.systemWindowInsetBottom
            )
            insets
        }
        binding.parentLayout.doOnApplyWindowInsets { view, insets, padding ->
            insets
        }
        sendSearchEvent(SearchViewModelEvent.GetAllCocktails)
    }

    override fun onDestroyView() {
        val parentView =
            (requireActivity().findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        parentView.viewTreeObserver.removeOnGlobalLayoutListener {}
        super.onDestroyView()
    }

    private fun setListeners() {
        binding.pearlPhoto.setOnClickListener {
            getImageResult.launch("image/*")
        }
        binding.pearlCocktailName.doOnTextChanged { text, start, before, count ->
            pearlVariantsAdapter.setData(searchEventManager.allCocktails.filter { cocktail ->
                cocktail.name.uppercase().startsWith(text.toString().uppercase())
            })
        }
        binding.pearlPublish.setOnClickListener {
            Log.d(Logger.PEARL, "cocktailName: ${binding.pearlCocktailName.text}")
            Log.d(Logger.PEARL, "cocktailId: $cocktailId")
            Log.d(Logger.PEARL, "stars: ${binding.pearlRating.rating}")
            if (cocktailId == "") {
                showSnackbar("Cocktail not selected")
                return@setOnClickListener
            }
            if (binding.pearlRating.rating == 0f) {
                showSnackbar("Cocktail not rated")
                return@setOnClickListener
            }
            binding.pearlPublish.render {
                this.copy(isLoading = true)
            }
            val extension =
                if (currentUri != null) {
                    FileUtils.getMimeType(requireContext(), currentUri!!) ?: ""
                } else ""
            sendEvent(
                PearlViewModelEvent.PublishPearl(
                    PublishPearlData(
                        account_id = mainViewModel.localId!!,
                        cocktail_id = cocktailId,
                        grade = binding.pearlRating.rating.toInt(),
                        review = binding.pearlReview.text.toString()
                    ),
                    requireContext(),
                    (binding.pearlPhoto.drawable as BitmapDrawable).bitmap,
                    extension
                )
            )
        }
        pearlEventManager.viewEventObservable.observe(viewLifecycleOwner) {
            when (it) {
                is PearlViewState.PublishPearlResult -> {
                    Log.d(Logger.PEARL, "result: ${it.publishPearlResult.id}")
                    binding.pearlPublish.render {
                        this.copy(isLoading = false)
                    }
                    requireActivity().onBackPressed()
                }
            }
        }
        searchEventManager.viewEventObservable.observe(viewLifecycleOwner) {
            when (it) {
                is SearchViewState.AllCocktailsResult -> {
                    pearlVariantsAdapter.setData(it.allCocktails)
                }
            }
        }
    }

    private fun scaleLayoutIfNeed(show: Boolean) {
        if (show) {
            binding.parentLayout.transitionToState(R.id.end)
        } else {
            binding.parentLayout.transitionToState(R.id.start)
        }
    }

    override fun onVisibilityChanged(visible: Boolean) {
        if (!visible) {
            scaleLayoutIfNeed(show = false)
            binding.pearlCocktailName.clearFocus()
        } else if (binding.pearlCocktailName.hasFocus()) {
            scaleLayoutIfNeed(show = true)
        } else if (binding.pearlReview.hasFocus()) {
            binding.parentLayout.transitionToState(R.id.reviewUp)
        }
    }

    private fun sendEvent(event: PearlViewModelEvent) {
        pearlEventManager.event(event)
    }

    private fun sendSearchEvent(event: SearchViewModelEvent) {
        searchEventManager.event(event)
    }

}