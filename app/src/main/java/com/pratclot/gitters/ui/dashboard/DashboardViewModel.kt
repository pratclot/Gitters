package com.pratclot.gitters.ui.dashboard

import android.util.Log
import com.pratclot.core.HandyViewModel
import com.pratclot.domain.User
import com.pratclot.service.GithubApi
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(val githubApi: GithubApi) : HandyViewModel() {

    var user: User? = null
        set(value) {
            if (value != null) {
                githubApi.getUser(value.login)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            userSubject.onNext(value)
                        },
                        {
                            Log.e(TAG, "We are in trouble: $it")
                        }
                    )
                    .toDisposables()
            }
            field = value
        }

    val userSubject: PublishSubject<User> = PublishSubject.create()

    companion object {
        const val TAG = "DashboardViewModel"
    }
}
