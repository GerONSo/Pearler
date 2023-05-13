package com.geronso.pearler.main

import androidx.lifecycle.ViewModel
import com.geronso.pearler.login.model.data.CreateAccountWrapper
import com.geronso.pearler.login.model.data.LocalIdData
import javax.inject.Inject
import javax.inject.Singleton

typealias LocalId = String

@Singleton
class MainViewModel @Inject constructor() : ViewModel() {
    var currentAccount: CreateAccountWrapper = CreateAccountWrapper(
        "",
        "",
        ""
    )
    var localId: LocalId? = null
}