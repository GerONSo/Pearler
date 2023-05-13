package com.geronso.pearler.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.geronso.pearler.databinding.EmptyListViewBinding

class EmptyListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    private val binding: EmptyListViewBinding

    init {
        orientation = VERTICAL
        binding = EmptyListViewBinding.inflate(LayoutInflater.from(context), this)
    }

    fun render(show: Boolean) {
        binding.emptyFriendsListText.isVisible = show
        binding.emptyFriendsListIcon.isVisible = show
    }
}