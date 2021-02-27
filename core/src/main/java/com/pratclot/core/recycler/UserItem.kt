package com.pratclot.core.recycler

import com.pratclot.domain.User

data class UserItem(val user: User, val onClick: () -> Unit)
