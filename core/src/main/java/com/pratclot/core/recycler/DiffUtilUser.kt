package com.pratclot.core.recycler

import androidx.recyclerview.widget.DiffUtil

object DiffUtilUser : DiffUtil.ItemCallback<UserItem>() {
    override fun areItemsTheSame(p0: UserItem, p1: UserItem): Boolean {
        return p0.user.id == p1.user.id
    }

    override fun areContentsTheSame(
        p0: UserItem,
        p1: UserItem
    ): Boolean {
        return areItemsTheSame(p0, p1)
    }
}
