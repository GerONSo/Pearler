package com.geronso.pearler.main

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.geronso.pearler.R
import com.geronso.pearler.base.FirebaseAuthProvider
import com.geronso.pearler.base.OnKeyboardVisibilityImpl
import com.geronso.pearler.base.OnKeyboardVisibilityListener
import com.geronso.pearler.base.doOnApplyWindowInsets
import com.geronso.pearler.base.drawable
import com.geronso.pearler.base.hideKeyboard
import com.geronso.pearler.base.requestApplyInsetsWhenAttached
import com.geronso.pearler.base.setMarginTop
import com.geronso.pearler.feed.view.FeedFragment
import com.geronso.pearler.friends.view.FriendsFragment
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.main.di.MyApplication
import com.geronso.pearler.pearl.data.PearlFragmentData
import com.geronso.pearler.pearl.view.PearlFragment
import com.geronso.pearler.profile.data.ProfileFragmentData
import com.geronso.pearler.profile.view.ProfileFragment
import com.geronso.pearler.search.view.SearchFragment
import com.geronso.pearler.widgets.Snackbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ViewUtils
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnKeyboardVisibilityListener {

    @Inject
    lateinit var firebaseAuthProvider: FirebaseAuthProvider

    @Inject
    lateinit var mainFragmentProvider: MainFragmentProvider

    private val mainViewModel: MainViewModel by viewModels {
        (applicationContext as MyApplication).mainComponent.viewModelsFactory()
    }

    private val navigation: BottomNavigationView
        get() = findViewById(R.id.navigation)
    private val mainFrame: FragmentContainerView
        get() = findViewById(R.id.main_frame)
    private val mainLayout: CoordinatorLayout
        get() = findViewById(R.id.mainLayout)
    private val editProfileButton: ImageView
        get() = findViewById(R.id.editProfileButton)
    private val mainToolbar: Toolbar
        get() = findViewById(R.id.main_toolbar)
    private val pearlButton: FloatingActionButton
        get() = findViewById(R.id.fab_pearl)
    private val snackbar: Snackbar
        get() = findViewById(R.id.snackbar)

    private var isKeyboardOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MyApplication).mainComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainLayout.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        setListeners()
        setSupportActionBar(mainToolbar)
        //        mainToolbar.setMarginTop(getStatusBarSize())
        OnKeyboardVisibilityImpl.setKeyboardVisibilityListener(this, this)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setTitle(R.string.home_title)
        }
        firebaseAuthProvider.auth.currentUser?.let { user ->
            Log.d(Logger.MAIN, "user id: ${user.uid}")
        }
        mainToolbar.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                top = insets.systemWindowInsetTop
            )
            insets
        }
        navigation.doOnApplyWindowInsets { view, insets, padding ->
            insets
        }
        navigation.apply {
            background = null
            menu.getItem(2).isEnabled = false
        }
        setCurrentFragment(
            mainFragmentProvider.provideFragment(MainFragmentProvider.Fragments.FEED),
            false
        )
    }

    private fun setListeners() {
        navigation.setOnItemSelectedListener {
            editProfileButton.visibility = View.GONE
            when (it.itemId) {
                R.id.home -> {
                    supportActionBar?.setTitle(R.string.home_title)
                    setCurrentFragment(
                        mainFragmentProvider.provideFragment(MainFragmentProvider.Fragments.FEED),
                        true
                    )
                    true
                }
                R.id.search -> {
                    supportActionBar?.setTitle(R.string.search_title)
                    setCurrentFragment(
                        mainFragmentProvider.provideFragment(MainFragmentProvider.Fragments.SEARCH),
                        true
                    )
                    true
                }
                R.id.friends -> {
                    supportActionBar?.setTitle(R.string.friends_title)
                    setCurrentFragment(
                        mainFragmentProvider.provideFragment(MainFragmentProvider.Fragments.FRIENDS),
                        true
                    )
                    true
                }
                R.id.profile -> {
                    editProfileButton.visibility = View.VISIBLE
                    supportActionBar?.title = ""
                    setCurrentFragment(
                        fragment = mainFragmentProvider.provideFragment(
                            MainFragmentProvider.Fragments.PROFILE,
                            ProfileFragmentData(mainViewModel.localId!!)
                        ),
                        addToBackstack = true,
                        toolbarOverlay = true
                    )
                    true
                }
                else -> throw IllegalArgumentException("Unknown menu item")
            }
        }
        pearlButton.setOnClickListener {
            setPearlFragment()
        }
    }

    fun setPearlFragment(pearlFragmentData: PearlFragmentData? = null) {
        supportActionBar?.setTitle(R.string.pearl_title)
        setCurrentFragment(
            mainFragmentProvider.provideFragment(MainFragmentProvider.Fragments.PEARL, pearlFragmentData),
            true
        )
    }

    fun setCurrentFragment(fragment: Fragment, addToBackstack: Boolean, toolbarOverlay: Boolean = false) {
        if (toolbarOverlay) {
            supportActionBar?.title = ""
            mainToolbar.background = drawable(R.drawable.toolbar_background)
            mainToolbar.navigationIcon?.setTint(getColor(R.color.colorOnPrimary))
        } else {
            mainToolbar.background = null
            mainToolbar.navigationIcon?.setTint(getColor(R.color.colorPrimary))
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_frame, fragment)
            if (addToBackstack) addToBackStack(null)
            commit()
        }
    }

    fun setOnEditProfileListener(listener: View.OnClickListener) {
        editProfileButton.setImageResource(R.drawable.ic_edit)
        editProfileButton.setOnClickListener(listener)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        if (isKeyboardOpen) {
            hideKeyboard()
            return
        }
        super.onBackPressed()
        val fragment = supportFragmentManager.findFragmentById(R.id.main_frame)
        val toolbarOverlay = fragment is ProfileFragment
        if (toolbarOverlay) {
            supportActionBar?.title = ""
            mainToolbar.background = drawable(R.drawable.toolbar_background)
            mainToolbar.navigationIcon?.setTint(getColor(R.color.colorOnPrimary))
        } else {
            mainToolbar.background = null
            mainToolbar.navigationIcon?.setTint(getColor(R.color.colorPrimary))
        }

        editProfileButton.visibility = View.GONE
        when (fragment) {
            is FeedFragment -> {
                navigation.menu.getItem(0).isChecked = true
                supportActionBar?.setTitle(R.string.home_title)
            }
            is SearchFragment -> {
                navigation.menu.getItem(1).isChecked = true
                supportActionBar?.setTitle(R.string.search_title)
            }
            is FriendsFragment -> {
                navigation.menu.getItem(3).isChecked = true
                supportActionBar?.setTitle(R.string.friends_title)
            }
            is ProfileFragment -> {
                editProfileButton.visibility = View.VISIBLE
                navigation.menu.getItem(4).isChecked = true
                supportActionBar?.title = ""
            }
            is PearlFragment -> {
                supportActionBar?.setTitle(R.string.pearl_title)
            }
        }
    }

    fun showSnackbar(text: String) {
        snackbar.show(text)
    }

    private fun getStatusBarSize(): Int {
        val rectangle = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        val statusBarHeight: Int = rectangle.top
        val contentViewTop: Int = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
        return contentViewTop - statusBarHeight
    }

    override fun onVisibilityChanged(visible: Boolean) {
        isKeyboardOpen = visible
    }
}
