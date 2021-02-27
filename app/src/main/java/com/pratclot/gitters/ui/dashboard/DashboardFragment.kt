package com.pratclot.gitters.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.pratclot.core.HandyFragment
import com.pratclot.domain.User
import com.pratclot.gitters.R
import com.pratclot.gitters.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@AndroidEntryPoint
class DashboardFragment : HandyFragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val args: DashboardFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e(TAG, "Creating fragment")

        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        dashboardViewModel.user = try {
            args.user
        } catch (ex: Exception) {
            null
        }

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        subscribeToUserSubject()

        return root
    }

    private fun subscribeToUserSubject() {
        dashboardViewModel.userSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    applyUserData(it)
                },
                {
                    Log.e(TAG, "We are in trouble: $it")
                }
            ).toDisposables()
    }

    private fun applyUserData(it: User) {
        binding.apply {
            Glide.with(this@DashboardFragment).load(it.avatar_url)
                .error(R.drawable.ic_baseline_error_outline_24)
                .into(imageViewFragmentDashboard)
            textViewFragmentDashboardLogin.text = it.login
            textViewFragmentDashboardUrl.apply {
                val htmlUrl = it.html_url
                text = htmlUrl
                setOnClickListener { goToUserProfile(htmlUrl) }
            }
            textViewFragmentDashboardLocation.text = it.location ?: "Location: unspecified"
        }
    }

    private fun goToUserProfile(htmlUrl: String) {
        findNavController().navigate(
            DashboardFragmentDirections.actionNavigationDashboardToNavigationNotifications(htmlUrl)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DashboardFragment"
    }
}
