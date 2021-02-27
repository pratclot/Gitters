package com.pratclot.core

import android.widget.Toast
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class HandyFragment : Fragment() {
    protected val subscriptions = CompositeDisposable()
    override fun onDestroyView() {
        subscriptions.clear()
        super.onDestroyView()
    }

    fun Disposable.toDisposables() {
        subscriptions.add(this)
    }

    fun showToast(text: String) = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}
