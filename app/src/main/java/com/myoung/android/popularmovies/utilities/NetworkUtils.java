package com.myoung.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    // TAG for log
    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**************************************************************************
    * API Key (https://www.themoviedb.org/)
    * Insert your TMDb API Key here.
    **************************************************************************/
    private static final String apiKeyV3 = "*** Insert your TMDb API key here ***";

    // URL
    private static final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3";

    // Path
    private static final String PATH_DISCOVER = "discover";
    private static final String PATH_MOVIE = "movie";
    private static final String PATH_VIDEOS = "videos";
    private static final String PATH_REVIEWS = "reviews";
    private static final String PATH_POPULAR = "popular";
    private static final String PATH_TOP_RATE = "top_rated";

    // Query
    private static final String PARAM_API_KEY = "api_key";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_SORT_BY = "sort_by";

    // Values
    private static final String sortBy = "popularity.desc";

    // Sort Options
    public static final int SORT_OPTION_POPULAR = 0;
    public static final int SORT_OPTION_TOP_RATE = 1;



    public static URL buildMovieListUrl(String page, int sortOption) {
        String sortOptionPath = PATH_POPULAR;
        switch (sortOption) {
            case SORT_OPTION_POPULAR:
                sortOptionPath = PATH_POPULAR;
                break;
            case SORT_OPTION_TOP_RATE:
                sortOptionPath = PATH_TOP_RATE;
                break;
        }

        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(sortOptionPath)
                .appendQueryParameter(PARAM_API_KEY, apiKeyV3)
                .appendQueryParameter(PARAM_PAGE, page)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI : " + url);

        return url;
    }

    public static URL buildMovieDetailUrl(String movieId) {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(movieId)
                .appendQueryParameter(PARAM_API_KEY, apiKeyV3)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI : " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner sc = new Scanner(in);
            sc.useDelimiter("\\A");

            boolean hasInput = sc.hasNext();
            if(hasInput) {
                return sc.next();
            }
            else {
                return  null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
