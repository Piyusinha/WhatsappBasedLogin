package com.teen.waveerifysdk.socketClient

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


internal class AuthorizationInterceptor(private val token : String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().signedRequest()
        return chain.proceed(newRequest)
    }

    private fun Request.signedRequest(): Request {
        return newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }
}