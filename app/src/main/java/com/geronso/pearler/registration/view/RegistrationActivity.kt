package com.geronso.pearler.registration.view

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import com.geronso.pearler.R
import com.geronso.pearler.base.Activities
import com.geronso.pearler.base.ActivityRouter
import com.geronso.pearler.base.OnKeyboardVisibilityListener
import com.geronso.pearler.base.doOnApplyWindowInsets
import com.geronso.pearler.base.setMarginTop
import com.geronso.pearler.databinding.ActivityRegistrationBinding
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.login.UserAuthenticationData
import com.geronso.pearler.login.view.EmailConfirmationActivity
import com.geronso.pearler.main.di.DaggerMainComponent
import com.geronso.pearler.registration.model.RegistrationData
import com.geronso.pearler.registration.viewmodel.RegistrationEventManager
import com.geronso.pearler.registration.viewmodel.RegistrationViewModelEvent
import com.geronso.pearler.registration.viewmodel.RegistrationViewState
import com.geronso.pearler.widgets.LoadingButton
import com.geronso.pearler.widgets.Snackbar
import javax.inject.Inject

class RegistrationActivity : AppCompatActivity(), OnKeyboardVisibilityListener {

    private val registrationComponent by lazy {
        DaggerMainComponent.create().registrationComponent().create()
    }
    private lateinit var binding: ActivityRegistrationBinding

    @Inject
    lateinit var registrationEventManager: RegistrationEventManager

    @Inject
    lateinit var activityRouter: ActivityRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        registrationComponent.inject(this)
        setListeners()
        setKeyboardVisibilityListener(this)
    }

    private fun setListeners() {
        with(binding.registrationButton) {
            render {
                this.copy(
                    text = resources.getString(R.string.btn_register_text),
                )
            }
            setOnClickListener {
                render {
                    this.copy(
                        isLoading = true,
                        text = ""
                    )
                }
                sendEvent(RegistrationViewModelEvent.Register(provideUserAuthenticationData()))
            }
        }
        registrationEventManager.viewEventObservable.observe(this) {
            when (it) {
                is RegistrationViewState.RegistrationSuccess -> {
                    binding.registrationButton.render {
                        this.copy(
                            isLoading = false,
                            text = resources.getString(R.string.btn_register_text),
                        )
                    }
                    val intent = activityRouter.routeTo(
                        this@RegistrationActivity,
                        Activities.EmailConfirmation
                    )
                    intent.putExtra(
                        EmailConfirmationActivity.EMAIL_INTENT_FLAG,
                        binding.emailEditText.text.toString()
                    )
                    intent.putExtra(
                        EmailConfirmationActivity.PASSWORD_INTENT_FLAG,
                        binding.passwordEditText.text.toString()
                    )
                    startActivity(intent)
                }
                is RegistrationViewState.RegistrationFailed -> {
                    binding.registrationButton.render {
                        this.copy(
                            isLoading = false,
                            text = resources.getString(R.string.btn_register_text)
                        )
                    }
                    Log.d(Logger.REGISTRATION, "received fail")
                    showSnackbar(
                        when (it.error) {
                            RegistrationViewState.RegistrationFailed.RegistrationFailReason.FIREBASE_ERROR -> {
                                "Incorrect format of email or password"
                            }
                            RegistrationViewState.RegistrationFailed.RegistrationFailReason.EMPTY_EMAIL_OR_PASSWORD -> {
                                "Empty email or password"
                            }
                        }
                    )
                }
            }
        }
    }

    private fun provideUserAuthenticationData(): UserAuthenticationData =
        UserAuthenticationData(
            binding.emailEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )

    private fun sendEvent(event: RegistrationViewModelEvent) {
        registrationEventManager.event(event)
    }

    private fun scaleLayoutIfNeed(need: Boolean) {
        if (need) {
            binding.root.transitionToEnd()
        } else {
            binding.root.transitionToStart()
        }
    }

    private fun setKeyboardVisibilityListener(onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {
        val parentView = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        parentView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
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
//        val snackbar = Snackbar.create(applicationContext, "")
//        binding.root.addView(snackbar)
//        snackbar.show("LoLOLLOl")
        binding.snackbar.show(text)
    }
}