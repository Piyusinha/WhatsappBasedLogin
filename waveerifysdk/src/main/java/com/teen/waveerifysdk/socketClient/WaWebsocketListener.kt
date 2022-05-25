package com.teen.waveerifysdk.socketClient

import com.teen.waveerifysdk.socketClient.SocketConnectionProvider.Companion.NORMAL_CLOSURE_STATUS
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

@ExperimentalCoroutinesApi
internal class WaWebSocketListener : WebSocketListener() {

    val socketEventChannel: Channel<SocketUpdate> = Channel(10)

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocket.send("Hi")
    }

    @DelicateCoroutinesApi
    override fun onMessage(webSocket: WebSocket, text: String) {
        GlobalScope.launch {
            socketEventChannel.send(SocketUpdate(text))
        }
    }

    @DelicateCoroutinesApi
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        GlobalScope.launch {
            socketEventChannel.send(SocketUpdate(exception = SocketAbortedException()))
        }
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        socketEventChannel.close()
    }

    @DelicateCoroutinesApi
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        GlobalScope.launch {
            socketEventChannel.send(SocketUpdate(exception = t))
        }
    }

}

class SocketAbortedException : Exception()

data class  SocketUpdate(
    val text: String? = null,
    val byteString: ByteString? = null,
    val exception: Throwable? = null,
)