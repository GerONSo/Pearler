package com.geronso.pearler.profile.view

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.geronso.pearler.R
import com.geronso.pearler.base.Activities
import com.geronso.pearler.base.ActivityRouter
import com.geronso.pearler.base.BaseViewItem
import com.geronso.pearler.base.FileUtils
import com.geronso.pearler.base.FirebaseAuthProvider
import com.geronso.pearler.base.OrientationTransformation
import com.geronso.pearler.base.baseItemDiffCallback
import com.geronso.pearler.base.color
import com.geronso.pearler.base.doOnApplyWindowInsets
import com.geronso.pearler.base.getMainComponent
import com.geronso.pearler.base.hideKeyboard
import com.geronso.pearler.cocktail.data.CocktailData
import com.geronso.pearler.databinding.FragmentProfileBinding
import com.geronso.pearler.feed.model.data.PearlData
import com.geronso.pearler.feed.model.data.mapNoProfileViewState
import com.geronso.pearler.feed.view.FeedEmptyListItem
import com.geronso.pearler.feed.view.feedEmptyAdapterDelegate
import com.geronso.pearler.friends.model.data.SubscriptionWrapper
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.main.MainActivity
import com.geronso.pearler.main.MainFragmentProvider
import com.geronso.pearler.main.MainViewModel
import com.geronso.pearler.profile.data.ProfileFragmentData
import com.geronso.pearler.profile.model.data.EditProfileWrapper
import com.geronso.pearler.profile.model.data.GeneralProfileIdWrapper
import com.geronso.pearler.profile.viewmodel.ProfileEventManager
import com.geronso.pearler.profile.viewmodel.ProfileViewModelEvent
import com.geronso.pearler.profile.viewmodel.ProfileViewState
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.squareup.picasso.Picasso
import java.io.File
import javax.inject.Inject

