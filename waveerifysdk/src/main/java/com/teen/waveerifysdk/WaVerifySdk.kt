package com.teen.waveerifysdk

import android.content.Context
import com.teen.waveerifysdk.callback.WhatsappLoginCallback
import com.teen.waveerifysdk.socketClient.SocketConnectionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.withContext
import com.teen.waveerifysdk.utils.WaSdkUtils
import com.teen.waveerifysdk.utils.WaSdkUtils.Companion.getToken
import com.teen.waveerifysdk.utils.WaSdkUtils.Companion.onGetResponse
import com.teen.waveerifysdk.utils.WaSdkUtils.Companion.uniqueId
import com.teen.waveerifysdk.utils.WaSdkUtils.Companion.verifyMessage


class WaVerifySdk private constructor(builder: WASdkBuilder) {
    private val url: String? = builder.url
    private var callback: WhatsappLoginCallback?
    private val uniqueID:String = uniqueId()
    private val jwtKey = builder.jwtSecretKey
    private var socket :SocketConnectionProvider? = SocketConnectionProvider(getToken(jwtKey,identifier = uniqueID))
    private val context: Context?
    private val businessNumber:String
    private val message:String
    private var socketStarted = false

    init {
        this.callback = builder.callback
        this.context = builder.context
        this.businessNumber = builder.businessNumber
        this.message = builder.message
    }

    companion object {
        private var INSTANCE: WaVerifySdk? = null

        fun initWhatsAppSdk(builder: WASdkBuilder) {
               synchronized(this) { INSTANCE = WaVerifySdk(builder) }
        }


        fun getInstance(): WaVerifySdk {
            return INSTANCE ?: throw RuntimeException("Please call initWhatsAppSdk() on WhatsAppSDK first")
        }
        fun close() {
            INSTANCE = null
        }
    }


    class WASdkBuilder(var context: Context?,var businessNumber: String,var url: String?, ) {

        var callback: WhatsappLoginCallback? = null
            private set

        var message: String = ""
            private set

        var jwtSecretKey: String = ""
            private set

        fun callback(callback: WhatsappLoginCallback) = apply { this.callback = callback }

        fun message(msg:String) = apply { this.message = msg }

        fun jwtSecretKey(key:String) = apply { this.jwtSecretKey = jwtSecretKey }

        fun build() {
            if(jwtSecretKey.isNotEmpty() && jwtSecretKey.length < 32){
                throw RuntimeException("Secret Key must be length of 32")
            }else{
                initWhatsAppSdk(this)
            }

        }
    }

    @ExperimentalCoroutinesApi
    suspend fun verifyOtpService() {
        WaSdkUtils.startWhatsApp(context,message,businessNumber,uniqueID)
        withContext(Dispatchers.IO) {
            if (url != null && !socketStarted) {
                socketStarted = true
                socket?.startSocket(url)?.consumeEach {
                    if (it.exception == null) {
                        if(verifyMessage(it.text,uniqueID)) {
                            callback?.onWhatsAppLoginSuccess(onGetResponse(it.text))
                        }
                    } else {
                        socketStarted = false
                        socket?.stopSocket()
                        callback?.onWhatsAppError(it.exception)
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun stopSocket(){
        socket?.stopSocket()
    }

    fun onDestroy(){
        stopSocket()
        close()
        socket?.onDestroy()
    }

    fun isUsable(): Boolean {
       return WaSdkUtils.isUsable(context)
    }
    fun getJWTToken(): String? {
        return socket?.getJWTtoken()
    }
}
