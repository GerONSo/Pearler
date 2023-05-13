package com.geronso.pearler.base

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseAuthProvider @Inject constructor() {

    val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()
}