package com.teen.whatsapplogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.teen.waveerifysdk.WaVerifySdk
import com.teen.model.SocketResponse
import com.teen.model.WASuccessResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import com.teen.waveerifysdk.callback.WhatsappLoginCallback

class MainActivity : AppCompatActivity() {
    private var liveData = MutableLiveData<SocketResponse>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initObserver()
        val whatsappLoginCallback : WhatsappLoginCallback = object : WhatsappLoginCallback {
            override fun onWhatsAppLoginSuccess(success: WASuccessResponse?) {
                this@MainActivity.runOnUiThread(Runnable {
                    findViewById<TextView>(R.id.textView)?.text = success?.name
                })
            }

            override fun onWhatsAppError(exception: Throwable) {

            }

        }
        WaVerifySdk.WaBuilder().url("https://whatsapp-websocket.herokuapp.com/wasocket").callback(whatsappLoginCallback).build()
        findViewById<TextView>(R.id.textView)?.setOnClickListener {
            onClickListener()
        }
    }

    private fun initObserver() {
        liveData.observe(this,{
            if(it.fullDocument?.entry?.get(0)?.changes?.get(0)?.value?.messages?.get(0)?.text?.body == "Hi for verification"){
                findViewById<TextView>(R.id.textView)?.text = "Verified"
            }

        })
    }

    private fun onClickListener() {
        GlobalScope.launch {
            WaVerifySdk.getInstance()?.verifyOtpService()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WaVerifySdk.getInstance()?.stopSocket()
    }
}