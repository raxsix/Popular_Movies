package eu.raxsix.popularmovies.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.raxsix.popularmovies.Interfaces.OnFragmentInteractionListener;
import eu.raxsix.popularmovies.Interfaces.SortListener;
import eu.raxsix.popularmovies.R;
import eu.raxsix.popularmovies.adapters.GridAdapter;
import eu.raxsix.popularmovies.api_key.ApiKey;
import eu.raxsix.popularmovies.database.MovieContract;
import eu.raxsix.popularmovies.extras.Constants;
import eu.raxsix.popularmovies.network.VolleySingleton;

import static eu.raxsix.popularmovies.extras.Constants.BASE_REQUEST_URL;
import static eu.raxsix.popularmovies.extras.Constants.POPULAR_MOVIES;
import static eu.raxsix.popularmovies.extras.Constants.TAG_REQUEST_POPULAR;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_ID;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_ORIGINAL_TITLE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_OVERVIEW;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_POPULARITY;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_POSTER_PATH;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_RELEASE_DATE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_RESULTS;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_VOTE_AVERAGE;

/**
 * Created by Ragnar on 8/30/2015.
 */
public class PosterGridFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, SortListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = PosterGridFragment.class.getSimpleName();

    private int sortOrderId = 0;

    private GridAdapter mAdapter;
    private ProgressDialog mDialog;
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsObjRequest;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OnFragmentInteractionListener mListener;

    public PosterGridFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_grid, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridView);

        mAdapter = new GridAdapter(getActivity(), R.layout.fragment_item_grid);
        gridview.setAdapter(mAdapter);
        gridview.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Loading");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        updateMovies();

        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader loader = null;

        switch (id) {

            case 0:
                loader = new CursorLoader(getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_IMAGE_PATH},
                        null,
                        null,
                        MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY + " DESC LIMIT 20");
                break;

            case 1:

                loader = new CursorLoader(getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_IMAGE_PATH},
                        null,
                        null,
                        MovieContract.MovieEntry.COLUMN_RATING + " DESC LIMIT 20");
                break;

            case 2:

                loader = new CursorLoader(getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_IMAGE_PATH},
                        MovieContract.MovieEntry.COLUMN_IS_FAVORITE + " = ?",
                        new String[]{String.valueOf(1)},
                        null);
                break;
        }
        return loader;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (sortOrderId == loader.getId()) {
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        if (cursor != null) {

            mListener.onItemSelected(MovieContract.MovieEntry.buildMovieUri(
                    cursor.getInt(Constants.COL_MOVIE_ID)));

            // Another way to do this
            /*((Callback) getActivity())
                    .onItemSelected(MovieContract.MovieEntry.buildMovieUri(
                            cursor.getInt(COL_ID)
                    ));*/
        }
    }


    private void updateMovies() {

        // Instantiate the RequestQueue.
        mRequestQueue = VolleySingleton.getsInstance().getRequestQueue();

        // Creating url
        String url = BASE_REQUEST_URL + POPULAR_MOVIES + ApiKey.API_KEY;

        mJsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                mJsObjRequest.setTag(TAG_REQUEST_POPULAR);

                // Parse the response
                parseJsonResponse(response);

                mDialog.hide();

                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                handleVolleyError(error);
            }
        });

        // Add the request to the RequestQueue.
        mRequestQueue.add(mJsObjRequest);
    }

    /**
     * Parses the Volley response and build up movie lists
     *
     * @param response from Volley response
     */
    private void parseJsonResponse(JSONObject response) {

        if (response != null && response.length() > 0) {

            try {

                if (response.has(KEY_RESULTS)) {


                    JSONArray jsonArray = response.getJSONArray(KEY_RESULTS);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        // Given a default values for json keys to avoid JSONException when key is null or not there
                        long id = -1;
                        String title = Constants.NA;
                        String posterPath = Constants.NA;
                        String overview = Constants.NA;
                        double average = -1;
                        String release = Constants.NA;
                        double popularity = -1;

                        JSONObject movie = jsonArray.getJSONObject(i);

                        if (movie.has(KEY_ID) && !movie.isNull(KEY_ID)) {


                            id = movie.getLong(KEY_ID);
                        }

                        if (movie.has(KEY_POSTER_PATH) && !movie.isNull(KEY_POSTER_PATH)) {


                            posterPath = movie.getString(KEY_POSTER_PATH);
                        }

                        if (movie.has(KEY_ORIGINAL_TITLE) && !movie.isNull(KEY_ORIGINAL_TITLE)) {


                            title = movie.getString(KEY_ORIGINAL_TITLE);
                        }

                        if (movie.has(KEY_OVERVIEW) && !movie.isNull(KEY_OVERVIEW)) {

                            overview = movie.getString(KEY_OVERVIEW);
                        }

                        if (movie.has(KEY_VOTE_AVERAGE) && !movie.isNull(KEY_VOTE_AVERAGE)) {

                            average = movie.getDouble(KEY_VOTE_AVERAGE);
                        }

                        if (movie.has(KEY_RELEASE_DATE) && !movie.isNull(KEY_RELEASE_DATE)) {

                            release = movie.getString(KEY_RELEASE_DATE);
                        }

                        if (movie.has(KEY_POPULARITY) && !movie.isNull(KEY_POPULARITY)) {

                            popularity = movie.getDouble(KEY_POPULARITY);
                        }


                        // If movie does not have title or id do not but it to the movies list
                        if (id != -1 && !title.equals(Constants.NA)) {

                            addMovie(id, title, posterPath, overview, average, release, popularity);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private long addMovie(long remoteMovieId, String title, String posterImagePath, String overview, double rating, String releasDate, double popularity) {

        long movieId;

        String[] where = {MovieContract.MovieEntry._ID};

        Cursor movieCursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,                      // SELECT ID FROM MOVIE WHERE remote_movie_id = remoteMovieId;
                where,
                MovieContract.MovieEntry.COLUMN_REMOTE_MOVIE_ID + " = ?",
                new String[]{String.valueOf(remoteMovieId)},
                null);

        if (movieCursor.moveToFirst()) {

            ContentValues updateValue = new ContentValues();
            updateValue.put(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY, popularity);
            updateValue.put(MovieContract.MovieEntry.COLUMN_RATING, rating);

            getActivity().getContentResolver().update(
                    MovieContract.MovieEntry.CONTENT_URI,
                    updateValue,
                    MovieContract.MovieEntry.COLUMN_REMOTE_MOVIE_ID + " = ?",
                    new String[]{String.valueOf(remoteMovieId)});

            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
            movieId = movieCursor.getLong(movieIdIndex);

        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues movieValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            movieValues.put(MovieContract.MovieEntry.COLUMN_REMOTE_MOVIE_ID, remoteMovieId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_PATH, posterImagePath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
            movieValues.put(MovieContract.MovieEntry.COLUMN_DATE, releasDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY, popularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TAG, "popular");
            movieValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 0);


            // Finally, insert movie data into the database.
            Uri insertedUri = getActivity().getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    movieValues
            );

            // The resulting URI contains the ID for the row.  Extract the movieId from the Uri.
            movieId = ContentUris.parseId(insertedUri);
        }
        movieCursor.close();

        return movieId;

    }

    /**
     * Custom method for handling different Volley errors
     */
    private void handleVolleyError(VolleyError error) {

        mDialog.hide();

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

            Toast.makeText(getActivity(), R.string.error_timeout, Toast.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);

        } else if (error instanceof AuthFailureError) {

        } else if (error instanceof ServerError) {

        } else if (error instanceof NetworkError) {

        } else if (error instanceof ParseError) {

        }
    }

    @Override
    public void onSortByPopular() {

        sortOrderId = 0;
        Toast.makeText(getActivity(), "Sorted by Popularity", Toast.LENGTH_SHORT).show();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onSortByRating() {

        sortOrderId = 1;
        Toast.makeText(getActivity(), "Sorted by Rating", Toast.LENGTH_SHORT).show();
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onSortByFavorites() {

        sortOrderId = 2;
        Toast.makeText(getActivity(), "Sorted by Favorites", Toast.LENGTH_SHORT).show();
        getLoaderManager().initLoader(2, null, this);

    }

    @Override
    public void onRefresh() {
        updateMovies();
    }
}