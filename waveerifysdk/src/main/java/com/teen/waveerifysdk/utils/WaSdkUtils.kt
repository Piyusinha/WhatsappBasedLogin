package com.teen.waveerifysdk.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.google.gson.Gson
import com.teen.waveerifysdk.MESSAGE
import com.teen.waveerifysdk.URL
import com.teen.waveerifysdk.WHATSAPP_PKG
import com.teen.waveerifysdk.model.SocketResponse
import com.teen.waveerifysdk.model.WASuccessResponse
import java.util.*

internal class WaSdkUtils {
    companion object{
        fun isUsable(context: Context?): Boolean {
            if(context == null) {
                throw RuntimeException("Please call initWhatsAppSdk() on WhatsAppSDK first")
            }
            return try {
                context.packageManager?.getApplicationInfo(WHATSAPP_PKG, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
         private fun getUri(message: String, businessNumber: String, uniqueID: String): String {
            return if(message.isNotEmpty()){
                URL.replace("{phoneNumber}",businessNumber).replace("{sessionID}",uniqueID).replace("{message}",message)
            }else{
                URL.replace("{phoneNumber}",businessNumber).replace("{sessionID}",uniqueID).replace("{message}", MESSAGE)
            }
        }
        fun uniqueId():String = UUID.randomUUID().toString()

        fun startWhatsApp(
            context: Context?,
            message: String,
            businessNumber: String,
            uniqueID: String
        ) {
            val waApp = Intent(Intent.ACTION_VIEW)
            waApp.data = Uri.parse(getUri(message,businessNumber,uniqueID))
            context?.startActivity(waApp)
        }
        fun verifyMessage(text: String?, uniqueID: String): Boolean {
            val data = Gson().fromJson(text, SocketResponse::class.java)
            val message = data.fullDocument?.entry?.get(0)?.changes?.get(0)?.value?.messages?.get(0)?.text?.body
            if(message?.contains("#") == true) {
                val keyStart = message.substring(message.indexOf("#")+1)
                if(keyStart.contains("#")) {
                    val requiredData = keyStart.indexOf("#").let { keyStart.substring(0, it) }
                    if(requiredData == uniqueID){
                        return true
                    }
                }
            }
            return false
        }
        fun onGetResponse(text: String?): WASuccessResponse {
            val data = Gson().fromJson(text,SocketResponse::class.java)
            val contactInfo = data.fullDocument?.entry?.get(0)?.changes?.get(0)?.value?.contacts?.get(0)
            return WASuccessResponse(contactInfo?.profile?.name,contactInfo?.waId)
        }
    }
}