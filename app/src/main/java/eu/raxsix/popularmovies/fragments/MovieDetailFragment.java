package eu.raxsix.popularmovies.fragments;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.raxsix.popularmovies.R;
import eu.raxsix.popularmovies.adapters.TrailerCursorAdapter;
import eu.raxsix.popularmovies.api_key.ApiKey;
import eu.raxsix.popularmovies.database.MovieContract;
import eu.raxsix.popularmovies.extras.Constants;
import eu.raxsix.popularmovies.helpers.Helper;
import eu.raxsix.popularmovies.network.VolleySingleton;

import static eu.raxsix.popularmovies.extras.Constants.COL_DATE;
import static eu.raxsix.popularmovies.extras.Constants.COL_FAVORITE;
import static eu.raxsix.popularmovies.extras.Constants.COL_IMAGE_PATH;
import static eu.raxsix.popularmovies.extras.Constants.COL_OVERVIEW;
import static eu.raxsix.popularmovies.extras.Constants.COL_RATING;
import static eu.raxsix.popularmovies.extras.Constants.COL_REMOTE_MOVIE_ID;
import static eu.raxsix.popularmovies.extras.Constants.COL_TITLE;
import static eu.raxsix.popularmovies.extras.Constants.TAG_REQUEST_REVIEW;
import static eu.raxsix.popularmovies.extras.Constants.TAG_REQUEST_TRAILER;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_PREVIEW_CONTENT;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_RESULTS;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_REVIEW_AUTHOR;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_TRAILER_NAME;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_TRAILER_SITE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_TRAILER_SIZE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_TRAILER_TYPE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_YOUTUBE_KEY;


public class MovieDetailFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {


    public static final String DETAIL_URI = "URI";
    private static final String TAG = MovieDetailFragment.class.getSimpleName();

    private Uri mUri;
    private String mShareUrl;
    private int mLocalMovieId;
    private int mRemoteMovieId;
    private int mFavorite;
    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mReviewTextView;
    private ListView mListView;
    private TextView mOverview;
    private CheckBox mCheckBox;
    private ImageView mPosterImageView;
    private ProgressBar mListProgressBar;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mTrailerRequest;
    private JsonObjectRequest mReviewRequest;
    private ShareActionProvider mShareActionProvider;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Get the references for the widgets
        mTitle = (TextView) rootView.findViewById(R.id.titleTextView);
        mPosterImageView = (ImageView) rootView.findViewById(R.id.posterImageView);
        mReleaseDate = (TextView) rootView.findViewById(R.id.releaseTextView);
        mRating = (TextView) rootView.findViewById(R.id.ratingTextView);
        mOverview = (TextView) rootView.findViewById(R.id.overviewTextView);
        mReviewTextView = (TextView) rootView.findViewById(R.id.reviewTextView);
        mCheckBox = (CheckBox) rootView.findViewById(R.id.favoriteCheckBox);
        mListView = (ListView) rootView.findViewById(R.id.trailersListView);
        mListProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get Volley to init ImageLoader for cached images
        VolleySingleton mVolleySingleton = VolleySingleton.getsInstance();

        // Instantiate the RequestQueue.
        mRequestQueue = VolleySingleton.getsInstance().getRequestQueue();

        mImageLoader = mVolleySingleton.getImageLoader();

        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                mUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {

            // Set favorite
            mFavorite = data.getInt(COL_FAVORITE);

            // Set the favorite
            if (mFavorite == 0) {

                mCheckBox.setChecked(false);
            } else {
                mCheckBox.setChecked(true);
            }

            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mCheckBox.isChecked()) {
                        mFavorite = 1;
                    } else {
                        mFavorite = 0;
                    }

