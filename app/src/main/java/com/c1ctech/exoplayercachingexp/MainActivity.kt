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

//    private var httpDataSourceFactory: HttpDataSource.Factory = MyApp.httpDataSourceFactory
//    private lateinit var defaultDataSourceFactory: DefaultDataSourceFactory
//    private var cacheDataSourceFactory: DataSource.Factory = MyApp.cacheDataSourceFactory
//    private var simpleExoPlayer: SimpleExoPlayer? = null
//    private val simpleCache: SimpleCache = MyApp.simpleCache
//
//    private lateinit var playerView: PlayerView
//    private val videoURL =  "https://media.colearn.id/videos/transcoded/M0500101E001/hls/M0500101E001.m3u8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }


    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            //initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        const val cookie =  "CloudFront-Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9tZWRpYS5jb2xlYXJuLmlkLyoiLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE2Mzg4Nzc4ODR9fX1dfQ__; Domain=colearn.id; expires=Tue, 07 Dec 2021 11:51:24 GMT; HttpOnly; Max-Age=7200; Path=/; Secure;CloudFront-Signature=CoZVdSef0vbldbYMmI2xbRsWdHUiSp1yI6VOEfEexIl-mzCyjTGUcTrEZF6BluubStDmgGwM75hfwE6yDmqevgixaxlC9lpCuggsqGC6AsMbHFsrNxWO3lxQPz1isTWb9az-LVunklb6qeCut8Tk8vuYby0lYVRyy4dmy5GddWQBwuDsnjGizJF8K96zQWZiYM1L8Fsv9~9d9lDG4OKt9jVpJF5eRUQVTzNgoadIfq0nJ-Iq9~ockHtVsC2JTyPCva6NjPWKZ3-uXmBVrP-c-FftHeOfSjWvS31pNcMm9Qih7CmpG1NFsBjtOpHIug5Lf~WDqR1nFL0cr9aOyqA-gw__; Domain=colearn.id; expires=Tue, 07 Dec 2021 11:51:24 GMT; HttpOnly; Max-Age=7200; Path=/; Secure;CloudFront-Key-Pair-Id=APKAJ6DN7UYDXNJCVUWQ; Domain=colearn.id; expires=Tue, 07 Dec 2021 11:51:24 GMT; HttpOnly; Max-Age=7200; Path=/; Secure"
    }

}

