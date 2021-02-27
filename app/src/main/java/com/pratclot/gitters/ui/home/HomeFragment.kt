package com.pratclot.gitters.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.pratclot.core.HandyFragment
import com.pratclot.core.recycler.UserAdapter
import com.pratclot.core.recycler.UserItem
import com.pratclot.domain.User
import com.pratclot.gitters.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@AndroidEntryPoint
class HomeFragment : HandyFragment() {

    private val userAdapter = UserAdapter()

    private lateinit var homeViewModel: HomeViewModel

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e(TAG, "Creating fragment")

        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        subscribeToUserSubject()

        return root
    }

    private fun setupRecyclerView() {
        userAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    binding.apply {
                        progressBarFragmentHome.visibility = View.VISIBLE
                    }
                }
                is LoadState.Error, is LoadState.NotLoading -> {
                    binding.apply {
                        progressBarFragmentHome.visibility = View.GONE
                    }
                }
                else -> {}
            }
        }

        binding.recyclerViewFragmentHome.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun subscribeToUserSubject() {
        homeViewModel.usersSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { convertUserToUserItem(it) }
            .subscribe(
                {
                    userAdapter.submitData(lifecycle, it)
                },
                {
                    Log.e(TAG, "We are in trouble: $it")
                }
            )
            .toDisposables()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeViewModel.homeFragmentIsReady.onNext(true)
    }

    private fun convertUserToUserItem(it: PagingData<User>) =
        it.map { UserItem(it) { goToUserFragment(it) } }

    private fun goToUserFragment(it: User) {
        findNavController().navigate(
            HomeFragmentDirections.actionNavigationHomeToNavigationDashboard(
                it
            )
        )
    }

    override fun onDestroyView() {
        homeViewModel.homeFragmentIsReady.onNext(false)
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}
