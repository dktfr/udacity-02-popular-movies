package com.myoung.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myoung.android.popularmovies.data.MovieListItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MovieDetailActivity extends AppCompatActivity {
    // TAG for log
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    // Views
//    private ProgressBar mProgressBarLoading;
    private TextView mTextViewTitle;
    private TextView mTextViewReleaseDate;
    private TextView mTextViewVoteAverage;
    private TextView mTextViewOverview;
    private ImageView mImageViewPoster;

    // Variables
    Context context;


    private void initActivity() {
        context = this;

//        mProgressBarLoading = (ProgressBar) findViewById(R.id.pb_loading);
        mTextViewTitle = (TextView) findViewById(R.id.tv_md_title);
        mTextViewReleaseDate = (TextView) findViewById(R.id.tv_md_year);
        mTextViewVoteAverage = (TextView) findViewById(R.id.tv_md_vote_average);
        mTextViewOverview = (TextView) findViewById(R.id.tv_md_overview);
        mImageViewPoster = (ImageView) findViewById(R.id.iv_md_poster);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        initActivity();

        Intent intent = getIntent();
        MovieListItem movieListItem = intent.getParcelableExtra(intent.EXTRA_TEXT);
        setMovieDetailData(movieListItem);

//        Intent intent = getIntent();
//        String movieId = intent.getStringExtra(Intent.EXTRA_TEXT);
//        loadMovieDetailData(String.valueOf(movieId));
    }

    public void setMovieDetailData(MovieListItem movieListItem) {
        if(movieListItem != null) {
            String title = movieListItem.getTitle();
            String voteAverage = movieListItem.getVoteAverage();
            String overview = movieListItem.getOverview();
            String releaseDate = movieListItem.getReleaseDate();
            String posterPath = movieListItem.getPosterPath();
            String year;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                SimpleDateFormat outputFormat
                        = new SimpleDateFormat("dd MMM yyyy", new Locale("en", "US"));
                Date date = dateFormat.parse(releaseDate);

                releaseDate = outputFormat.format(date);
                year = yearFormat.format(date);  // get year
                title = title + " (" + year + ")";
            } catch(ParseException e) {
                e.printStackTrace();
                year = "";
            }

            mTextViewTitle.setText(title);
            mTextViewVoteAverage.setText(voteAverage);
            mTextViewOverview.setText(overview);
            mTextViewReleaseDate.setText(releaseDate);
            Glide.with(context)
                    .load(posterPath)
                    .into(mImageViewPoster);
        }
    }

//    public void setMovieDetailData(MovieDetail movieDetail) {
//        if(movieDetail != null) {
//            mTextViewMovieID.setText(movieDetail.getId());
//            mTextViewTitle.setText(movieDetail.getTitle());
//            mTextViewVoteAverage.setText(movieDetail.getVoteAverage());
//            mTextViewOverview.setText(movieDetail.getOverview());
//            mTextViewYear.setText(movieDetail.getReleaseDate());
//
//            Glide.with(context)
//                    .load(movieDetail.getPosterPath())
//                    .into(mImageViewPoster);
//        }
//    }
//
//    public void loadMovieDetailData(String movieId) {
//        new FetchMovieDetail().execute(movieId);
//    }
//
//    public class FetchMovieDetail extends AsyncTask<String, Void, MovieDetail> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mProgressBarLoading.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected MovieDetail doInBackground(String... params) {
//            MovieDetail movieDetail = null;
//
//            String movieId = params[0];
//            URL movieDetailRequestUrl = NetworkUtils.buildMovieDetailUrl(movieId);
//
//            try {
//                String movieDetailJsonResponse =
//                        NetworkUtils.getResponseFromHttpUrl(movieDetailRequestUrl);
//
//                movieDetail = TheMovieDbJsonUtils.getMovieDetailFromJson(movieDetailJsonResponse);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return movieDetail;
//        }
//
//        @Override
//        protected void onPostExecute(MovieDetail movieDetail) {
//            mProgressBarLoading.setVisibility(View.INVISIBLE);
//            setMovieDetailData(movieDetail);
//        }
//    }

}
