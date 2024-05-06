package com.siloamhospitals.siloamcaregiver.di


import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.siloamhospitals.siloamcaregiver.BuildConfig
import com.siloamhospitals.siloamcaregiver.ext.common.applyIf
import com.siloamhospitals.siloamcaregiver.network.NetworkInterceptor
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException

private const val REQUEST_TIMEOUT = 30L

val Context.dataModule
    get() = module {
        single { AppPreferences(applicationContext) }
        single { Moshi.Builder().build() }
        single {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val builder =
                getUnsafeOkHttpClient().addInterceptor(NetworkInterceptor(applicationContext))
                    .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                    .applyIf(BuildConfig.DEBUG) {
                        addInterceptor(ChuckerInterceptor.Builder(applicationContext).build())
                        addInterceptor(httpLoggingInterceptor)
                    }

            builder.build()
        }


        single {
            val builder = GsonBuilder()
                .setLenient()
                .create()

            GsonConverterFactory.create(builder)
        }

        single {
            Retrofit.Builder()
                .baseUrl(BuildConfig.URL_CAREGIVER)
                .client(get())
                .addConverterFactory(get())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
        }

        single { Repository(get()) }

    }


fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
    try {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier(hostnameVerifier = { _, _ -> true })

        return builder
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}
