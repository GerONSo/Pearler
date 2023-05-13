package com.geronso.pearler.base

import android.app.Activity
import android.content.Intent
import com.geronso.pearler.login.view.EmailConfirmationActivity
import com.geronso.pearler.login.view.LoginActivity
import com.geronso.pearler.login.view.RecoverPasswordActivity
import com.geronso.pearler.login.view.SetNameActivity
import com.geronso.pearler.main.MainActivity
import com.geronso.pearler.registration.view.RegistrationActivity
import javax.inject.Inject

class ActivityRouter @Inject constructor() {
    fun routeTo(
        from: Activity,
        activity: Activities,
        needNewTask: Boolean = false,
        needAnimation: Boolean = true
    ): Intent =
        Intent(
            from,
            when (activity) {
                Activities.Register -> {
                    RegistrationActivity::class.java
                }
                Activities.Main -> {
                    MainActivity::class.java
                }
                Activities.SetName -> {
                    SetNameActivity::class.java
                }
                Activities.EmailConfirmation -> {
                    EmailConfirmationActivity::class.java
                }
                Activities.Login -> {
                    LoginActivity::class.java
                }
                Activities.RecoverPassword -> {
                    RecoverPasswordActivity::class.java
                }
            }
        ).apply {
            if (needNewTask) flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (!needAnimation) addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
}