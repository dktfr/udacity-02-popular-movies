package com.myoung.android.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myoung.android.popularmovies.R;
import com.myoung.android.popularmovies.data.MovieListItem;

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // TAG for log
    private static final String TAG = MovieListAdapter.class.getSimpleName();

    // Constants
    private final int VIEW_TYPE_MOVIE = 0;
    private final int VIEW_TYPE_LOAD = 1;

    // Variables
    private Context context;
    public ArrayList<MovieListItem> mMovieListData;
    private final MovieListAdapterOnItemClickHandler mItemClickHandler;


    // Constructor
    public MovieListAdapter(Context context, MovieListAdapterOnItemClickHandler itemClickHandler) {
        this.context = context;
        this.mItemClickHandler = itemClickHandler;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem;
        boolean shouldAttachToParentImmediately = false;

        View view = null;
        switch(viewType) {
            case VIEW_TYPE_MOVIE:
                layoutIdForListItem = R.layout.movie_list_item;
                view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
                return new MovieListViewHolder(view);

            case VIEW_TYPE_LOAD:
                layoutIdForListItem = R.layout.progress_bar_item;
                view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
                return new LoadViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);

        switch(viewType) {
            case VIEW_TYPE_MOVIE:
                MovieListItem movieListItem = mMovieListData.get(position);
                String movieTitle = movieListItem.getTitle();
                String moviePosterUrl = movieListItem.getPosterPath();

                MovieListViewHolder movieViewHolder = (MovieListViewHolder) viewHolder;

                Log.v(TAG, moviePosterUrl);
                movieViewHolder.mMovieTitleTextView.setText(movieTitle);
                Glide.with(context)
                        .load(moviePosterUrl)
                        .into(movieViewHolder.mMoviePosterImageView);
                break;

            case VIEW_TYPE_LOAD:
                LoadViewHolder loadViewHolder =  (LoadViewHolder) viewHolder;
                loadViewHolder.mProgressBar.setIndeterminate(true);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (mMovieListData == null) ? 0 : mMovieListData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMovieListData.get(position) == null ? VIEW_TYPE_LOAD : VIEW_TYPE_MOVIE;
    }

    public void setMovieListData(ArrayList<MovieListItem> movieListData) {
        if(mMovieListData != null && movieListData != null) {
            mMovieListData.addAll(movieListData);
        }
        else {
            mMovieListData = movieListData;
        }
        notifyDataSetChanged();
    }


    // View holder
    public class MovieListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mMovieTitleTextView;
        public final ImageView mMoviePosterImageView;

        public MovieListViewHolder(View view) {
            super(view);

            mMovieTitleTextView = (TextView) view.findViewById(R.id.tv_movie_title);
            mMoviePosterImageView = (ImageView) view.findViewById(R.id.iv_movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            MovieListItem movieItem = mMovieListData.get(position);
            mItemClickHandler.onItemClick(movieItem);
        }
    }

    public class LoadViewHolder extends RecyclerView.ViewHolder {
        public final ProgressBar mProgressBar;

        public LoadViewHolder(@NonNull View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.pb_load_more_item);
        }
    }


    // Interface
    public interface MovieListAdapterOnItemClickHandler {
        void onItemClick(MovieListItem movieItem);
    }

}
