package com.geronso.pearler.base

import android.content.SharedPreferences

object BaseStorage {

    fun writeTo(prefs: SharedPreferences, tag: KeyStorage, request: String) {
        with(prefs.edit()) {
            putString(tag.key, request)
            apply()
        }
    }

    fun writeTo(prefs: SharedPreferences, tag: KeyStorage, request: Boolean) {
        with(prefs.edit()) {
            putBoolean(tag.key, request)
            apply()
        }
    }
}

enum class KeyStorage(val key: String, val type: Type, val default: Any) {
    WAS_CONFIRMATION_EMAIL_SENT("was_confirmation_email_sent", Type.BOOL, true);

    enum class Type {
        BOOL, STRING
    }
}