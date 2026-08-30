package com.example.analyticsandroidexample

import android.app.Application
import com.example.core.analytics.AnalyticsTracker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AnalyticsApp : Application() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    override fun onCreate() {
        super.onCreate()
        setupAnalytics()
    }

    private fun setupAnalytics() {
        // GEMINI.md: Global parameters are injected centrally using providers
        analyticsTracker.setProviders(
            {
                mapOf(
                    "platform" to "android",
                    "app_version" to "1.0.0",
                    "environment" to "production"
                )
            }
        )
    }
}