class ProfileFragment(
    val profileData: ProfileFragmentData
) : Fragment() {

    private val profileComponent
        get() = getMainComponent().profileComponent().create()
    private val profileEventManager: ProfileEventManager by viewModels {
        profileComponent.viewModelsFactory()
    }
    private val mainViewModel: MainViewModel by viewModels {
        getMainComponent().viewModelsFactory()
    }
    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var activityRouter: ActivityRouter

    @Inject
    lateinit var firebaseAuthProvider: FirebaseAuthProvider

    @Inject
    lateinit var mainFragmentProvider: MainFragmentProvider

    private var isEdit = false
    private val getImageResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            currentUri = uri
            Picasso
                .get()
                .load(uri)
                .transform(OrientationTransformation(uri = uri, context = requireContext()))
                .fit()
                .centerCrop()
                .into(binding.avatar)
        }
    private var currentUri: Uri? = null

    private val profilePearlsAdapter: AsyncListDifferDelegationAdapter<BaseViewItem> = AsyncListDifferDelegationAdapter(
        AsyncDifferConfig.Builder(baseItemDiffCallback).build(),
        profileAdapterDelegate(this::openCocktail),
        feedEmptyAdapterDelegate()
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        profileComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                bottom = padding.bottom + insets.systemWindowInsetBottom
            )
            insets
        }
        setListeners()
        binding.loadingScreen.render(show = true)
        binding.pearls.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = profilePearlsAdapter
        }
        if (profileData.profileId == mainViewModel.localId!!) {
            binding.followButton.isVisible = false
            setMyListeners()
            binding.root.doOnApplyWindowInsets { view, insets, padding ->
                view.updatePadding(
                    bottom = padding.bottom + insets.systemWindowInsetBottom
                )
                insets
            }
        } else {
            binding.followButton.render {
                this.copy(
                    isLoading = false,
                    text = getString(R.string.follow),
                    background = R.drawable.button_login
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (profileData.profileId == mainViewModel.localId!!) {
            sendEvent(
                ProfileViewModelEvent.GetSelfProfileById(
                    id = profileData.profileId
                )
            )
        } else {
            sendEvent(
                ProfileViewModelEvent.GetProfileById(
                    GeneralProfileIdWrapper(
                        profileData.profileId, mainViewModel.localId!!
                    )
                )
            )
        }
    }

    private fun setListeners() {
        binding.editAvatar.setOnClickListener {
            getImageResult.launch("image/*")
        }
        profileEventManager.viewEventObservable.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ProfileViewState.SelfProfileResultData -> {
                    binding.profileNameEditText.setText(viewState.profileData.account.name)
                    binding.profileNameTextView.text = viewState.profileData.account.name
                    binding.bio.apply {
                        val bio = viewState.profileData.account.description
                        if (bio.isNotEmpty() && bio != getString(R.string.edit_bio_profile_text)) {
                            binding.bioEditText.setText(bio)
                            text = bio
                            setTextColor(color(R.color.colorPrimary))
                        } else {
                            text = getString(R.string.edit_bio_profile_text)
                            setTextColor(color(R.color.colorSecondary))
                        }
                    }
                    binding.followersTextView.text =
                        "${viewState.profileData.subscriptions_statistics.subscribers_amount}"
                    binding.followingTextView.text =
                        "${viewState.profileData.subscriptions_statistics.subscriptions_amount}"
                    profilePearlsAdapter.items =
                        if (viewState.profileData.pearls.isNotEmpty()) viewState.profileData.pearls.map(PearlData::mapNoProfileViewState)
                        else listOf(FeedEmptyListItem(getString(R.string.empty_pearl_list_text)))
                    val imageAddress = viewState.profileData.account.image_url
                    Log.d(Logger.PROFILE, imageAddress)
                    if (currentUri == null) {
                        if (imageAddress.isNotEmpty()) {
                            Picasso
                                .get()
                                .load(imageAddress)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .into(binding.avatar)
                        } else {
                            Picasso
                                .get()
                                .load(R.drawable.placeholder)
                                .into(binding.avatar)
                        }
                    }
                    binding.loadingScreen.render(show = false)
                }
                is ProfileViewState.ProfileResultData -> {
                    binding.profileNameEditText.setText(viewState.profileData.account.name)
                    binding.profileNameTextView.text = viewState.profileData.account.name
                    binding.bio.apply {
                        val bio = viewState.profileData.account.description
                        isVisible = bio.isNotEmpty()
                    }
                    binding.followersTextView.text =
                        "${viewState.profileData.subscriptions_statistics.subscribers_amount}"
                    binding.followingTextView.text =
                        "${viewState.profileData.subscriptions_statistics.subscriptions_amount}"
                    profilePearlsAdapter.items =
                        if (viewState.profileData.pearls.isNotEmpty()) viewState.profileData.pearls.map(PearlData::mapNoProfileViewState)
                        else listOf(FeedEmptyListItem(getString(R.string.empty_pearl_list_text)))
                    val imageAddress = viewState.profileData.account.image_url
                    Log.d(Logger.PROFILE, imageAddress)
                    if (viewState.profileData.account.image_url != "") {
                        Picasso
                            .get()
                            .load(imageAddress)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(binding.avatar)
                    } else {
                        Picasso
                            .get()
                            .load(R.drawable.placeholder)
                            .into(binding.avatar)
                    }
                    binding.followButton.render {
                        this.copy(
                            isLoading = false,
                            text = if (viewState.profileData.subscriptions_statistics.is_subscribed) {
                                getString(R.string.unfollow)
                            } else {
                                getString(R.string.follow)
                            },
                            background = if (viewState.profileData.subscriptions_statistics.is_subscribed) {
                                R.drawable.button_login_pressed
                            } else {
                                R.drawable.button_login
                            },
                            textColor = if (viewState.profileData.subscriptions_statistics.is_subscribed) {
                                R.color.colorPrimary
                            } else {
                                R.color.colorOnPrimary
                            }
                        )
                    }
                    binding.loadingScreen.render(show = false)
                }
                is ProfileViewState.SubscriptionResult -> {
                    val followersCount = binding.followersTextView.text.toString().toInt() + 1
                    binding.followersTextView.text = "$followersCount"
                    binding.followButton.render {
                        this.copy(
                            isLoading = false,
                            text = getString(R.string.unfollow),
                            background = R.drawable.button_login_pressed,
                            textColor = R.color.colorPrimary,
                            progressBarColor = R.color.colorPrimary
                        )
                    }
                }
                is ProfileViewState.UnsubscriptionResult -> {
                    val followersCount = binding.followersTextView.text.toString().toInt() - 1
                    binding.followersTextView.text = "$followersCount"
                    binding.followButton.render {
                        this.copy(
                            isLoading = false,
                            text = getString(R.string.follow),
                            background = R.drawable.button_login,
                            textColor = R.color.colorOnPrimary,
                            progressBarColor = R.color.colorOnPrimary
                        )
                    }
                }
                is ProfileViewState.EditProfileResult -> {

                }
                is ProfileViewState.EditAvatarResult -> {
                    Log.d(Logger.PROFILE, "profile_id: ${viewState.id.id}")
                    Log.d(Logger.PROFILE, "image_url: ${viewState.id.image_url}")
                }
            }
        }
        binding.followButton.setOnClickListener {
            binding.followButton.render {
                this.copy(
                    isLoading = true,
                    text = ""
                )
            }
            if (profileEventManager.isSubscribed) {
                sendEvent(
                    ProfileViewModelEvent.Unsubscribe(
                        SubscriptionWrapper(
                            mainViewModel.localId!!,
                            profileData.profileId
                        )
                    )
                )
            } else {
                sendEvent(
                    ProfileViewModelEvent.Subscribe(
                        SubscriptionWrapper(
                            mainViewModel.localId!!,
                            profileData.profileId
                        )
                    )
                )
            }
        }
    }

    private fun setMyListeners() {
        (requireActivity() as MainActivity).apply {
            if (profileData.profileId == mainViewModel.localId!!) {
                binding.logout.setOnClickListener {
                    firebaseAuthProvider.auth.signOut()
                    startActivity(activityRouter.routeTo(this, Activities.Login, needNewTask = true))
                }
                binding.logout.isVisible = true
            }
        }
        (requireActivity() as MainActivity).setOnEditProfileListener {
            val button = (it as ImageView)
            isEdit = !isEdit
            if (isEdit) {
                button.setImageResource(R.drawable.ic_done)
                binding.profileNameTextView.visibility = View.INVISIBLE
                binding.profileNameEditText.visibility = View.VISIBLE
                binding.editAvatar.visibility = View.VISIBLE
                binding.bioEditText.isVisible = true
                binding.bio.isInvisible = true
            } else {
                val extension =
                    if (currentUri != null) FileUtils.getMimeType(requireContext(), currentUri!!) ?: "" else ""
                sendEvent(
                    ProfileViewModelEvent.EditProfileData(
                        editProfileWrapper = EditProfileWrapper(
                            id = mainViewModel.localId!!,
                            name = binding.profileNameEditText.text.toString(),
                            description = binding.bioEditText.text.toString()
                        ),
                        context = requireContext(),
                        bitmap = (binding.avatar.drawable as BitmapDrawable).bitmap,
                        fileExtension = extension,
                        wasBitmapChanged = currentUri != null
                    )
                )
                binding.profileNameTextView.text = binding.profileNameEditText.text.toString()
                button.setImageResource(R.drawable.ic_edit)
                binding.profileNameTextView.visibility = View.VISIBLE
                binding.profileNameEditText.visibility = View.INVISIBLE
                binding.editAvatar.visibility = View.GONE
                val bio = binding.bioEditText.text
                binding.bioEditText.isInvisible = true
                binding.bio.isVisible = true
                if (bio.isEmpty()) {
                    binding.bio.apply {
                        text = getString(R.string.edit_bio_profile_text)
                        setTextColor(color(R.color.colorSecondary))
                    }
                } else {
                    binding.bio.apply {
                        text = bio
                        setTextColor(color(R.color.colorPrimary))
                    }
                }
                this.hideKeyboard()
            }
        }
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

    private fun sendEvent(event: ProfileViewModelEvent) {
        profileEventManager.event(event)
    }
}