package com.geronso.pearler.widgets

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.geronso.pearler.R

class Snackbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.style.Theme_Pearler_Snackbar
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var startTime = 0L
    private val timerHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            val time = System.currentTimeMillis() - startTime
            if (time > SHOW_TIME) {
                timerHandler.removeCallbacks(this)
                isVisible = false
                return
            }
            timerHandler.postDelayed(this, HANDLER_STEP)
        }
    }

    init {
//        isVisible = false
    }

    fun show(text: String) {
        setText(text)
        isVisible = true
        startTime = System.currentTimeMillis()
        timerHandler.post(timerRunnable)
    }

    companion object {
        fun create(context: Context, text: String): Snackbar {
            return Snackbar(context).apply {
                setText(text)
            }
        }

        private const val SHOW_TIME = 2000L
        private const val HANDLER_STEP = 200L
    }
}