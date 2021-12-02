package com.c1ctech.exoplayercachingexp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper
import com.google.android.exoplayer2.offline.StreamKey
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.hls.offline.HlsDownloader
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlayerActivity : AppCompatActivity() {

    private var httpDataSourceFactory: HttpDataSource.Factory = MyApp.httpDataSourceFactory
    private lateinit var defaultDataSourceFactory: DefaultDataSourceFactory
    lateinit var cacheDataSourceFactory: DataSource.Factory

    private var simpleExoPlayer: SimpleExoPlayer? = null
    private val simpleCache: SimpleCache = MyApp.simpleCache

    private lateinit var playerView: PlayerView
    private val videoURL =
        "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.playerView)

    }

    private fun initPlayer() {


        defaultDataSourceFactory = DefaultDataSourceFactory(
            applicationContext, httpDataSourceFactory
        )

        //A DataSource that reads and writes a Cache.


        val adaptiveTrackSelection: TrackSelection.Factory =
            AdaptiveTrackSelection.Factory()

        val defaultTrackSelector = DefaultTrackSelector(this, adaptiveTrackSelection)


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

        val httpDSFactory = DefaultHttpDataSourceFactory("Android")

        cacheDataSourceFactory =
            CacheDataSourceFactory(
                MyApp.simpleCache,
                MyApp.httpDataSourceFactory,
                FileDataSource.Factory(),
                CacheDataSinkFactory(MyApp.simpleCache, CacheDataSink.DEFAULT_FRAGMENT_SIZE),
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                object : CacheDataSource.EventListener {
                    override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
                        Log.d(
                            "CR",
                            "onCachedBytesRead. cacheSizeBytes:$cacheSizeBytes, cachedBytesRead: $cachedBytesRead"
                        )
                    }

                    override fun onCacheIgnored(reason: Int) {
                        Log.d("CR", "onCacheIgnored. reason:$reason")
                    }
                }
            )

        val dataSourceFactory = cacheDataSourceFactory

        val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .setStreamKeys(MyApp.cacheStreamKeys)
            .createMediaSource(Uri.parse(videoURL))

        // Prepare the player.
        simpleExoPlayer!!.prepare(mediaSource)


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

    override fun onDestroy() {
        super.onDestroy()
        //removePreCache()
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
        const val cookieValue = ""
    }

}