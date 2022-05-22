package com.teen.waveerifysdk.socketClient

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

 internal class SocketConnectionProvider {

    private var _webSocket: WebSocket? = null

    private val socketOkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(39, TimeUnit.SECONDS)
        .hostnameVerifier { _, _ -> true }
        .build()

    @ExperimentalCoroutinesApi
    private var _webSocketListener: WaWebSocketListener? = null

    @ExperimentalCoroutinesApi
    fun startSocket(url: String): Channel<SocketUpdate> =
        with(WaWebSocketListener()) {
            startSocket(this,url)
            this@with.socketEventChannel
        }

    @ExperimentalCoroutinesApi
    fun startSocket(webSocketListener: WaWebSocketListener,url:String) {
        _webSocketListener = webSocketListener
        _webSocket = socketOkHttpClient.newWebSocket(
            Request.Builder().url(url).build(),
            webSocketListener
        )
        socketOkHttpClient.dispatcher.executorService.shutdown()
    }

    @ExperimentalCoroutinesApi
    fun stopSocket() {
        try {
            _webSocket?.close(NORMAL_CLOSURE_STATUS, null)
            _webSocket = null
            _webSocketListener?.socketEventChannel?.close()
            _webSocketListener = null
        } catch (ex: Exception) {
        }
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }

}