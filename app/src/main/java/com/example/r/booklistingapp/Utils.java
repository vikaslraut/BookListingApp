package com.example.r.booklistingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static android.content.Context.CONNECTIVITY_SERVICE;

public final class Utils {
    //JSON Constants used for parsing
    public static final String JSON_BOOK_ARRAY = "items";
    public static final String JSON_BOOK_TITLE = "title";
    public static final String JSON_VOLUME_INFO = "volumeInfo";
    public static final String JSON_AUTHORS_ARRAY = "authors";
    public static final String JSON_UNKNOWN_AUTHOR_STRING = "Unknown author";

    private static final String TAG = Utils.class.getSimpleName();
    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 5000;

    /**
     * Mthod to open connection to given URL and return response
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String makeHttpRequest(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String stringResponse = "";
        Log.d(TAG, "makeHttpRequest: URL : " + url);
        try {
            //Prepare request
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);

            //open connection
            urlConnection.connect();

            //process the response if SUCCESS
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                stringResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "makeHttpRequest: ERROR response code : " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "makeHttpRequest: problem retrieving json response", e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (inputStream != null)
                inputStream.close();
        }
        return stringResponse;
    }


    /**
     * Method to read data from stream
     *
     * @param inputStream
     * @throws IOException
     */
    public static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * method to generate url object
     *
     * @param stringURL
     * @return url
     */
    public static URL createURL(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(TAG, "buildURL: error while creating url ", e);
        }
        return url;
    }

    /**
     * Method to check network availability
     *
     * @param context
     * @return
     */
    public static boolean networkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        return false;
    }
}
