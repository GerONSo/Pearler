package com.geronso.pearler.login.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.geronso.pearler.R
import com.geronso.pearler.base.FirebaseAuthProvider
import com.geronso.pearler.databinding.ActivityRecoverPasswordBinding
import com.geronso.pearler.logging.Logger
import com.geronso.pearler.main.MainViewModel
import com.geronso.pearler.main.di.MyApplication
import com.geronso.pearler.widgets.LoadingButton
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class RecoverPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecoverPasswordBinding

    private val firebaseAuthProvider = FirebaseAuthProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoverPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.nextButton.render {
            LoadingButton.State.DEFAULT.copy(
                text = getString(R.string.next)
            )
        }
        binding.nextButton.setOnClickListener {
            binding.nextButton.render {
                this.copy(
                    isLoading = true
                )
            }
            firebaseAuthProvider.auth.sendPasswordResetEmail(binding.recoverEmailEditText.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        setResult(RESULT_OK, Intent().also { it.putExtra("isRecoverEmailSent", true) })
                        finish()
                    } else {
                        binding.snackbar.show("Recover email cannot be sent")
                    }
                    binding.nextButton.render {
                        this.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }
}