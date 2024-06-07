package com.example.harryerayaudiorecorder

import AudioViewModel
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun OAuthWebViewScreen(
    clientId: String,
    redirectUri: String,
    scope: String,
    onCodeReceived: (String) -> Unit,
) {
    val authUrl = "https://freesound.org/apiv2/oauth2/authorize/?client_id=$clientId&response_type=code&redirect_uri=$redirectUri&scope=$scope"

    AndroidView(factory = { context ->
        WebView(context).apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url.toString()
                    if (url.startsWith(redirectUri)) {
                        val uri = Uri.parse(url)
                        val code = uri.getQueryParameter("code")
                        if (code != null) {
                            onCodeReceived(code)
                        }
                        return true // Intercept URL loading
                    }
                    return false // Allow WebView to load other URLs
                }
            }
            loadUrl(authUrl)
        }
    })
}

@Composable
fun authenticate(
    audioViewModel: AudioViewModel,
    setShowOAuthWebView: (Boolean) -> Unit,
    context: Context,
    onAuthenticated: (String) -> Unit, // This is the lambda callback,
    modifier: Modifier
) {
    val tokenState = remember { mutableStateOf<String?>(null) }

    OAuthWebViewScreen(
        clientId = "iwKIY7IGaujDQhiLWj8m",
        redirectUri = "http://freesound.org/home/app_permissions/permission_granted/",
        scope = "read write", // Ensure you include the write scope
        onCodeReceived = { code ->
            Log.d("authcode", code)

            audioViewModel.exchangeCode(
                clientId = "iwKIY7IGaujDQhiLWj8m",
                clientSecret = "DFYwiCdqrNbhB9RFGiENSXURVlF30uGFrGcLMFWy", // Ensure this is kept secure
                code = code,
                redirectUri = "http://freesound.org/home/app_permissions/permission_granted/",
                callback = object : Callback<TokenResponse> {
                    override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                tokenState.value = it.accessToken
                                Log.d("token", "Access Token: ${it.accessToken}")
                                audioViewModel.setAccessToken(context, it.accessToken) // Store the access token
                                onAuthenticated(it.accessToken) // Call the lambda with the access token
                            }
                        } else {
                            Log.e("API Error", "Token exchange failed: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Log.e("API Error", "Token exchange error: ${t.message}")
                    }
                }
            )

            setShowOAuthWebView(false)
        }
    )
}

@Composable
fun ApiResponseDialog(audioViewModel: AudioViewModel) {
    audioViewModel.apiResponse.value?.let { apiResponse ->
        AlertDialog(
            onDismissRequest = {
                audioViewModel.clearApiResponseMessage()
            },
            title = {
                Text(text = if (apiResponse.second) "Success" else "Error")
            },
            text = {
                Text(apiResponse.first)
            },
            confirmButton = {
                Button(onClick = {
                    audioViewModel.clearApiResponseMessage()
                }) {
                    Text("OK")
                }
            }
        )
    }
}

//                tokenState.value?.let { audioViewModel.downloadSound("738484", it, context) }
