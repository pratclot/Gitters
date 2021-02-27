package com.pratclot.service

import com.pratclot.domain.User
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {
    @GET("users")
    fun getUsers(
        @Query("since") since: Int,
        @Query("per_page") per_page: Int,
    ): Single<List<User>>

    @GET("user")
    fun getUser(@Query("username") username: String): Single<User>
}
