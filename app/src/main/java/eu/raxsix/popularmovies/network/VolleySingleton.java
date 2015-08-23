package eu.raxsix.popularmovies.network;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import eu.raxsix.popularmovies.MyApplication;

/**
 * Created by Ragnar on 8/23/2015.
 */
public class VolleySingleton {

    // Reference to class object
    private static VolleySingleton sInstance = null;

    private RequestQueue mRequestQueue;

    private ImageLoader mImageLoader;

    // We do not want to other classes to use constructor, we make it private
    private VolleySingleton() {

        // NB A key concept is that the RequestQueue must be instantiated with the Application context,
        // not an Activity context. This ensures that the RequestQueue will last for the lifetime of your app,
        // instead of being recreated every time the activity is recreated (for example,
        // when the user rotates the device).
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());


        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            private LruCache<String, Bitmap> cache = new LruCache<>((int) Runtime.getRuntime().maxMemory() / 1024 / 8);


            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static VolleySingleton getsInstance() {

        if (sInstance == null) {

            sInstance = new VolleySingleton();
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue() {

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {

        return mImageLoader;
    }
}
