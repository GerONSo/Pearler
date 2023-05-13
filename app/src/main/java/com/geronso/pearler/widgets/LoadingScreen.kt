package com.geronso.pearler.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.geronso.pearler.R
import com.geronso.pearler.base.dpToPx
import com.geronso.pearler.base.dpToPxF
import com.geronso.pearler.base.drawable
import com.geronso.pearler.databinding.LoadingScreenBinding

class LoadingScreen @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: LoadingScreenBinding

    init {
        elevation = 2.dpToPxF
        background = drawable(R.color.colorOnPrimary)
        binding = LoadingScreenBinding.inflate(LayoutInflater.from(context), this)
    }

    fun render(show: Boolean) {
        binding.root.isVisible = show
    }
}