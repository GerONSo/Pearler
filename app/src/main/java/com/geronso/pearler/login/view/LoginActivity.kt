package com.geronso.pearler.login.view

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.geronso.pearler.R
import com.geronso.pearler.base.Activities
import com.geronso.pearler.base.ActivityRouter
import com.geronso.pearler.base.FirebaseAuthProvider
import com.geronso.pearler.base.OnKeyboardVisibilityListener
import com.geronso.pearler.databinding.ActivityLoginBinding
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.login.UserAuthenticationData
import com.geronso.pearler.login.di.LoginComponent
import com.geronso.pearler.login.viewmodel.LoginEventManager
import com.geronso.pearler.login.viewmodel.LoginViewModelEvent
import com.geronso.pearler.login.viewmodel.LoginViewState
import com.geronso.pearler.main.MainViewModel
import com.geronso.pearler.main.di.MyApplication
import com.geronso.pearler.widgets.LoadingButton
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), OnKeyboardVisibilityListener {

    private val loginComponent: LoginComponent
        get() = (applicationContext as MyApplication).mainComponent.loginComponent().create()

    private val loginEventManager: LoginEventManager by viewModels {
        loginComponent.viewModelsFactory()
    }
    private val mainViewModel: MainViewModel by viewModels {
        (applicationContext as MyApplication).mainComponent.viewModelsFactory()
    }
    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var activityRouter: ActivityRouter

    @Inject
    lateinit var firebaseAuthProvider: FirebaseAuthProvider

    private val recoverPassword = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val isRecoverEmailSent = result.data?.getBooleanExtra("isRecoverEmailSent", false) ?: false
        if (result.resultCode == RESULT_OK && isRecoverEmailSent) {
            showSnackbar("Recover email has been sent")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(applicationContext)
        loginComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuthProvider.auth.addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                sendEvent(LoginViewModelEvent.GetLocalIdByFirebaseUid(firebaseAuthProvider.auth.currentUser))
            }
        }
        setListeners()
        setKeyboardVisibilityListener(this)
    }

    private fun setListeners() {
        with(binding) {
            loginButton.render {
                this.copy(
                    text = getString(R.string.btn_login_text)
                )
            }
            loginButton.setOnClickListener {
                loginButton.render {
                    this.copy(
                        isLoading = true,
                    )
                }
                sendEvent(LoginViewModelEvent.FirebaseLogin(provideUserAuthenticationData()))
            }
            registerButton.render {
                LoadingButton.State.DEFAULT.copy(
                    text = getString(R.string.register)
                )
            }
            registerButton.setOnClickListener {
                startActivity(activityRouter.routeTo(this@LoginActivity, Activities.Register))
            }
            recoverPasswordButton.setOnClickListener {
                recoverPassword.launch(activityRouter.routeTo(this@LoginActivity, Activities.RecoverPassword))
            }
            loginEventManager.viewEventObservable.observe(this@LoginActivity) { viewState ->
                when (viewState) {
                    is LoginViewState.FirebaseLoginSuccess -> {
                        mainViewModel.currentAccount.email = firebaseAuthProvider.auth.currentUser?.email!!
                        mainViewModel.currentAccount.firebase_uid =
                            firebaseAuthProvider.auth.currentUser!!.uid
                        sendEvent(LoginViewModelEvent.GetLocalIdByFirebaseUid(firebaseAuthProvider.auth.currentUser))
                        Log.d(Logger.LOGIN, "received success")
                    }
                    is LoginViewState.LoginFailed -> {
                        loginButton.render {
                            this.copy(
                                isLoading = false,
                            )
                        }
                        showSnackbar(
                            when (viewState.error) {
                                LoginViewState.LoginFailed.LoginFailReason.FIREBASE -> {
                                    "There is no account with these credentials"
                                }
                                LoginViewState.LoginFailed.LoginFailReason.EMPTY -> {
                                    "Empty email or password"
                                }
                            }
                        )
                        Log.d(Logger.LOGIN, "received fail")
                    }
                    is LoginViewState.LoginSuccess -> {
                        loginButton.render {
                            this.copy(
                                isLoading = false,
                            )
                        }
                        val exists = viewState.localIdData.existsMetadata.exists
                        if (exists) {
                            Log.d(
                                Logger.LOGIN,
                                "Successful login localId: ${viewState.localIdData.id}"
                            )
                            mainViewModel.localId = viewState.localIdData.id
                            startActivity(
                                activityRouter.routeTo(
                                    this@LoginActivity,
                                    Activities.Main,
                                    needNewTask = true
                                )
                            )
                        } else {
                            Log.d(Logger.LOGIN, "This user doesn't exists")
                            val emailVerified =
                                firebaseAuthProvider.auth.currentUser?.isEmailVerified
                            if (emailVerified == null) {
                                sendEvent(
                                    LoginViewModelEvent.GetLocalIdByFirebaseUid(
                                        firebaseAuthProvider.auth.currentUser
                                    )
                                )
                                return@observe
                            }
                            if (!emailVerified) {
                                startActivity(
                                    activityRouter.routeTo(
                                        this@LoginActivity,
                                        Activities.EmailConfirmation
                                    )
                                )
                            } else {
                                startActivity(
                                    activityRouter.routeTo(
                                        this@LoginActivity,
                                        Activities.SetName
                                    )
                                )
                            }
                        }
                    }
                    is LoginViewState.AccountCreated -> { }
                }
            }
        }
    }

    private fun sendEvent(event: LoginViewModelEvent) {
        loginEventManager.event(event)
    }

    private fun provideUserAuthenticationData() =
        UserAuthenticationData(
            binding.emailEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )

    private fun scaleLayoutIfNeed(need: Boolean) {
        if (need) {
            binding.root.transitionToEnd()
        } else {
            binding.root.transitionToStart()
        }
    }

    private fun setKeyboardVisibilityListener(onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {
        val parentView = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        parentView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            private var alreadyOpen = false
            private val defaultKeyboardHeightDP = 100
            private val EstimatedKeyboardDP =
                defaultKeyboardHeightDP + 48
            private val rect: Rect = Rect()
            override fun onGlobalLayout() {
                val estimatedKeyboardHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    EstimatedKeyboardDP.toFloat(),
                    parentView.resources.displayMetrics
                ).toInt()
                parentView.getWindowVisibleDisplayFrame(rect)
                val heightDiff: Int = parentView.rootView.height - (rect.bottom - rect.top)
                val isShown = heightDiff >= estimatedKeyboardHeight
                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...")
                    return
                }
                alreadyOpen = isShown
                onKeyboardVisibilityListener.onVisibilityChanged(isShown)
            }
        })
    }

    override fun onVisibilityChanged(visible: Boolean) {
        if (!visible) {
            binding.emailEditText.clearFocus()
            binding.passwordEditText.clearFocus()
        }
        scaleLayoutIfNeed(visible)
    }

    fun showSnackbar(text: String) {
        binding.snackbar.show(text)
    }
}