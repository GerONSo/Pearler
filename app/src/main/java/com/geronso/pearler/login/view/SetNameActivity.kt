package com.geronso.pearler.login.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import com.geronso.pearler.R
import com.geronso.pearler.base.Activities
import com.geronso.pearler.base.ActivityRouter
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.login.di.LoginComponent
import com.geronso.pearler.login.viewmodel.LoginEventManager
import com.geronso.pearler.login.viewmodel.LoginViewModelEvent
import com.geronso.pearler.login.viewmodel.LoginViewState
import com.geronso.pearler.main.MainViewModel
import com.geronso.pearler.main.di.MyApplication
import com.geronso.pearler.widgets.LoadingButton
import javax.inject.Inject

class SetNameActivity : AppCompatActivity() {
    private val nextButton: LoadingButton
        get() = findViewById(R.id.nextButton)
    private val loginComponent: LoginComponent
        get() = (applicationContext as MyApplication).mainComponent.loginComponent().create()

    private val loginEventManager: LoginEventManager by viewModels {
        loginComponent.viewModelsFactory()
    }
    private val mainViewModel: MainViewModel by viewModels {
        (applicationContext as MyApplication).mainComponent.viewModelsFactory()
    }

    private val nameEditText: EditText
        get() = findViewById(R.id.nameEditText)

    @Inject
    lateinit var activityRouter: ActivityRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        loginComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_name)
        nextButton.render {
            LoadingButton.State.DEFAULT.copy(
                text = getString(R.string.next)
            )
        }
        nextButton.setOnClickListener {
            val name = nameEditText.text.toString()
            if (name != "") {
                mainViewModel.currentAccount.name = name
                Log.d(Logger.LOGIN, "create account " +
                        "email:${mainViewModel.currentAccount.email} " +
                        "firebase_uid: ${mainViewModel.currentAccount.firebase_uid}" +
                        "name: ${mainViewModel.currentAccount.name}")
                sendEvent(LoginViewModelEvent.CreateAccount(mainViewModel.currentAccount))
            }
        }
        loginEventManager.viewEventObservable.observe(this) {
            when (it) {
                is LoginViewState.AccountCreated -> {
                    Log.d(Logger.LOGIN, "Account created id: ${it.localIdData.id}")
                    mainViewModel.localId = it.localIdData.id
                    startActivity(activityRouter.routeTo(this, Activities.Main, needNewTask = true))
                }
                else -> {}
            }
        }
    }

    private fun sendEvent(event: LoginViewModelEvent) {
        loginEventManager.event(event)
    }
}