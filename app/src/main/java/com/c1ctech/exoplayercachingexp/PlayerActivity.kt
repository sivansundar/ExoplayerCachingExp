package com.c1ctech.exoplayercachingexp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util


class PlayerActivity : AppCompatActivity() {

    private var simpleExoPlayer: SimpleExoPlayer? = null

    private lateinit var playerView: PlayerView
    private val videoURL =
        "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.playerView)

    }


    private fun initPlayer() {


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

    }

}