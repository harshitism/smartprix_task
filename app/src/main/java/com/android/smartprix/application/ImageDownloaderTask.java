package com.android.smartprix.application;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.android.smartprix.R;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;


public class ImageDownloaderTask   extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> documentsImageViewReference;
    private final SmartPrixApplication application;


    public ImageDownloaderTask(ImageView imageView)
    {
        application = SmartPrixApplication.getInstance();
        documentsImageViewReference = new WeakReference<ImageView>(imageView);
    }


    @Override
    protected Bitmap doInBackground(String... params) {

        Bitmap image;
        if(application.getBitmapFromCache(params[0])==null)
        {
            Log.d("Cache","Downling from Server"+params[0]);
            image = downloadBitmap(params[0]);
            application.addBitmapToCache(params[0],image);
        }
        else
        {
            Log.d("Cache","Loading from Cache"+params[0]);
            image = application.getBitmapFromCache(params[0]);
        }



        return image;
    }

    private Bitmap downloadBitmap(String url) {
        // TODO Auto-generated method stub
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {


        if (documentsImageViewReference != null) {
            ImageView imageView =documentsImageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {

                    imageView.setImageBitmap(bitmap);
                } else {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.progress);
                    imageView.setImageDrawable(placeholder);
                }
            }
        }
    }

}

