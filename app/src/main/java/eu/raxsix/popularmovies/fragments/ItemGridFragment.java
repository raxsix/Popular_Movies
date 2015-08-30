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
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_POSTER_PATH;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_RELEASE_DATE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_RESULTS;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_VOTE_AVERAGE;

/**
 * Created by Ragnar on 8/30/2015.
 */
public class ItemGridFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>,SortListener {


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
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("GRID", "Fragment - onStart");
        updateMovies();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("GRID", "Fragment - onCreateLoader");
        CursorLoader loader = new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_IMAGE_PATH},
                null,
                null,
                null);

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

        Log.d("INTENT", "position: " + position);
        Log.d("INTENT", "id: " + id);

        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();

        Log.d("INTENT", "uri: " + uri);

        // SELECT * FROM MOVIE WHERE _id = position
        Cursor movieCursor = getActivity().getContentResolver().query(
                uri,
                null,
                MovieContract.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null);

        if (movieCursor == null || !movieCursor.moveToFirst()){

            Log.d("INTENT", "POLE MIDAGI SEES");
        }else {
            Log.d("INTENT", "position" + movieCursor.getPosition());
            Log.d("INTENT", "columns" + movieCursor.getColumnCount());
            Log.d("INTENT", "count" + movieCursor.getCount());

        }

        int titleIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int pathIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_PATH);
        int overviewIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        int ratingIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
        int dateIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE);

        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
        intent.putExtra(Constants.EXTRA_TITLE, movieCursor.getString(titleIndex));
        intent.putExtra(Constants.EXTRA_PATH, movieCursor.getString(pathIndex));
        intent.putExtra(Constants.EXTRA_OVERVIEW, movieCursor.getString(overviewIndex));
        intent.putExtra(Constants.EXTRA_RATING, movieCursor.getDouble(ratingIndex));
        intent.putExtra(Constants.EXTRA_DATE, movieCursor.getString(dateIndex));
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


                        // If movie does not have title or id do not but it to the movies list
                        if (id != -1 && !title.equals(Constants.NA)) {

                            long dbMovieId = addMovie(id, title, posterPath, overview, average, release);

                            Log.d("DB", dbMovieId + "");

                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private long addMovie(long remoteMovieId, String title, String posterImagePath, String overview, double rating, String releasDate) {


        long movieId = 0;


        Cursor movieCursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,                      // SELECT ID FROM MOVIE WHERE remote_movie_id = remoteMovieId;
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry.COLUMN_REMOTE_MOVIE_ID + " = ?",
                new String[]{String.valueOf(remoteMovieId)},
                null);

        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
            movieId = movieCursor.getLong(movieIdIndex);
            Log.d("DB", movieId + " is already in db");
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
            movieValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 0);

            // Finally, insert movie data into the database.
            Uri insertedUri = getActivity().getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    movieValues
            );

            // The resulting URI contains the ID for the row.  Extract the movieId from the Uri.
            movieId = ContentUris.parseId(insertedUri);
            Log.d("DB", movieId + " first time inserted to db");
        }

        movieCursor.close();
        // Wait, that worked?  Yes!
        return movieId;


    }

    @Override
    public void onSortByPopular() {
        Toast.makeText(getActivity(),"onSortByPopular", Toast.LENGTH_SHORT).show();
        updateMovies();
    }

    @Override
    public void onSortByRating() {
        Toast.makeText(getActivity(),"onSortByRating", Toast.LENGTH_SHORT).show();
    }
}