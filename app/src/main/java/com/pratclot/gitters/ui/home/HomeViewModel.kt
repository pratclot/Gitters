package com.pratclot.gitters.ui.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import androidx.paging.rxjava2.observable
import com.pratclot.core.HandyViewModel
import com.pratclot.di.internetSubject
import com.pratclot.di.tokenSubject
import com.pratclot.domain.User
import com.pratclot.repo.GithubPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val githubPagingSource: GithubPagingSource,
) : HandyViewModel() {

    val usersSubject: BehaviorSubject<PagingData<User>> = BehaviorSubject.create()
    val homeFragmentIsReady: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    init {
        subscriptions.apply {
            add(
                Observable.combineLatest(
                    tokenSubject,
                    homeFragmentIsReady,
                    internetSubject
                ) { t1, t2, t3 -> t1 and t2 and t3 }
                    .filter { it }
                    .flatMap { getUsersFromPagingSource(1) }
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            usersSubject.onNext(it)
                        },
                        {
                            Log.e(TAG, "We are in trouble: $it")
                        }
                    )
            )
        }
    }

    fun getUsersFromPagingSource(limit: Int): Observable<PagingData<User>> {
        val pager = Pager(PagingConfig(limit)) { githubPagingSource }
        return pager.observable.cachedIn(viewModelScope)
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}
