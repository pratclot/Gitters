package com.pratclot.core

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class HandyViewModel : ViewModel() {
    protected val subscriptions = CompositeDisposable()

    fun Disposable.toDisposables() {
        subscriptions.add(this)
    }

    override fun onCleared() {
        subscriptions.clear()
        super.onCleared()
    }
}
