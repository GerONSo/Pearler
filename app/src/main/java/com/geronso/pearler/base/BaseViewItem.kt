package com.geronso.pearler.base

import androidx.recyclerview.widget.DiffUtil

open class BaseViewItem(private val itemId: String = "", val group: String = "") {
    fun getItemId() = javaClass.simpleName + itemId
}

val baseItemDiffCallback = object : DiffUtil.ItemCallback<BaseViewItem>() {
    override fun areItemsTheSame(oldItem: BaseViewItem, newItem: BaseViewItem): Boolean {
        return oldItem.getItemId() == newItem.getItemId()
    }

    override fun areContentsTheSame(oldItem: BaseViewItem, newItem: BaseViewItem): Boolean {
        return oldItem == newItem
    }
}