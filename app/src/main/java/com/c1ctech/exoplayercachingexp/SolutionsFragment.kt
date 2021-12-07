package com.c1ctech.exoplayercachingexp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.c1ctech.exoplayercachingexp.databinding.FragmentSolutionsBinding
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper
import com.google.android.exoplayer2.offline.StreamKey
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.hls.offline.HlsDownloader
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SolutionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SolutionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var httpDataSourceFactory: HttpDataSource.Factory = MyApp.httpDataSourceFactory
    private var cacheDataSourceFactory: DataSource.Factory = MyApp.cacheDataSourceFactory
    private val simpleCache: SimpleCache = MyApp.simpleCache

    private val videoURL =  "https://media.colearn.id/videos/transcoded/M0500101E001/hls/M0500101E001.m3u8"

    lateinit var binding : FragmentSolutionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSolutionsBinding.inflate(inflater, container, false)
        initPlayer()

        binding.playButton.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            startActivity(intent)
        }

        binding.clearCache.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                Log.d("Cache", "Cache clear called!")

                for(item in simpleCache.keys) {
                    Log.d("Cache", "Cache Deleting! : $item")
                    CacheUtil.remove(simpleCache, item)
                }
            }
        }
        return binding.root
    }

    private fun initPlayer() {

        httpDataSourceFactory.defaultRequestProperties.set("Cookie", MainActivity.cookie)

        //A DataSource that reads and writes a Cache.

//        // Create a player instance and set mediaSourceFactory.
//        simpleExoPlayer = SimpleExoPlayer.Builder(context as Context).build()
//
//        // Bind the player to the view.
//        playerView.player = simpleExoPlayer
//
//        //setting exoplayer when it is ready.
//        simpleExoPlayer!!.playWhenReady = true
//
//        //Seeks to a position specified in milliseconds in the specified window.
//        simpleExoPlayer!!.seekTo(0, 0)
//
//        //set repeat mode.
//        simpleExoPlayer!!.repeatMode = Player.REPEAT_MODE_OFF
//
//        // Set the media source to be played.
//        //simpleExoPlayer!!.setMediaSource(mediaSource, true)
//
//        // Prepare the player.
//        simpleExoPlayer!!.prepare(mediaSource)

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
            Uri.parse(videoURL),
            cacheStreamKeys,
            DownloaderConstructorHelper(
                simpleCache,
                httpDataSourceFactory,
                cacheDataSourceFactory,
                null,
                null
            )
        )
    }

    private fun cancelPreCache() {
        downloader.cancel()
    }

    private suspend fun preCacheVideo() = withContext(Dispatchers.IO) {
        runCatching {
            // do nothing if already cache enough

            if (simpleCache.isCached(videoURL, 0, MyApp.exoPlayerCacheSize)) {
                Log.d("PreCache", "video has been cached, return")
                return@runCatching
            }


            Log.d("PreCache", "start pre-caching")

            downloader.download { contentLength, bytesDownloaded, percentDownloaded ->
                if (bytesDownloaded >= MyApp.exoPlayerCacheSize) cancelPreCache()
                Log.d(
                    "PreCache",
                    "contentLength: $contentLength, bytesDownloaded: $bytesDownloaded, percentDownloaded: $percentDownloaded"
                )
            }
        }.onFailure {
            if (it is InterruptedException) return@onFailure

            Log.d("TAG", "Cache fail for position:  with exception: $it}")
            it.printStackTrace()
        }.onSuccess {
            Log.d("TAG", "Cache success")
        }
        Unit
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SolutionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SolutionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}