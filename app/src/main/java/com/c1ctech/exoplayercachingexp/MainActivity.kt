// 2.11.0


package com.c1ctech.exoplayercachingexp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.StreamKey
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.hls.offline.HlsDownloader
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var httpDataSourceFactory: HttpDataSource.Factory
    private lateinit var defaultDataSourceFactory: DefaultDataSourceFactory
    private lateinit var cacheDataSourceFactory: DataSource.Factory
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private val simpleCache: SimpleCache = MyApp.simpleCache

    private lateinit var playerView: PlayerView
    private val videoURL =  ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get PlayerView by its id
        playerView = findViewById(R.id.playerView)

    }

    private fun initPlayer() {
        httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Android").setDefaultRequestProperties(mapOf("Cookie" to cookie))


        defaultDataSourceFactory = DefaultDataSourceFactory(
            applicationContext, httpDataSourceFactory
        )

        //A DataSource that reads and writes a Cache.
        cacheDataSourceFactory =
            CacheDataSource.Factory()
                .setCache(simpleCache)
                .setUpstreamDataSourceFactory(defaultDataSourceFactory)
                .setCacheReadDataSourceFactory(FileDataSource.Factory())
                .setCacheWriteDataSinkFactory(CacheDataSink.Factory().setCache(simpleCache).setFragmentSize(CacheDataSink.DEFAULT_FRAGMENT_SIZE))
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                .setEventListener(
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

        // Set the media source to be played.
        //simpleExoPlayer!!.setMediaSource(mediaSource, true)

        // Prepare the player.
        simpleExoPlayer!!.prepare(mediaSource)

        lifecycleScope.launch(Dispatchers.IO) {
            preCacheVideo()
        }
    }

    private val cacheStreamKeys = arrayListOf(
        StreamKey(0, 1),
        StreamKey(1, 1),
        StreamKey(2, 1),
        StreamKey(3, 1),
        StreamKey(4, 1)

    )

    private val downloader by lazy {
        HlsDownloader(
            MediaItem.Builder()
                .setUri(Uri.parse(videoURL))
                .setStreamKeys(cacheStreamKeys)
                .build(),
            cacheDataSourceFactory as CacheDataSource.Factory,

        )

    }


    private val mediaSource: MediaSource by lazy {
        val dataSourceFactory = cacheDataSourceFactory
        HlsMediaSource.Factory(dataSourceFactory)
            .setStreamKeys(cacheStreamKeys)
            .setAllowChunklessPreparation(true)
            .createMediaSource(Uri.parse(videoURL))
    }

    private fun cancelPreCache() {
        downloader.cancel()
    }

    private suspend fun preCacheVideo() = withContext(Dispatchers.IO) {
        runCatching {
            // do nothing if already cache enough

            if (simpleCache.isCached(videoURL, 0, 5 * 1024 * 1024L)) {
                Log.d("PreCache", "video has been cached, return")
                return@runCatching
            }

            Log.d("PreCache", "start pre-caching")

            downloader.download { contentLength, bytesDownloaded, percentDownloaded ->
                if (bytesDownloaded >= 5 * 1024 * 1024L) downloader.cancel()
                Log.d(
                    "PreCache",
                    "contentLength: $contentLength, bytesDownloaded: $bytesDownloaded, percentDownloaded: $percentDownloaded"
                )
            }
        }.onFailure {
            if (it is InterruptedException) return@onFailure

            Log.d("TAG", "Cache fail for position:   with exception: $it}")
            it.printStackTrace()
        }.onSuccess {
            Log.d("TAG", "Cache success")
        }
        Unit
    }

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
        const val cookie = ""
    }
}

