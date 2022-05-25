package com.teen.waveerifysdk

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import com.google.gson.Gson
import com.teen.waveerifysdk.callback.WhatsappLoginCallback
import com.teen.waveerifysdk.model.SocketResponse
import com.teen.waveerifysdk.model.WASuccessResponse
import com.teen.waveerifysdk.socketClient.SocketConnectionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.withContext
import java.util.*


class WaVerifySdk private constructor(builder: WaBuilder) {
    private val url: String?
    private var callback: WhatsappLoginCallback?
    private val socket = SocketConnectionProvider()
    private val context: Context?
    private val businessNumber: String
    private val message: String
    private val uniqueID: String
    private var socketStarted = false

    init {
        this.url = builder.url
        this.callback = builder.callback
        this.context = builder.context
        this.businessNumber = builder.businessNumber
        this.message = builder.message
        this.uniqueID = uniqueId()
    }

    companion object {
        private var INSTANCE: WaVerifySdk? = null

        fun initWhatsAppSdk(builder: WaBuilder) {
            synchronized(this) { INSTANCE = WaVerifySdk(builder) }
        }

        fun getInstance(): WaVerifySdk {
            return INSTANCE
                ?: throw RuntimeException("Please call initWhatsAppSdk() on WhatsAppSDK first")
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

        var context: Context? = null
            private set

        var businessNumber: String = ""
            private set

        var message: String = ""
            private set

        fun url(url: String) = apply { this.url = url }

        fun callback(callback: WhatsappLoginCallback) = apply { this.callback = callback }

        fun context(context: Context?) = apply { this.context = context }

        fun businessNumber(number: String) = apply { this.businessNumber = number }

        fun message(msg: String) = apply { this.message = msg }

        fun build() = initWhatsAppSdk(this)
    }

    @ExperimentalCoroutinesApi
    suspend fun verifyOtpService() {
        startWhatsApp()
        withContext(Dispatchers.IO) {
            if (url != null && !socketStarted) {
                socketStarted = true
                socket.startSocket(url).consumeEach {
                    if (it.exception == null) {
                        if (verifyMessage(it.text)) callback?.onWhatsAppLoginSuccess(onGetResponse(
                            it.text))
                    } else {
                        socketStarted = false
                        socket.stopSocket()
                        callback?.onWhatsAppError(it.exception)
                    }
                }
            }
        }
    }

    private fun verifyMessage(text: String?): Boolean {
        val data = Gson().fromJson(text, SocketResponse::class.java)
        val message =
            data.fullDocument?.entry?.get(0)?.changes?.get(0)?.value?.messages?.get(0)?.text?.body
        if (message?.contains("#") == true) {
            val keyStart = message.substring(message.indexOf("#") + 1)
            if (keyStart.contains("#")) {
                val requiredData = keyStart.indexOf("#").let { keyStart.substring(0, it) }
                if (requiredData == uniqueID) {
                    return true
                }
            }
        }
        return false
    }

    private fun startWhatsApp() {
        val waApp = Intent(Intent.ACTION_VIEW)
        waApp.data = Uri.parse(getUri())
        context?.startActivity(waApp)
    }

    private fun getUri(): String {
        return if (message.isNotEmpty()) {
            URL.replace("{phoneNumber}", businessNumber).replace("{sessionID}", uniqueID)
                .replace("{message}", message)
        } else {
            URL.replace("{phoneNumber}", businessNumber).replace("{sessionID}", uniqueID)
                .replace("{message}", MESSAGE)
        }

    }

    private fun onGetResponse(text: String?): WASuccessResponse {
        val data = Gson().fromJson(text, SocketResponse::class.java)
        val contactInfo = data.fullDocument?.entry?.get(0)?.changes?.get(0)?.value?.contacts?.get(0)
        return WASuccessResponse(contactInfo?.profile?.name, contactInfo?.waId)
    }

    private fun uniqueId(): String = UUID.randomUUID().toString()

    @ExperimentalCoroutinesApi
    fun stopSocket() {
        socket.stopSocket()
    }

    fun onDestroy() {
        stopSocket()
        close()
        socket.onDestroy()
    }

    fun isUsable(): Boolean {
        if (context == null) {
            throw RuntimeException("Please call initWhatsAppSdk() on WhatsAppSDK first")
        }
        return try {
            context.packageManager?.getApplicationInfo(WHATSAPP_PKG, 0)
            true
        } catch (e: NameNotFoundException) {
            false
        }
    }
}
