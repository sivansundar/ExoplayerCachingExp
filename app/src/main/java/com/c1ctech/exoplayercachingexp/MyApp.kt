package com.c1ctech.exoplayercachingexp

import android.app.Application
import com.google.android.exoplayer2.offline.StreamKey
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Log
import java.io.File

class MyApp : Application() {

    companion object {
       // lateinit var simpleCache: SimpleCache
      //  const val exoPlayerCacheSize: Long = 50 * 1024
      //  lateinit var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor

       // lateinit var cacheDataSourceFactory: DataSource.Factory
        //lateinit var httpDataSourceFactory: HttpDataSource.Factory

        val cacheStreamKeys = arrayListOf(
            StreamKey(0, 1),
            StreamKey(1, 1),
            StreamKey(2, 1),
            StreamKey(3, 1),
            StreamKey(4, 1)
        )
    }

    override fun onCreate() {
        super.onCreate()
//        leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
//
//
//        httpDataSourceFactory = DefaultHttpDataSourceFactory("Android")
//
//        httpDataSourceFactory.setDefaultRequestProperty("Cookie", PlayerActivity.cookieValue)

//        cacheDataSourceFactory =
//            CacheDataSourceFactory(
//                simpleCache,
//                httpDataSourceFactory,
//                FileDataSource.Factory(),
//                CacheDataSinkFactory(simpleCache, CacheDataSink.DEFAULT_FRAGMENT_SIZE),
//                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
//                object : CacheDataSource.EventListener {
//                    override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
//                        Log.d(
//                            "CacheRead",
//                            "onCachedBytesRead. cacheSizeBytes:$cacheSizeBytes, cachedBytesRead: $cachedBytesRead"
//                        )
//                    }
//
//                    override fun onCacheIgnored(reason: Int) {
//                        Log.d("CacheRead", "onCacheIgnored. reason:$reason")
//                    }
//                }
//            )
//    }
    }
}