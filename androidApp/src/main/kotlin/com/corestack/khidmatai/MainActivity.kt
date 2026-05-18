package com.corestack.khidmatai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.corestack.khidmatai.di.KhidmatAIKoinApp
import org.koin.plugin.module.dsl.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}
//
//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}