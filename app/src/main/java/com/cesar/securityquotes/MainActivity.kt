package com.cesar.securityquotes

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cesar.securityquotes.ui.SecurityQuotesApp
import com.cesar.securityquotes.ui.SecurityQuotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: SecurityQuotesViewModel = viewModel(factory = SecurityQuotesViewModel.Factory(applicationContext))
            SecurityQuotesApp(vm)
        }
    }
}
