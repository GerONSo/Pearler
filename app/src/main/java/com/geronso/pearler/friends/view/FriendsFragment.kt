package com.geronso.pearler.friends.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geronso.pearler.R
import com.geronso.pearler.base.BaseViewItem
import com.geronso.pearler.base.baseItemDiffCallback
import com.geronso.pearler.base.doOnApplyWindowInsets
import com.geronso.pearler.base.getMainComponent
import com.geronso.pearler.databinding.FragmentFriendsBinding
import com.geronso.pearler.friends.model.data.AllUsersPrefixRequest
import com.geronso.pearler.friends.model.data.SubscriptionWrapper
import com.geronso.pearler.friends.viewmodel.FriendsEventManager
import com.geronso.pearler.friends.viewmodel.FriendsViewModelEvent
import com.geronso.pearler.friends.viewmodel.FriendsViewState
import com.geronso.pearler.main.MainActivity
import com.geronso.pearler.main.MainFragmentProvider
import com.geronso.pearler.main.MainViewModel
import com.geronso.pearler.main.di.MyApplication
import com.geronso.pearler.profile.data.ProfileFragmentData
import com.geronso.pearler.profile.model.data.ProfileData
import com.geronso.pearler.profile.model.data.ProfileIdWrapper
import com.geronso.pearler.profile.model.data.mapAllUsersViewItem
import com.geronso.pearler.profile.model.data.mapFriendsViewItem
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import javax.inject.Inject

class FriendsFragment : Fragment() {

    private val friendsComponent
        get() = getMainComponent().friendsComponent().create()
    private val friendsEventManager: FriendsEventManager by viewModels {
        friendsComponent.viewModelsFactory()
    }
    private val mainViewModel: MainViewModel by viewModels {
        (requireActivity().applicationContext as MyApplication).mainComponent.viewModelsFactory()
    }

    private val friendsAdapter: AsyncListDifferDelegationAdapter<BaseViewItem> = AsyncListDifferDelegationAdapter(
        AsyncDifferConfig.Builder(baseItemDiffCallback).build(),
        friendsAdapterDelegate(
            this::onSelectedProfile,
        )
    )
    private val allUsersAdapter: AsyncListDifferDelegationAdapter<BaseViewItem> = AsyncListDifferDelegationAdapter(
        AsyncDifferConfig.Builder(baseItemDiffCallback).build(),
        allUsersAdapterDelegate(
            this::onSelectedProfile,
            this::onAddFriend,
            this::onRemoveFriend
        )
    )
    private lateinit var binding: FragmentFriendsBinding

    @Inject
    lateinit var mainFragmentProvider: MainFragmentProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)
        friendsComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        friendsEventManager.viewEventObservable.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is FriendsViewState.MyFriendsResult -> {
                    binding.emptyFriendsListView.render(show = viewState.myFriends.isEmpty())
                    friendsAdapter.items = viewState.myFriends.map(ProfileData::mapFriendsViewItem)
                    binding.loadingScreen.render(show = false)
                }
                is FriendsViewState.AllUsersResult -> {
                    binding.emptyAllUsersListView.render(show = viewState.allUsers.isEmpty())
                    allUsersAdapter.items = viewState.allUsers.map(ProfileData::mapAllUsersViewItem)
                    binding.loadingScreen.render(show = false)
                }
                is FriendsViewState.SubscriptionResult -> {
//                    sendEvent(
//                        FriendsViewModelEvent.GetMyFriends(
//                            mainViewModel.localIdData.id
//                        )
//                    )
//                    sendEvent(
//                        FriendsViewModelEvent.GetAllUsersByPrefix(
//                            accountId = mainViewModel.localIdData.id,
//                            prefix = binding.searchEditText.text.toString(),
//                            limit = ALL_USERS_LIMIT
//                        )
//                    )
                }
            }
        }
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                top = padding.top + insets.systemWindowInsetTop,
                bottom = padding.bottom + insets.systemWindowInsetBottom
            )
            insets
        }
        binding.loadingScreen.render(show = true)
        friendsEventManager.myFriends.observe(viewLifecycleOwner) { myFriends ->
            binding.emptyFriendsListView.render(show = myFriends.isEmpty())
            friendsAdapter.items = myFriends.map(ProfileData::mapFriendsViewItem)
            binding.searchEditText.doOnTextChanged { text, _, _, _ ->
                friendsAdapter.items = myFriends.filter { friend ->
                    friend.name.uppercase().startsWith(text.toString().uppercase())
                }.map(ProfileData::mapFriendsViewItem)
                sendEvent(
                    FriendsViewModelEvent.GetAllUsersByPrefix(
                        accountId = mainViewModel.localId!!,
                        prefix = text.toString(),
                        limit = ALL_USERS_LIMIT
                    )
                )
            }
        }
        friendsEventManager.allUsers.observe(viewLifecycleOwner) { allUsers ->
            binding.emptyAllUsersListView.render(show = allUsers.isEmpty())
            allUsersAdapter.items = allUsers
        }
        binding.friendsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = friendsAdapter
        }
        binding.allUsersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = allUsersAdapter
        }
        sendEvent(
            FriendsViewModelEvent.GetMyFriends(
                profileId = mainViewModel.localId!!
            )
        )
        sendEvent(
            FriendsViewModelEvent.GetAllUsersByPrefix(
                accountId = mainViewModel.localId!!,
                prefix = "",
                limit = ALL_USERS_LIMIT
            )
        )
    }

    private fun onAddFriend(id: String) {
        val subscriptionWrapper = SubscriptionWrapper(
            source_id = mainViewModel.localId!!,
            target_id = id
        )
        sendEvent(
            FriendsViewModelEvent.Subscribe(
                subscriptionWrapper
            )
        )
    }

    private fun onRemoveFriend(id: String) {
        val subscriptionWrapper = SubscriptionWrapper(
            source_id = mainViewModel.localId!!,
            target_id = id
        )
        sendEvent(
            FriendsViewModelEvent.Unsubscribe(
                subscriptionWrapper
            )
        )
    }

    private fun onSelectedProfile(id: String) {
        (requireActivity() as MainActivity).setCurrentFragment(
            fragment = mainFragmentProvider.provideFragment(
                MainFragmentProvider.Fragments.PROFILE,
                ProfileFragmentData(id)
            ),
            addToBackstack = true,
            toolbarOverlay = true
        )
    }

    private fun sendEvent(event: FriendsViewModelEvent) {
        friendsEventManager.event(event)
    }

    companion object {

        private const val ALL_USERS_LIMIT = 10
    }
}