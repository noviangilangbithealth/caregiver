package com.siloamhospitals.siloamcaregiver.network.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {

        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val getInstance: ApiService by lazy {
            retrofit.create(ApiService::class.java)
        }

        private val BASE_URL = "https://uat-mysiloam-api.siloamhospitals.com"
//        private val BASE_URL = "http://10.85.139.102:3344"
//        private val BASE_URL = "https://mysiloam-api-staging.siloamhospitals.com"

    }

}
