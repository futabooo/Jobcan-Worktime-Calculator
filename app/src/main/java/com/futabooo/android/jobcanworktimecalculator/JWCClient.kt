package com.futabooo.android.jobcanworktimecalculator

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

class JWCClient(private val context: Context) {

  private val cookieJar: PersistentCookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
  private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
      .cookieJar(cookieJar)
      .build()
  private val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl("https://ssl.jobcan.jp")
      .addCallAdapterFactory(CoroutineCallAdapterFactory())
      .client(okHttpClient)
      .build()

  fun login(clientId: String, email: String, password: String): Deferred<ResponseBody> {
    return retrofit.create(JWCService::class.java)
        .login(clientId = clientId, email = email, password = password)
  }

  fun attendance(): Deferred<ResponseBody> {
    return retrofit.create(JWCService::class.java)
        .attendance()
  }

  interface JWCService {

    @FormUrlEncoded
    @POST("/login/pc-employee/old")
    fun login(
      @Field("client_id") clientId: String,
      @Field("email") email: String,
      @Field("password") password: String
    ): Deferred<ResponseBody>

    @GET("/employee/attendance")
    fun attendance(): Deferred<ResponseBody>
  }
}