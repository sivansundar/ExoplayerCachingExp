package com.c1ctech.exoplayercachingexp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.c1ctech.exoplayercachingexp.databinding.FragmentSolutionsBinding
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper
import com.google.android.exoplayer2.offline.StreamKey
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.hls.offline.HlsDownloader
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    lateinit var binding : FragmentSolutionsBinding
    private val simpleCache: SimpleCache = MyApp.simpleCache
    lateinit var cacheDataSourceFactory: DataSource.Factory
    var httpDataSourceFactory: HttpDataSource.Factory = MyApp.httpDataSourceFactory

    private val videoURL = "https://media.colearn.id/videos/transcoded/M0500101E001/hls/M0500101E001.m3u8"


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
        lifecycleScope.launch(Dispatchers.IO) {
            preCacheVideo()
        }

        binding.playButton.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }



    private val downloader by lazy {
        HlsDownloader(
            Uri.parse(videoURL),
            MyApp.cacheStreamKeys,
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

    private fun removePreCache() {
        downloader.remove()
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
                if (bytesDownloaded >= MyApp.exoPlayerCacheSize) downloader.cancel()
                Log.d(
                    "PreCache",
                    "contentLength: $contentLength, bytesDownloaded: $bytesDownloaded, percentDownloaded: $percentDownloaded"
                )
            }
        }.onFailure {
            if (it is InterruptedException) return@onFailure

            Log.d("PreCache", "Cache fail for position:  with exception: $it}")
            it.printStackTrace()
        }.onSuccess {
            Log.d("PreCache", "Cache success")
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