package com.example.relax.models.network

import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Module
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("x-rapidapi-key", "77b6145f63msh1af2f53b56a39e9p1a59d9jsn4cba9b5c9ba4")
                    .addHeader("x-rapidapi-host", "booking-com15.p.rapidapi.com")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://booking-com15.p.rapidapi.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideAPIService(retrofit: Retrofit): APIService{
        return retrofit.create(APIService::class.java)
    }
}