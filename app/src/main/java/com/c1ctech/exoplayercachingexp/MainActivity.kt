package com.c1ctech.exoplayercachingexp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.StreamKey
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.hls.offline.HlsDownloader
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import java.io.IOException
import android.R.string.no
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var httpDataSourceFactory: HttpDataSource.Factory
    private lateinit var defaultDataSourceFactory: DefaultDataSourceFactory
    private lateinit var cacheDataSourceFactory: DataSource.Factory
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private val simpleCache: SimpleCache = MyApp.simpleCache

    private lateinit var playerView: PlayerView
    private var hlsDownloader : HlsDownloader? = null

    private val videoURL =  "https://media.colearn.id/videos/transcoded/M0500101E001/hls/M0500101E001.m3u8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get PlayerView by its id
        playerView = findViewById(R.id.playerView)

    }

    private fun initPlayer() {

        httpDataSourceFactory = DefaultHttpDataSource.Factory().setUserAgent("userAgent").setDefaultRequestProperties(
            mapOf("Cookie" to cookie))

        defaultDataSourceFactory = DefaultDataSourceFactory(
            applicationContext, httpDataSourceFactory
        )


        val mediaItem = MediaItem.Builder().setUri(Uri.parse(videoURL)).setCustomCacheKey("custom_cache").build()
        val hlsDownloadHelper = DownloadHelper.forMediaItem(this, mediaItem, DefaultRenderersFactory(this), httpDataSourceFactory)

            val prepareCallback = object : DownloadHelper.Callback {
                override fun onPrepared(helper: DownloadHelper) {

                    Log.d("ExoCache", "onPrepare start")
                    if (cacheStreamKeys.isNotEmpty()) {
//                        val cacheDataSourceFactory = CacheDataSource.Factory()
//                            .setCache(simpleCache)
//                            .setUpstreamDataSourceFactory(
//                                defaultDataSourceFactory
//                            )

                        // Create a downloader for the first variant in a master playlist.
//                         hlsDownloader = HlsDownloader(
//                             mediaItem,
//                            cacheDataSourceFactory
//                        )
                    }

                    runBlocking {

                        withContext(Dispatchers.IO) {
                            downloader.download { contentLength, bytesDownloaded, percentDownloaded ->
                            //    if (bytesDownloaded >= 50 * 1024L) downloader.cancel()
                                Log.d("ExoCache", "Content length : $contentLength : Percentage downloaded $percentDownloaded % : Bytes Downloaded : $bytesDownloaded")
                            }

                        }
                    }
                }
                override fun onPrepareError(helper: DownloadHelper, e: IOException) {
                    Log.d("ExoCache", "ERROR : $e")
                }

            }




        // Create a player instance and set mediaSourceFactory.
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()

        // Bind the player to the view.
        playerView.player = simpleExoPlayer

        //setting exoplayer when it is ready.
        simpleExoPlayer!!.playWhenReady = true

        //Seeks to a position specified in milliseconds in the specified window.
        simpleExoPlayer!!.seekTo(0, 0)

        //set repeat mode.
        simpleExoPlayer!!.repeatMode = Player.REPEAT_MODE_OFF

        val cacheDataSourceFactory = simpleCache.let {
            CacheDataSource.Factory()
                .setCache(it)
                .setUpstreamDataSourceFactory(defaultDataSourceFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                .setEventListener(object : CacheDataSource.EventListener{
                    override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
                        Log.d("Cache", "Cache bytes read : $cachedBytesRead : cacheSizeBytes : $cacheSizeBytes" )
                    }

                    override fun onCacheIgnored(reason: Int) {
                        Log.d("Cache", "Ignored : $reason" )
                    }

                })

        }


        val mediaSource = HlsMediaSource.Factory(cacheDataSourceFactory)
            .createMediaSource(mediaItem)

        simpleExoPlayer!!.addMediaSource(mediaSource)
        simpleExoPlayer!!.prepare()


        lifecycleScope.launch(Dispatchers.IO){
            hlsDownloadHelper.prepare(prepareCallback)
        }

    }

    private val downloader by lazy {
        HlsDownloader(
            MediaItem.Builder().setUri(Uri.parse(videoURL)).setCustomCacheKey("custom_cache").build(),
            CacheDataSource.Factory()
                .setCache(simpleCache)
                .setUpstreamDataSourceFactory(
                    defaultDataSourceFactory
                )
        )
    }

    private val cacheStreamKeys = arrayListOf(
        StreamKey(0, 1),
        StreamKey(1, 1),
        StreamKey(2, 1),
        StreamKey(3, 1),
        StreamKey(4, 1)
    )


    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || simpleExoPlayer == null) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (simpleExoPlayer == null) {
            return
        }
        //release player when done
        simpleExoPlayer!!.release()
        simpleExoPlayer = null
    }

    companion object {
        const val cookie = "CloudFront-Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9tZWRpYS5jb2xlYXJuLmlkLyoiLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE2Mzg0Mzk5MzN9fX1dfQ__; Domain=colearn.id; expires=Thu, 02 Dec 2021 10:12:13 GMT; HttpOnly; Max-Age=7200; Path=/; Secure;CloudFront-Signature=Z-8KPjt-RW~qZL1n4PBA7917WtZW7GYh-iMjEy-8tGft2CA1hMHILX85IOJlk5czzItyfgezhl5cvHvp9HLasPJw3G8uyWISqsDyXU~QD9W618SZLwLJ4H~ZPaJA0YOsWhQV7qa-jXAno1kBlwZ9OA-lvQ2dAjH5JWM7PhgIVfJL-Hp2hdE~wItNrPTcF6G8Pbd1U1KJX66X1FDihWJLKNWDIWrOSo3clP5EILGcZcsxcbVzZSmbLrCnxHSEo6bN-GkHhk1vIlxaMXPc4TjjvMobd3hT29FTvDO9FBWPJN8-jT-oSZSAtvDV-IbZOUzQbGewZpolAtTZpYRSeetj5A__; Domain=colearn.id; expires=Thu, 02 Dec 2021 10:12:13 GMT; HttpOnly; Max-Age=7200; Path=/; Secure;CloudFront-Key-Pair-Id=APKAJ6DN7UYDXNJCVUWQ; Domain=colearn.id; expires=Thu, 02 Dec 2021 10:12:13 GMT; HttpOnly; Max-Age=7200; Path=/; Secure"    }
}