                    ContentValues updateValue = new ContentValues();
                    updateValue.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, mFavorite);

                    getActivity().getContentResolver().update(
                            mUri,
                            updateValue,
                            null,
                            null);
                }
            });


            String posterImageUrl = data.getString(COL_IMAGE_PATH);
            // Build the poster url
            String posterUrl = Constants.BASE_URL + Constants.IMAGE_SIZE + posterImageUrl;

            // Get the image, it should be cached by Volley, not sure for 100%
            mImageLoader.get(posterUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    // Set the poster
                    mPosterImageView.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                    handleVolleyError(error);
                }
            });

            // Set title
            mTitle.setText(data.getString(COL_TITLE));

            // Building the date
            String date = data.getString(COL_DATE);

            // Set the year, take the string and make the Calendar object from it
            try {
                Calendar releaseDate = new GregorianCalendar();
                Date formattedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                releaseDate.setTime(formattedDate);
                String year = Integer.toString(releaseDate.get(Calendar.YEAR));

                // Set the year
                mReleaseDate.setText(year);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Set overview
            String overview = data.getString(COL_OVERVIEW);
            mOverview.setText(overview);

            // Set the rating
            Double rating = data.getDouble(COL_RATING);
            mRating.setText(Double.toString(rating) + " / 10");


            mRemoteMovieId = data.getInt(COL_REMOTE_MOVIE_ID);
            mLocalMovieId = data.getInt(Constants.COL_MOVIE_ID);

            getTrailerInfo();
            getReviewInfo();

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.d(TAG, "MovieDetailFragment - onLoaderReset");
    }


    private void getTrailerInfo() {

        mListProgressBar.setVisibility(View.VISIBLE);

        String trailerUrl = Constants.MOVIE_TRAILER_BASE_URL + mRemoteMovieId + "/videos?api_key=" + ApiKey.API_KEY;

        mTrailerRequest = new JsonObjectRequest(Request.Method.GET, trailerUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "trailer onResponse was called");
                // Parse the response
                parseJsonResponse(response, mTrailerRequest.getTag().toString());

                setTrailerInfo();

                mListProgressBar.setVisibility(View.INVISIBLE);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                handleVolleyError(error);
            }
        });

        mTrailerRequest.setTag(TAG_REQUEST_TRAILER);
        // Add the request to the RequestQueue.
        mRequestQueue.add(mTrailerRequest);

    }


    private void addTrailerToDatabase(String youtubeKey, String name, String site, int size, String type) {

        //content://eu.raxsix.popularmovies/trailer/youtubeKey
        Uri uri = MovieContract.TrailerEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(youtubeKey)).build();

        Cursor trailerCursor = getActivity().getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);

        // Is this trailer already in db, if not then add it
        if (!trailerCursor.moveToFirst()) {

            ContentValues trailerValues = new ContentValues();

            trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, mLocalMovieId);
            Log.d(TAG, mLocalMovieId + "");
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, youtubeKey);
            Log.d(TAG, youtubeKey + "");
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, size);
            Log.d(TAG, size + "");
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, type);
            Log.d(TAG, type + "");
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, name);
            Log.d(TAG, name + "");
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_SITE, site);
            Log.d(TAG, site + "");

            getActivity().getContentResolver().insert(
                    MovieContract.TrailerEntry.CONTENT_URI,
                    trailerValues);
        }
        trailerCursor.close();
    }

    private void setTrailerInfo() {

        String[] columns = {MovieContract.TrailerEntry._ID, MovieContract.TrailerEntry.COLUMN_NAME};

        @SuppressLint("Recycle")
        Cursor setTrailerCursor = getActivity().getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                columns,
                MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[]{String.valueOf(mLocalMovieId)},
                null);


        // Setup cursor adapter using cursor from last step
        TrailerCursorAdapter trailerAdapter = new TrailerCursorAdapter(getActivity(), setTrailerCursor, 0);
        // Attach cursor adapter to the ListView
        mListView.setAdapter(trailerAdapter);

        mListView.setOnItemClickListener(this);


        buildYoutubeShareUrl();

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

    }

    private void getReviewInfo() {

        String reviewUrl = Constants.MOVIE_REVIEW_BASE_URL + mRemoteMovieId + "/reviews?api_key=" + ApiKey.API_KEY;

        mReviewRequest = new JsonObjectRequest(Request.Method.GET, reviewUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Review onResponse was called");
                // Parse the response
                parseJsonResponse(response, mReviewRequest.getTag().toString());

                setReviewInfo();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);
            }
        });

        mReviewRequest.setTag(TAG_REQUEST_REVIEW);
        // Add the request to the RequestQueue.
        mRequestQueue.add(mReviewRequest);

    }

    private void setReviewInfo() {

        StringBuilder sb = new StringBuilder();

        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[]{String.valueOf(mLocalMovieId)},
                null);

        while (cursor.moveToNext()) {

            int authorIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR);
            Log.d(TAG, "authorIndex: " + authorIndex);
            int contentIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT);
            Log.d(TAG, "contentIndex: " + contentIndex);

            sb.append("\n");
            sb.append(cursor.getString(authorIndex));
            sb.append("\n");
            sb.append(cursor.getString(contentIndex));
            sb.append("\n");
        }

        mReviewTextView.setText(sb);

        cursor.close();
    }


    private void parseJsonResponse(JSONObject response, String tag) {
        Log.d(TAG, "parseJsonResponse was called");
        if (response != null && response.length() > 0) {

            try {

                if (response.has(KEY_RESULTS)) {

                    JSONArray jsonArray = response.getJSONArray(KEY_RESULTS);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        if (tag.equals(TAG_REQUEST_TRAILER)) {

                            // Given a default values for json keys to avoid JSONException when key is null or not there
                            String youtubeKey = Constants.NA;
                            String name = Constants.NA;
                            String site = Constants.NA;
                            int size = -1;
                            String type = Constants.NA;

                            JSONObject movie = jsonArray.getJSONObject(i);


                            if (movie.has(KEY_YOUTUBE_KEY) && !movie.isNull(KEY_YOUTUBE_KEY)) {


                                youtubeKey = movie.getString(KEY_YOUTUBE_KEY);
                            }

                            if (movie.has(KEY_TRAILER_NAME) && !movie.isNull(KEY_TRAILER_NAME)) {


                                name = movie.getString(KEY_TRAILER_NAME);
                            }

                            if (movie.has(KEY_TRAILER_SITE) && !movie.isNull(KEY_TRAILER_SITE)) {

                                site = movie.getString(KEY_TRAILER_SITE);
                            }

                            if (movie.has(KEY_TRAILER_SIZE) && !movie.isNull(KEY_TRAILER_SIZE)) {

                                size = movie.getInt(KEY_TRAILER_SIZE);
                            }

                            if (movie.has(KEY_TRAILER_TYPE) && !movie.isNull(KEY_TRAILER_TYPE)) {

                                type = movie.getString(KEY_TRAILER_TYPE);
                            }


                            // If trailer does not have youtube key do not but it to the db
                            if (!youtubeKey.equals(Constants.NA)) {

                                addTrailerToDatabase(youtubeKey, name, site, size, type);
                            }
                        } else {

                            // Given a default values for json keys to avoid JSONException when key is null or not there

                            String author = Constants.NA;
                            String content = Constants.NA;

                            JSONObject movie = jsonArray.getJSONObject(i);


                            if (movie.has(KEY_REVIEW_AUTHOR) && !movie.isNull(KEY_REVIEW_AUTHOR)) {

                                author = movie.getString(KEY_REVIEW_AUTHOR);
                            }

                            if (movie.has(KEY_PREVIEW_CONTENT) && !movie.isNull(KEY_PREVIEW_CONTENT)) {

                                content = movie.getString(KEY_PREVIEW_CONTENT);
                            }


                            // If trailer does not have youtube key do not but it to the db
                            if (!content.equals(Constants.NA) && !author.equals(Constants.NA)) {

                                addReviewsToDatabase(author, content);
                            }

                        }


                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addReviewsToDatabase(String author, String content) {

        String[] columns = {MovieContract.ReviewEntry.COLUMN_AUTHOR};

        Cursor reviewCursor = getActivity().getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                columns,
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " = ?",
                new String[]{String.valueOf(author)},
                null);

        if (!reviewCursor.moveToFirst()) {

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, mLocalMovieId);
            Log.d(TAG, mLocalMovieId + "");
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author);
            Log.d(TAG, author + "");
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, content);
            Log.d(TAG, content + "");

            getActivity().getContentResolver().insert(
                    MovieContract.ReviewEntry.CONTENT_URI,
                    reviewValues);
        }

        reviewCursor.close();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String[] columns = {MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY};

        Cursor trailerCursor = getActivity().getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                columns,
                MovieContract.TrailerEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null);

        if (trailerCursor.moveToFirst()) {

            int youtubeKeyIndex = trailerCursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY);

            String youtubeKey = trailerCursor.getString(youtubeKeyIndex);

            watchYoutubeVideo(youtubeKey);
        }

        trailerCursor.close();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mShareUrl != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareUrl);

        return shareIntent;
    }

    private void buildYoutubeShareUrl(){

        String[] columns = {MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY};

        Cursor trailerCursor = getActivity().getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                columns,
                MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[]{String.valueOf(mLocalMovieId)},
                null);

        Helper.getCursorInfo(trailerCursor);

        trailerCursor.moveToFirst();
        String youtubeKey = trailerCursor.getString(0);

        mShareUrl = "http://www.youtube.com/watch?v=" + youtubeKey;

      trailerCursor.close();
    }


    private void watchYoutubeVideo(String id) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    /**
     * Custom method for handling different Volley errors
     */
    private void handleVolleyError(VolleyError error) {

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Toast.makeText(getActivity(), R.string.error_timeout, Toast.LENGTH_LONG).show();

        } else if (error instanceof AuthFailureError) {

        } else if (error instanceof ServerError) {

        } else if (error instanceof NetworkError) {

        } else if (error instanceof ParseError) {

        }
    }
}
