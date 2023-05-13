package com.geronso.pearler.login.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.geronso.pearler.R
import com.geronso.pearler.base.Activities
import com.geronso.pearler.base.ActivityRouter
import com.geronso.pearler.base.BaseStorage
import com.geronso.pearler.base.FirebaseAuthProvider
import com.geronso.pearler.base.KeyStorage
import com.geronso.pearler.databinding.ActivityEmailConfirmationBinding
import com.geronso.pearler.login.di.LoginComponent
import com.geronso.pearler.main.di.MyApplication
import com.geronso.pearler.widgets.LoadingButton
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class EmailConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailConfirmationBinding
    private val loginComponent: LoginComponent
        get() = (applicationContext as MyApplication).mainComponent.loginComponent().create()
    private val prefs: SharedPreferences by lazy {
        getPreferences(Context.MODE_PRIVATE)
    }

    @Inject
    lateinit var firebaseAuthProvider: FirebaseAuthProvider
    @Inject
    lateinit var activityRouter: ActivityRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        loginComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityEmailConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            explanationButton.setOnClickListener {
                help.isVisible = !help.isVisible
            }
            helpButton.setOnClickListener {
                sendVerificationEmail()
            }
            reloadButton.render(
                LoadingButton.State.DEFAULT.copy(
                    text = getString(R.string.reload)
                )
            )
            reloadButton.setOnClickListener {
                updateScreenStatus()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateScreenStatus()
    }

    private fun sendVerificationEmail() {
        firebaseAuthProvider.auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    binding.help.isVisible = true
                } else {
                    BaseStorage.writeTo(prefs, KeyStorage.WAS_CONFIRMATION_EMAIL_SENT, true)
                }
            }
    }

    private fun updateScreenStatus() {
        val email = intent.getStringExtra(EMAIL_INTENT_FLAG)
        val password = intent.getStringExtra(PASSWORD_INTENT_FLAG)
        if (email == null || password == null) return
        firebaseAuthProvider.auth.signInWithEmailAndPassword(email, password)
        val wasConfirmationEmailSent = prefs.getBoolean(KeyStorage.WAS_CONFIRMATION_EMAIL_SENT.key, false)
        val emailVerified = firebaseAuthProvider.auth.currentUser?.isEmailVerified!!
        if (!emailVerified) {
            if (!wasConfirmationEmailSent) {
                sendVerificationEmail()
            }
        } else {
            startActivity(activityRouter.routeTo(this@EmailConfirmationActivity, Activities.SetName))
        }
    }

    companion object {
        const val EMAIL_INTENT_FLAG = "email"
        const val PASSWORD_INTENT_FLAG = "password"
    }
}