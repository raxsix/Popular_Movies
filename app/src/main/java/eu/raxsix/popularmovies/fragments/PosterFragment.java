package eu.raxsix.popularmovies.fragments;


import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

import eu.raxsix.popularmovies.Interfaces.SortListener;
import eu.raxsix.popularmovies.R;
import eu.raxsix.popularmovies.adapters.MovieAdapter;
import eu.raxsix.popularmovies.api_key.ApiKey;
import eu.raxsix.popularmovies.extras.Constants;
import eu.raxsix.popularmovies.network.VolleySingleton;
import eu.raxsix.popularmovies.pojo.Movie;

import static eu.raxsix.popularmovies.extras.Constants.BASE_REQUEST_URL;
import static eu.raxsix.popularmovies.extras.Constants.MOST_RATED_MOVIES;
import static eu.raxsix.popularmovies.extras.Constants.POPULAR_MOVIES;
import static eu.raxsix.popularmovies.extras.Constants.TAG_REQUEST_POPULAR;
import static eu.raxsix.popularmovies.extras.Constants.TAG_REQUEST_RATED;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_ID;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_ORIGINAL_TITLE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_OVERVIEW;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_POSTER_PATH;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_RELEASE_DATE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_RESULTS;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_VOTE_AVERAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PosterFragment extends Fragment implements SortListener {

    private List<Movie> mMovieList;
    private List<Movie> mTopRatedMovieList;
    private MovieAdapter movieAdapter;
    private MovieAdapter mTopRatedAdapter;
    private RecyclerView mRecyclerView;
    private JsonObjectRequest mJsObjRequest;
    private TextView mErrorView;
    private RequestQueue mRequestQueue;
    private ProgressDialog mDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_poster, container, false);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDialog = new ProgressDialog(getActivity());

        mDialog.setMessage(getString(R.string.loading));

        mErrorView = (TextView) getActivity().findViewById(R.id.errorView);
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);

        mMovieList = new ArrayList<>();
        mTopRatedMovieList = new ArrayList<>();

        mDialog.show();

        // Instantiate the RequestQueue.
        mRequestQueue = VolleySingleton.getsInstance().getRequestQueue();

        // Creating url
        String url = BASE_REQUEST_URL + POPULAR_MOVIES + ApiKey.API_KEY;

        mJsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                mErrorView.setVisibility(View.GONE);
                mJsObjRequest.setTag(TAG_REQUEST_POPULAR);

                // Parse the response
                parseJSONResponse(response);

                mDialog.hide();

                // Create adapter for recyclerView
                movieAdapter = new MovieAdapter(getActivity(), mMovieList);

                // Set the adapter
                mRecyclerView.setAdapter(movieAdapter);

                // Enable optimizations if all item views are of the same height and width for significantly smoother scrolling:
                mRecyclerView.setHasFixedSize(true);

                // Set the layout manager for the recyclerView
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                handleVolleyError(error);

                mDialog.hide();

                // TODO when the network connection handling are ok, remove it, right now is set to GONE to prevent crashing the app when tapping on empty reciclerview
                mRecyclerView.setVisibility(View.GONE);
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

                            if (mJsObjRequest.getTag().equals(TAG_REQUEST_POPULAR)) {

                                // Build up the popular movie list
                                mMovieList.add(new Movie(id, title, posterPath, overview, average, release));

                            }
                            if (mJsObjRequest.getTag().equals(TAG_REQUEST_RATED)) {

                                // Build up the highest rated movie list
                                mTopRatedMovieList.add(new Movie(id, title, posterPath, overview, average, release));
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Custom method for handling different Volley errors
     *
     */
    private void handleVolleyError(VolleyError error) {

        mErrorView.setVisibility(View.VISIBLE);

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

            mErrorView.setText(R.string.error_timeout);


        } else if (error instanceof AuthFailureError) {

            //TODO
        } else if (error instanceof ServerError) {

            //TODO
        } else if (error instanceof NetworkError) {

            //TODO
        } else if (error instanceof ParseError) {

            //TODO
        }


    }

    /**
     * Implemented Interface method
     * When the popular movies button is clicked this method is called
     */
    @Override
    public void onSortByPopular() {

        // Change the adapter for recyclerView
        mRecyclerView.setAdapter(movieAdapter);
    }


    /**
     * Implemented Interface method
     * When the highest rated movie button is clicked this method is called
     */
    @Override
    public void onSortByRating() {

        // If highest rated movie list is not null it means we already made a network call and we have the list
        if (mTopRatedMovieList != null && !mTopRatedMovieList.isEmpty() && mTopRatedMovieList.size() > 0) {

            // Change the adapter
            mRecyclerView.setAdapter(mTopRatedAdapter);

        } else {

            // if highest rated movie list is null we have to make a network call

            // Creating url
            String url = BASE_REQUEST_URL + MOST_RATED_MOVIES + ApiKey.API_KEY;

            // Make a network call
            mJsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    mErrorView.setVisibility(View.GONE);

                    parseJSONResponse(response);

                    mDialog.hide();

                    mTopRatedAdapter = new MovieAdapter(getActivity(), mTopRatedMovieList);

                    mRecyclerView.setAdapter(mTopRatedAdapter);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    mDialog.hide();
                    handleVolleyError(error);


                }
            });

            mJsObjRequest.setTag(TAG_REQUEST_RATED);

            // Add the request to the RequestQueue.
            mRequestQueue.add(mJsObjRequest);
        }
    }

    /**
     * Callback when the phone rotates
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If in landscape mode then show 3 rows of poster
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        } else {
            // If in portrait mode then show 2 rows of poster
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }

    }
}
