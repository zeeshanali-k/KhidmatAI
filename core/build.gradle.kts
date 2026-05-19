@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.koin.compiler)
}

kotlin {
    androidLibrary {
        namespace = "com.corestack.khidmatai.core"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    
    iosArm64()
    iosSimulatorArm64()
    
    wasmJs { 
        browser() 
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
            
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
            
            implementation(libs.multiplatform.settings)
//            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.multiplatform.settings.no.arg)
        }
        
        androidMain.dependencies { 
            implementation(libs.ktor.client.okhttp) 
        }
        
        iosMain.dependencies { 
            implementation(libs.ktor.client.native) 
        }
        
        wasmJsMain.dependencies { 
            implementation(libs.ktor.client.js) 
        }
    }
}
