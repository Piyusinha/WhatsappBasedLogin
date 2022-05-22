<h1 align="left"> <img align="left" alt="Whatsapp" width="50px" src="https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/WhatsApp.svg/512px-WhatsApp.svg.png"/>Whatsapp Based Login</h1></br>
<p align="left">
  Try this to implement fastest phone number verification system [⚡]
</p>

<p align="center">
  <a href="https://www.linkedin.com/[removed]" rel="nofollow noreferrer">
    <img src="https://i.stack.imgur.com/gVE0j.png" alt="linkedin"> LinkedIn
  </a><br/>
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=26"><img alt="API" src="https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/Piyusinha"><img alt="Profile" src="https://img.shields.io/badge/Github%20-Piyu.svg"/></a>
</p> 

# Before Proceeding Further
<p align="left">
  Please go to this link and follow the steps to setup server for whatsapp verification
</p>
<a href="https://github.com/Piyusinha/WA-Webhook">Link to Server Repo</a>

# Now Let's Proceed Further

[![](https://jitpack.io/v/Piyusinha/WhatsappBasedLogin.svg)](https://jitpack.io/#Piyusinha/WhatsappBasedLogin)

### Gradle

Add the JitPack repo to your **project**'s `build.gradle` file:

```
maven { url 'https: //jitpack.io' }
```
Add the dependency below to your **module**'s `build.gradle` file:

```gradle
dependencies {
	   implementation 'com.github.Piyusinha:WhatsappBasedLogin:v1.0.0'
	}
```
## How to Use
WhatsAppBasedLogin supports both Kotlin and Java projects, so you can reference it by your language.

### Create WhatsappLogin with Kotlin DSL
```kotlin
 WaVerifySdk.WaBuilder()
 .context(this)
 .url(socketURL)
 .callback(whatsappLoginCallback)
 .businessNumber(businessNumber)
 .message(customMessage)
 .build()
```
Here, whatsappLoginCallback is an interface that you need to define in your app where you would get the success or failure callbacks

### You can trigger the Whatsapp verification flow by calling the following method
```kotlin
GlobalScope.launch {
  WaVerifySdk.getInstance()?.verifyOtpService()
 }
```
Note: Socket Runs on Background Thread so calling this method require **Coroutine Scope**.

### Implements Callback
```kotlin
val whatsappLoginCallback: WhatsappLoginCallback = object : WhatsappLoginCallback {
  override fun onWhatsAppLoginSuccess(success: WASuccessResponse?) {
      this@MainActivity.runOnUiThread({
          findViewById<TextView>(R.id.textView)?.text = success?.name
      })    
  }
  
  override fun onWhatsAppError(exception: Throwable) {
  
    }
} 
```
Callback coming from Background thread so it's important to pass it to main thread

### Clearing SDK instance (Important)
```kotlin
override fun onDestroy() {
  super.onDestroy()
  WaVerifySdk.getInstance()?.onDestroy()
}
```
Note: It's important to Destory SDK instance beacause it's close the socket connection.


# License
```xml
Copyright 2022 lucifer (Piyush Sinha)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
