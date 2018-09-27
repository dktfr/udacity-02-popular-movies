package com.myoung.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myoung.android.popularmovies.adapter.MovieListAdapter;
import com.myoung.android.popularmovies.data.MovieListItem;
import com.myoung.android.popularmovies.utilities.NetworkUtils;
import com.myoung.android.popularmovies.utilities.TheMovieDbJsonUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.MovieListAdapterOnItemClickHandler {
    // TAG for log
    private static final String TAG = MainActivity.class.getSimpleName();

    // Views
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBarLoadData;
    private TextView mErrorMessage;

    // Adapter
    private MovieListAdapter mMovieListAdapter;

    // Variables
    private int sortOption;
    private int currentPage;
    private final int visibleThreshold = 6;
    private boolean isLoading;


    private void initActivity() {
        currentPage = 1;
        sortOption = NetworkUtils.SORT_OPTION_POPULAR;
        isLoading = false;

        mProgressBarLoadData = (ProgressBar) findViewById(R.id.pb_load_data);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_list);
        mMovieListAdapter = new MovieListAdapter(this, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActivity();

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                MovieListAdapter adapter = mMovieListAdapter;
                ArrayList<MovieListItem> items = mMovieListAdapter.mMovieListData;
                if(items != null) {
                    if(items.get(position) == null) {
                        return layoutManager.getSpanCount();
                    }
                    else {
                        return 1;
                    }
                }
                return 0;
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mMovieListAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentItems = layoutManager.getChildCount();
                int totalItems = layoutManager.getItemCount();
                int scrollOutItems = layoutManager.findFirstVisibleItemPosition();

                if(!isLoading && (currentItems + scrollOutItems + visibleThreshold >= totalItems)) {
                    loadMovieListData();
                }
            }
        });

        loadMovieListData();
    }

    public void loadMovieListData() {
        if(!isLoading) {
            isLoading = true;
            showMovieListView();
            new FetchMovieLists().execute(String.valueOf(currentPage++));
        }
    }

    public void refreshData() {
        currentPage = 1;
        mMovieListAdapter.setMovieListData(null);  // Clear adapter
        loadMovieListData();
    }



    @Override
    public void onItemClick(MovieListItem movieItem) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
//        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movieItem.getId());  // Put movie ID into Extra
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movieItem);  // Put movie data into Extra

        startActivity(intentToStartDetailActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.menu_sort_popular:
                if(!item.isChecked()) {
                    item.setChecked(true);
                    sortOption = NetworkUtils.SORT_OPTION_POPULAR;
                    refreshData();
                    return true;
                }
                break;

            case R.id.menu_sort_top_rate:
                if(!item.isChecked()) {
                    item.setChecked(true);
                    sortOption = NetworkUtils.SORT_OPTION_TOP_RATE;
                    refreshData();
                    return true;
                }
                break;

            case R.id.menu_refresh:
                refreshData();
                return true;

            case R.id.menu_license_notice:
                showLicenseActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showMovieListView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showLicenseActivity() {
        Context context = this;
        Class destinationClass = LicenseActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);
    }

    public class FetchMovieLists extends AsyncTask<String, Void, ArrayList<MovieListItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(mMovieListAdapter.mMovieListData == null) {
                // Show a progress bar in center of view.
                mProgressBarLoadData.setVisibility(View.VISIBLE);
            }
            else {
                // Add a progress bar item.
                mMovieListAdapter.mMovieListData.add(null);
                int position = mMovieListAdapter.mMovieListData.size() - 1;
                mMovieListAdapter.notifyItemInserted(position);
            }
        }

        @Override
        protected ArrayList<MovieListItem> doInBackground(String... params) {
            String page = params[0];
            URL movieListRequestUrl = NetworkUtils.buildMovieListUrl(page, sortOption);

            try {
                String movieListsJsonResponse = NetworkUtils.getResponseFromHttpUrl(movieListRequestUrl);

                ArrayList<MovieListItem> result =
                        TheMovieDbJsonUtils.getMovieListFromJson(MainActivity.this, movieListsJsonResponse);

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieListItem> movieListData) {
            if(movieListData == null) {
                showErrorMessage();
                mProgressBarLoadData.setVisibility(View.INVISIBLE);
                isLoading = false;
                return;
            }

            if(mMovieListAdapter.mMovieListData == null) {
                // Hide a progress bar in center of view.
                mProgressBarLoadData.setVisibility(View.INVISIBLE);
            }
            else {
                // Remove a progress bar item.
                int position = mMovieListAdapter.mMovieListData.size() - 1;
                mMovieListAdapter.mMovieListData.remove(position);
                mMovieListAdapter.notifyItemRemoved(position);
            }
            showMovieListView();
            mMovieListAdapter.setMovieListData(movieListData);
            isLoading = false;
        }
    }
}
