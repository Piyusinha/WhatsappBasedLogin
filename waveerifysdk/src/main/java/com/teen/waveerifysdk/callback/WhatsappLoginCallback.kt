package com.teen.waveerifysdk.callback

import com.teen.waveerifysdk.model.WASuccessResponse


interface WhatsappLoginCallback {
    fun onWhatsAppLoginSuccess(success: WASuccessResponse?)
    fun onWhatsAppError(exception: Throwable)
}