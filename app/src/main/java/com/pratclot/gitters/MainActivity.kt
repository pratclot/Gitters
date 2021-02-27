package com.pratclot.gitters

import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pratclot.core.CONNECTIVITY_CHANGE
import com.pratclot.core.GITHUB_API_KEY
import com.pratclot.di.TokenInterceptor
import com.pratclot.di.toastSubject
import com.pratclot.gitters.databinding.ActivityMainBinding
import com.pratclot.gitters.databinding.DialogApiKeyBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.HttpURLConnection
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val subscriptions = CompositeDisposable()

    @Inject
    lateinit var myBroadcastReceiver: MyBroadcastReceiver

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var tokenInterceptor: TokenInterceptor

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Gitters)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        registerBroadcastReceiver()
        subscribeToToastSubject()
    }

    private fun registerBroadcastReceiver() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
            addAction(CONNECTIVITY_CHANGE)
        }
        registerReceiver(myBroadcastReceiver, filter)
    }

    private fun subscribeToToastSubject() {
        subscriptions.add(
            toastSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        when (it) {
                            HttpURLConnection.HTTP_UNAUTHORIZED -> {
                                Toast.makeText(
                                    applicationContext,
                                    "Please supply your GitHub token via options menu",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                            }
                        }
                    },
                    {
                        Log.e(TAG, "We are in trouble: $it")
                    }
                )
        )
    }

    override fun onDestroy() {
        unregisterReceiver(myBroadcastReceiver)
        subscriptions.clear()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.optionsMenuSetKey -> {
                val dialogBinding = DialogApiKeyBinding.inflate(layoutInflater, binding.root, false)
                val dialog = AlertDialog.Builder(this)
                    .setView(dialogBinding.root)
                    .create()

                dialogBinding.apply {
                    buttonDialogApiKey.setOnClickListener {
                        val keyInput = inputDialogApiKey.editableText.toString()
                        sharedPreferences.edit()
                            .putString(GITHUB_API_KEY, keyInput)
                            .apply()
                        tokenInterceptor.apiToken = keyInput
                        Toast.makeText(applicationContext, "Token saved!", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()
                    }
                }

                dialog.show()
                true
            }
            else -> false
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
