package com.pratclot.core.recycler

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pratclot.core.R
import com.pratclot.core.databinding.RecyclerItemUserBinding

class UserViewHolder(val binding: RecyclerItemUserBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: UserItem) {
        binding.apply {
            item.user.let {
                Glide.with(root).load(it.avatar_url)
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .into(imageViewRecyclerItemUser)
                textViewRecyclerItemUser.text = it.login
            }

            root.setOnClickListener { item.onClick() }
        }
    }
}
