package com.myoung.android.popularmovies.utilities;

import android.content.Context;

import com.myoung.android.popularmovies.data.MovieDetail;
import com.myoung.android.popularmovies.data.MovieListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TheMovieDbJsonUtils {
    // TAG for log
    private  static final String TAG = TheMovieDbJsonUtils.class.getSimpleName();

    // URL
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";

    // Constants for Movie List JSON
    private static final String JSON_ML_RESULTS = "results";
    private static final String JSON_ML_ID = "id";
    private static final String JSON_ML_TITLE = "title";
    private static final String JSON_ML_POSTER_PATH = "poster_path";
    private static final String JSON_ML_VOTE_AVERAGE = "vote_average";
    private static final String JSON_ML_OVERVIEW = "overview";
    private static final String JSON_ML_RELEASE_DATE = "release_date";

    // Constants for Movie Detail JSON
    private static final String JSON_MD_ID = "id";
    private static final String JSON_MD_TITLE = "title";
    private static final String JSON_MD_VOTE_AVERAGE = "vote_average";
    private static final String JSON_MD_OVERVIEW = "overview";
    private static final String JSON_MD_RELEASE_DATE = "release_date";
    private static final String JSON_MD_POSTER_PATH = "poster_path";

    // Values
    private static final String imageWidthSize = "w185";



    public static ArrayList<MovieListItem> getMovieListFromJson(Context context, String jsonString)
            throws JSONException {

        JSONObject movieListJson = new JSONObject(jsonString);

        JSONArray movieListArray = movieListJson.getJSONArray(JSON_ML_RESULTS);
        ArrayList<MovieListItem> result = new ArrayList<>();

        for(int i=0; i<movieListArray.length(); i++) {
            JSONObject movie = movieListArray.getJSONObject(i);

            String id = movie.getString(JSON_ML_ID);
            String title = movie.getString(JSON_ML_TITLE);
            String posterPath = getFullImageUrl(movie.getString(JSON_ML_POSTER_PATH));
            String overview = movie.getString(JSON_ML_OVERVIEW);
            String releaseDate = movie.getString(JSON_ML_RELEASE_DATE);
            String voteAverage = movie.getString(JSON_ML_VOTE_AVERAGE);

            MovieListItem movieListItem = new MovieListItem(id, title, posterPath);
            movieListItem.setOverview(overview);
            movieListItem.setReleaseDate(releaseDate);
            movieListItem.setVoteAverage(voteAverage);

            result.add(movieListItem);
        }

        return result;
    }

    public static MovieDetail getMovieDetailFromJson(String jsonString) throws JSONException {
        MovieDetail movieDetail = null;

        JSONObject movieDetailJson = new JSONObject(jsonString);

        String id = movieDetailJson.getString(JSON_MD_ID);
        String title = movieDetailJson.getString(JSON_MD_TITLE);
        String overview = movieDetailJson.getString(JSON_MD_OVERVIEW);
        String releaseDate = movieDetailJson.getString(JSON_MD_RELEASE_DATE);
        String voteAverage = movieDetailJson.getString(JSON_MD_VOTE_AVERAGE);
        String posterPath = getFullImageUrl(movieDetailJson.getString(JSON_MD_POSTER_PATH));

        movieDetail = new MovieDetail(id, title);
        movieDetail.setVoteAverage(voteAverage);
        movieDetail.setOverview(overview);
        movieDetail.setReleaseDate(releaseDate);
        movieDetail.setPosterPath(posterPath);

        return movieDetail;
    }

    public static String getFullImageUrl(String relativePath) {
        String fullUrl = null;

        if(relativePath != null) {
            fullUrl = IMAGE_BASE_URL + '/' + imageWidthSize + relativePath;
        }
        return fullUrl;
    }

}
