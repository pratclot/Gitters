package com.pratclot.repo

import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.pratclot.domain.User
import com.pratclot.service.GithubApi
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GithubPagingSource @Inject constructor(
    private val githubApi: GithubApi,
) : RxPagingSource<Int, User>() {
    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, User>> {
        // Start refresh at page 1 if undefined.
        val nextPageNumber: Int = params.key ?: 0
        val limit = params.loadSize

        return githubApi.getUsers(nextPageNumber, limit)
            .subscribeOn(Schedulers.io())
            .map { toLoadResult(it, limit) }
            .onErrorReturn { LoadResult.Error(it) }
    }

    private fun toLoadResult(
        response: List<User>,
        limit: Int
    ): LoadResult<Int, User> {
        return LoadResult.Page(
            response,
            null, // Only paging forward.
            response.last().id,
        )
    }
}
