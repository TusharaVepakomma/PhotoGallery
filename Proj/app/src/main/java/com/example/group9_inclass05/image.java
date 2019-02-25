package com.example.group9_inclass05;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by akhilareddydepa on 13/02/18.
 */

public class image extends AsyncTask<String,Void,Bitmap> {
    MainActivity activity;


    public image(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        activity.p.setTitle("Loading");
        //activity.p.setProgressStyle(ProgressDialog.STYL);
        activity.p.setCancelable(false);
        activity.p.show();
        Log.d("Demo","loading");
//        activity.p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

    }


    @Override
    protected Bitmap doInBackground(String... strings) {

        Bitmap image=null;
        ArrayList<Bitmap> ab=new ArrayList<Bitmap>();
        BufferedReader b=null;
        HttpURLConnection htp=null;
        try {

                Log.d("Demo", "array is" + strings[0]);
                URL u = new URL(strings[0]);
                //Log.d("Demo","array is"+strings1.get(1));
                htp = (HttpURLConnection) u.openConnection();
                htp.connect();
                if (htp.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    image = BitmapFactory.decodeStream(htp.getInputStream());

                }

            return image;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (htp != null) {
                htp.disconnect();
            }
            if (b != null) {
                try {
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return null;
    }
/*
    @Override
    protected void onProgressUpdate(Void... values) {
        activity.p.show();
    }
*/
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        activity.p.dismiss();
        activity.image(bitmap);

    }
}
