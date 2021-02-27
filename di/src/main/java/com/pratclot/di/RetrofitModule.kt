package com.pratclot.di

import android.content.SharedPreferences
import com.pratclot.core.GITHUB_API_KEY
import com.pratclot.service.GithubApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideGithubApi(okHttpClient: OkHttpClient, moshi: Moshi): GithubApi {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
            .create(GithubApi::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        tokenInterceptor: TokenInterceptor,
        unauthorizedInterceptor: UnauthorizedInterceptor
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor().also {
                    it.level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .addInterceptor(tokenInterceptor)
            .addInterceptor(unauthorizedInterceptor)
            .build()
    }

    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .build()
    }
}

@Singleton
class TokenInterceptor @Inject constructor(val sharedPreferences: SharedPreferences) : Interceptor {
    var apiToken = sharedPreferences.getString(GITHUB_API_KEY, "") ?: ""
        set(value) {
            tokenSubject.onNext(true)
            field = value
        }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("authorization", "bearer $apiToken")
            .build()

        return chain.proceed(request)
    }
}

@Singleton
class UnauthorizedInterceptor @Inject constructor() :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            toastSubject.onNext(response.code)
        }
        return response
    }
}

val toastSubject: PublishSubject<Int> = PublishSubject.create()
val tokenSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(true)
val internetSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
