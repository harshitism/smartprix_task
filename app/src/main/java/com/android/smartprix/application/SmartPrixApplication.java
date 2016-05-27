package com.android.smartprix.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.LruCache;

/**
 * Created by Harshit on 26-May-16.
 */
public class SmartPrixApplication extends Application {


    private static SmartPrixApplication mInstance;

    public static SmartPrixApplication getInstance() {

        if(mInstance==null)
        {
            return new SmartPrixApplication();
        }
        else
        {
        return mInstance;
        }
    }

    private SharedPreferences prefs;


    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 4;
    private LruCache<String,Bitmap> imageCache = new LruCache<String, Bitmap>(cacheSize){
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.getByteCount() / 1024;
        }
    };

    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromCache(key) == null && bitmap !=null) {
            imageCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        return imageCache.get(key);
    }

    public void setStringValueInSharedPreference(String name, String value) {
        prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, value);
        editor.commit();
    }


    public String getStringValueFromSharedPrefernced(String name) {
        prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String stringvalue = prefs.getString(name, "0");
        return stringvalue;
    }

}
