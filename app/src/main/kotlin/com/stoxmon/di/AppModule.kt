package com.stoxmon.di

import com.stoxmon.data.api.StoxmonApi
import com.stoxmon.data.repository.*
import com.stoxmon.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(StoxmonApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideStoxmonApi(retrofit: Retrofit): StoxmonApi {
        return retrofit.create(StoxmonApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideTickerRepository(api: StoxmonApi): TickerRepository {
        return TickerRepositoryImpl(api)
    }
    
    @Provides
    @Singleton
    fun provideNewsRepository(api: StoxmonApi): NewsRepository {
        return NewsRepositoryImpl(api)
    }
    
    @Provides
    @Singleton
    fun providePortfolioRepository(): PortfolioRepository {
        return PortfolioRepositoryImpl()
    }
}
