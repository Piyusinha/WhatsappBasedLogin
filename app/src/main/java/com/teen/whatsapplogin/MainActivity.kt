package com.teen.whatsapplogin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.teen.waveerifysdk.WaVerifySdk
import com.teen.waveerifysdk.callback.WhatsappLoginCallback
import com.teen.waveerifysdk.model.WASuccessResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.EOFException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val whatsappLoginCallback: WhatsappLoginCallback = object : WhatsappLoginCallback {
            override fun onWhatsAppLoginSuccess(success: WASuccessResponse?) {
                this@MainActivity.runOnUiThread(
                    Runnable {
                        findViewById<AppCompatButton>(R.id.whatsapp_button)?.text = success?.name
                    }
                )
            }

            override fun onWhatsAppError(exception: Throwable) {
                when (exception) {
                    is EOFException -> {
                        WaVerifySdk.getInstance()?.stopSocket()
                        this@MainActivity.runOnUiThread(
                            Runnable {
                                Toast.makeText(this@MainActivity, "Socket Unexpectedly closed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
        WaVerifySdk.WASdkBuilder(this,"919717083714","https://whatsapp-socket-example.herokuapp.com/wasocket")
            .callback(whatsappLoginCallback).message("Hello from Pennyup,Please send this message to verify").build()
        if (WaVerifySdk.getInstance().isUsable()) {
            findViewById<AppCompatButton>(R.id.whatsapp_button)?.apply {
                isVisible = true
                setOnClickListener {
                    onClickListener()
                }
            }
        }
    }

    private fun onClickListener() {
        GlobalScope.launch {
            WaVerifySdk.getInstance().verifyOtpService()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WaVerifySdk.getInstance().onDestroy()
    }
}
