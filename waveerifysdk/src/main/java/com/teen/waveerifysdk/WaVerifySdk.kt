package com.teen.waveerifysdk

import com.google.gson.Gson
import com.teen.model.SocketResponse
import com.teen.model.WASuccessResponse
import com.teen.waveerifysdk.callback.WhatsappLoginCallback
import com.teen.waveerifysdk.socketClient.SocketConnectionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.withContext


class WaVerifySdk private constructor(builder: WaBuilder) {
    private val url: String?
    private var callback: WhatsappLoginCallback?
    private val socket = SocketConnectionProvider()

    init {
        this.url = builder.url
        this.callback = builder.callback
    }

    companion object {
        @Volatile
        private var INSTANCE: WaVerifySdk? = null

        fun initWhatsAppSdk(builder: WaBuilder) {
               synchronized(this) { INSTANCE = WaVerifySdk(builder) }
        }

        fun getInstance(): WaVerifySdk? {
            return if (INSTANCE != null) {
                INSTANCE
            } else {
                throw RuntimeException("Please call initWhatsAppSdk() on WhatsAppSDK first")
            }
        }
        fun close() {
            INSTANCE = null
        }
    }

    class WaBuilder {
        var url: String? = null
            private set

        var callback: WhatsappLoginCallback? = null
            private set

        fun url(url: String) = apply { this.url = url }

        fun callback(callback: WhatsappLoginCallback) = apply { this.callback = callback }

        fun build() = initWhatsAppSdk(this)
    }

    @ExperimentalCoroutinesApi
    suspend fun verifyOtpService() {
        return withContext(Dispatchers.IO) {
            if (url != null) {
                socket.startSocket(url).consumeEach {
                    if (it.exception == null) {
                        callback?.onWhatsAppLoginSuccess(onGetResponse(it.text))
                    }else{
                        callback?.onWhatsAppError(it.exception)
                    }
                }
            }
        }
    }

    private fun onGetResponse(text: String?): WASuccessResponse? {
        val data = Gson().fromJson(text,SocketResponse::class.java)
        val contactInfo = data.fullDocument?.entry?.get(0)?.changes?.get(0)?.value?.contacts?.get(0)
        return WASuccessResponse(contactInfo?.profile?.name,contactInfo?.waId)
    }

    @ExperimentalCoroutinesApi
    fun stopSocket(){
        socket.stopSocket()
    }
}
