package com.c1ctech.exoplayercachingexp

import android.app.Application
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Log
import java.io.File

class MyApp : Application() {

    companion object {
        lateinit var simpleCache: SimpleCache
        const val exoPlayerCacheSize: Long = 90 * 1024 * 1024
        lateinit var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor
        lateinit var exoDatabaseProvider: ExoDatabaseProvider

        lateinit var cacheDataSourceFactory: CacheDataSourceFactory
        lateinit var httpDataSourceFactory : DefaultHttpDataSourceFactory

        lateinit var defaultDataSourceFactory: DefaultDataSourceFactory

    }

    override fun onCreate() {
        super.onCreate()
        leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
        exoDatabaseProvider = ExoDatabaseProvider(this)
        simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, exoDatabaseProvider)

        httpDataSourceFactory = DefaultHttpDataSourceFactory("Android")

        val defaultBandwidthMeter = DefaultBandwidthMeter.Builder(this).build()

        defaultDataSourceFactory = DefaultDataSourceFactory(
            this, defaultBandwidthMeter, httpDataSourceFactory
        )

        cacheDataSourceFactory =
            CacheDataSourceFactory(
                simpleCache,
                defaultDataSourceFactory,
                FileDataSource.Factory(),
                CacheDataSinkFactory(simpleCache, CacheDataSink.DEFAULT_FRAGMENT_SIZE),
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                object : CacheDataSource.EventListener {
                    override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
                        Log.d(
                            "Cache",
                            "onCachedBytesRead. cacheSizeBytes:$cacheSizeBytes, cachedBytesRead: $cachedBytesRead"
                        )
                    }

                    override fun onCacheIgnored(reason: Int) {
                        Log.d("Cache", "onCacheIgnored. reason:$reason")
                    }
                }
            )
    }
}