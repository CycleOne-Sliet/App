package com.cycleone.cycleoneapp.services

import android.app.Application
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class CachedNetworkClient(application: Application) {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .cache(
            Cache(
                directory = File(application.cacheDir, "http_cache"),
                // $0.05 worth of phone storage in 2020
                maxSize = 50L * 1024L * 1024L // 50 MiB
            )
        )
        .build()

    fun get(url: String): okhttp3.Response {
        return client.newCall(
            Request.Builder().get().url(url).addHeader("User-Agent", "CycleOne").build()
        ).execute()
    }

    companion object {
        private lateinit var c: CachedNetworkClient

        fun initialize(application: Application) {
            c = CachedNetworkClient(application)
        }

        fun get(url: String): okhttp3.Response {
            return c.get(url)
        }
    }
}