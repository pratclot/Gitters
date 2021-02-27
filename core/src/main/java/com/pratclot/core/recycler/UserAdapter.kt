package com.pratclot.core.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.pratclot.core.databinding.RecyclerItemUserBinding

class UserAdapter : PagingDataAdapter<UserItem, UserViewHolder>(DiffUtilUser) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        return UserViewHolder(RecyclerItemUserBinding.inflate(layoutInflater, p0, false))
    }

    override fun onBindViewHolder(p0: UserViewHolder, p1: Int) {
        getItem(p1)?.let { p0.bind(it) }
    }
}
