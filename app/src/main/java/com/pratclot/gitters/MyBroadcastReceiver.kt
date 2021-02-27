package com.pratclot.gitters

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.pratclot.core.CONNECTIVITY_CHANGE
import com.pratclot.di.internetSubject
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyBroadcastReceiver @Inject constructor(@ApplicationContext val context: Context) :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.let {
            when (it) {
                CONNECTIVITY_CHANGE -> {
                    val networkAvailable = isNetworkAvailable()
                    internetSubject.onNext(networkAvailable)
                }
                else -> {
                }
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    companion object {
        const val TAG = "MyBroadcastReceiver"
    }
}
