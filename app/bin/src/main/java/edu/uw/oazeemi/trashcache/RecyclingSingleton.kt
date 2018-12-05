package edu.uw.oazeemi.trashcache

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import android.app.DownloadManager

class Singleton private constructor(context: Context) {
    companion object {
        private var INSTANCE: edu.uw.oazeemi.trashcache.Singleton? = null

        fun getInstance(context: Context): edu.uw.oazeemi.trashcache.Singleton {
            if (INSTANCE == null) {
                INSTANCE = Singleton(context)
            }
            return INSTANCE as edu.uw.oazeemi.trashcache.Singleton
        }
    }

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
                object : ImageLoader.ImageCache {
                    private val cache = LruCache<String, Bitmap>(20)
                    override fun getBitmap(url: String): Bitmap? {
                        return cache.get(url)
                    }

                    override fun putBitmap(url: String, bitmap: Bitmap) {
                        cache.put(url, bitmap)
                    }
                }
        )
    }

    fun <T> add(req: Request<T>) {
        requestQueue.add(req)
    }
}
