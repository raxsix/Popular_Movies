package eu.raxsix.popularmovies.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.raxsix.popularmovies.Interfaces.OnFragmentInteractionListener;
import eu.raxsix.popularmovies.Interfaces.SortListener;
import eu.raxsix.popularmovies.MovieDetailActivity;
import eu.raxsix.popularmovies.R;
import eu.raxsix.popularmovies.adapters.GridAdapter;
import eu.raxsix.popularmovies.api_key.ApiKey;
import eu.raxsix.popularmovies.database.MovieContract;
import eu.raxsix.popularmovies.database.MovieDbHelper;
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
public class ItemGridFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, SortListener {


    private JsonObjectRequest mJsObjRequest;
    private TextView mErrorView;
    private RequestQueue mRequestQueue;
    private ProgressDialog mDialog;
    private MovieDbHelper mOpenHelper;

    private OnFragmentInteractionListener mListener;
    private GridAdapter adapter;

    public ItemGridFragment() {
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
        Log.i("GRID", "Fragment - onAttach");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("GRID", "Fragment - onCreateView");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_grid, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        adapter = new GridAdapter(getActivity(), R.layout.fragment_item_grid);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(this);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("GRID", "Fragment - onActivityCreated");
        updateMovies();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("GRID", "Fragment - onStart");
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
                Log.i("GRID", "Fragment - onCreateLoader 0");
                loader = new CursorLoader(getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_IMAGE_PATH},
                        null,
                        null,
                        MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY + " DESC LIMIT 20");
                break;

            case 1:

                Log.i("GRID", "Fragment - onCreateLoader 1");
                loader = new CursorLoader(getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_IMAGE_PATH},
                        null,
                        null,
                        MovieContract.MovieEntry.COLUMN_RATING + " DESC LIMIT 20");

                break;

            case 2:

                Log.i("GRID", "Fragment - onCreateLoader 2");
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
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        adapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();

        // SELECT * FROM MOVIE WHERE _id = position
        Cursor movieCursor = getActivity().getContentResolver().query(
                uri,
                null,
                MovieContract.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null);

        if (movieCursor == null || !movieCursor.moveToFirst()) {

        } else {

        }

        int idIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
        Log.d("MOVIE", idIndex + "");
        int titleIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        Log.d("MOVIE", titleIndex + "");
        int remoteIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_REMOTE_MOVIE_ID);
        Log.d("MOVIE", remoteIdIndex + "");
        int pathIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_PATH);
        Log.d("MOVIE", pathIndex + "");
        int overviewIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        Log.d("MOVIE", overviewIndex + "");
        int ratingIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
        Log.d("MOVIE", ratingIndex + "");
        int dateIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE);
        Log.d("MOVIE", dateIndex + "");
        int favoriteIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_FAVORITE);
        Log.d("MOVIE", favoriteIndex + "");


        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);

        intent.putExtra(Constants.EXTRA_LOCAL_ID, movieCursor.getInt(idIndex));
        Log.d("MOVIE", movieCursor.getInt(idIndex) + "");
        intent.putExtra(Constants.EXTRA_TITLE, movieCursor.getString(titleIndex));
        Log.d("MOVIE",  movieCursor.getString(titleIndex) + "");
        intent.putExtra(Constants.EXTRA_REMOTE_ID, movieCursor.getInt(remoteIdIndex));
        Log.d("MOVIE", movieCursor.getInt(remoteIdIndex) + "");
        intent.putExtra(Constants.EXTRA_PATH, movieCursor.getString(pathIndex));
        Log.d("MOVIE", movieCursor.getString(pathIndex) + "");
        intent.putExtra(Constants.EXTRA_OVERVIEW, movieCursor.getString(overviewIndex));
        Log.d("MOVIE", movieCursor.getString(overviewIndex) + "");
        intent.putExtra(Constants.EXTRA_RATING, movieCursor.getDouble(ratingIndex));
        Log.d("MOVIE", movieCursor.getDouble(ratingIndex) + "");
        intent.putExtra(Constants.EXTRA_DATE, movieCursor.getString(dateIndex));
        Log.d("MOVIE", movieCursor.getString(dateIndex) + "");
        intent.putExtra(Constants.EXTRA_IS_FAVORITE, movieCursor.getInt(favoriteIndex));
        Log.d("MOVIE", movieCursor.getInt(favoriteIndex) + "");
        getActivity().startActivity(intent);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onItemSelected(id);
        }

        movieCursor.close();
    }


    private void updateMovies() {


        mDialog = new ProgressDialog(getActivity());

        mDialog.setMessage(getString(R.string.loading));

        mDialog.show();

        // Instantiate the RequestQueue.
        mRequestQueue = VolleySingleton.getsInstance().getRequestQueue();

        // Creating url
        String url = BASE_REQUEST_URL + POPULAR_MOVIES + ApiKey.API_KEY;

        mJsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // mErrorView.setVisibility(View.GONE);
                mJsObjRequest.setTag(TAG_REQUEST_POPULAR);

                // Parse the response
                parseJSONResponse(response);

                mDialog.hide();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mDialog.hide();

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
    private void parseJSONResponse(JSONObject response) {

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

                            Log.d("DB", "popularity: " + popularity);
                            long dbMovieId = addMovie(id, title, posterPath, overview, average, release, popularity);


                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private long addMovie(long remoteMovieId, String title, String posterImagePath, String overview, double rating, String releasDate, double popularity) {


        long movieId = 0;
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

        // Wait, that worked?  Yes!
        return movieId;


    }

    @Override
    public void onSortByPopular() {
        Toast.makeText(getActivity(), "Sorted by Popularity", Toast.LENGTH_SHORT).show();
        getLoaderManager().initLoader(0, null, this);


    }

    @Override
    public void onSortByRating() {
        Toast.makeText(getActivity(), "Sorted by Rating", Toast.LENGTH_SHORT).show();
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onSortByFavorites() {

        Toast.makeText(getActivity(), "Sorted by Favorites", Toast.LENGTH_SHORT).show();
        getLoaderManager().initLoader(2, null, this);

    }
}