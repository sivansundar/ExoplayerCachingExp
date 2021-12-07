package com.c1ctech.exoplayercachingexp

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c1ctech.exoplayercachingexp.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.offline.StreamKey
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache

class PlayerActivity : AppCompatActivity() {

    lateinit var binding : ActivityPlayerBinding
    private lateinit var playerView: PlayerView


    private var httpDataSourceFactory: HttpDataSource.Factory = MyApp.httpDataSourceFactory
    private lateinit var defaultDataSourceFactory: DefaultDataSourceFactory
    private var cacheDataSourceFactory: DataSource.Factory = MyApp.cacheDataSourceFactory
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private val simpleCache: SimpleCache = MyApp.simpleCache

    private val videoURL =  "https://media.colearn.id/videos/transcoded/M0500101E001/hls/M0500101E001.m3u8"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerView = binding.playerView
        setupPlayer()
    }

    private fun setupPlayer() {
        // Create a player instance and set mediaSourceFactory.


        val adaptiveTrackSelection: TrackSelection.Factory =
            AdaptiveTrackSelection.Factory()

        simpleExoPlayer = SimpleExoPlayer.Builder(this, DefaultRenderersFactory(this))
            .setTrackSelector(DefaultTrackSelector(this, adaptiveTrackSelection))
            .setLoadControl(DefaultLoadControl())
            .build()

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
    }

    private val mediaSource: MediaSource by lazy {
        val dataSourceFactory = cacheDataSourceFactory
        HlsMediaSource.Factory(dataSourceFactory)
            .setStreamKeys(cacheStreamKeys)
            .setAllowChunklessPreparation(true)
            .createMediaSource(Uri.parse(videoURL))
    }

    private val cacheStreamKeys = arrayListOf(
        StreamKey(0, 1),
        StreamKey(1, 1),
        StreamKey(2, 1),
        StreamKey(3, 1),
        StreamKey(4, 1)
    )
}