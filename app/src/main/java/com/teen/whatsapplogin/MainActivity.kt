package com.teen.whatsapplogin

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.teen.waveerifysdk.WaVerifySdk
import com.teen.waveerifysdk.callback.WhatsappLoginCallback
import com.teen.waveerifysdk.model.SocketResponse
import com.teen.waveerifysdk.model.WASuccessResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.EOFException
import java.io.InterruptedIOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val whatsappLoginCallback: WhatsappLoginCallback = object : WhatsappLoginCallback {
            override fun onWhatsAppLoginSuccess(success: WASuccessResponse?) {
                this@MainActivity.runOnUiThread(Runnable {
                    findViewById<TextView>(R.id.textView)?.text = success?.name
                })
            }

            override fun onWhatsAppError(exception: Throwable) {
                    when(exception){
                        is EOFException ->{
                            WaVerifySdk.getInstance()?.stopSocket()
                            this@MainActivity.runOnUiThread(Runnable {
                                Toast.makeText(this@MainActivity,"Socket Unexpectedly closed",Toast.LENGTH_SHORT).show()
                            })

                        }
                    }
            }

        }
        WaVerifySdk.WaBuilder().context(this)
            .url("https://whatsapp-websocket.herokuapp.com/wasocket")
            .callback(whatsappLoginCallback).businessNumber("919717083714").message("Hello from Pennyup,Please send this message to verif").build()
        findViewById<TextView>(R.id.textView)?.setOnClickListener {
            onClickListener()
        }
    }

    private fun onClickListener() {
        GlobalScope.launch {
            WaVerifySdk.getInstance()?.verifyOtpService()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WaVerifySdk.getInstance()?.stopSocket()
        WaVerifySdk.close()
    }
}