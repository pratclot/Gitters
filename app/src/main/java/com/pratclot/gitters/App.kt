package com.pratclot.gitters

import android.app.Application
import android.content.SharedPreferences
import com.pratclot.core.GITHUB_API_KEY
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var sharedPrefs: SharedPreferences
    override fun onCreate() {
        super.onCreate()

        val savedKey = sharedPrefs.getString(GITHUB_API_KEY, "") ?: ""

        if (savedKey.isBlank()) {
            sharedPrefs.edit()
                .putString(GITHUB_API_KEY, "")
                .apply()
        }
    }
}
