package eu.raxsix.popularmovies.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_ID;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_ORIGINAL_TITLE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_OVERVIEW;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_POSTER_PATH;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_RELEASE_DATE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_RESULTS;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_VOTE_AVARAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PosterFragment extends Fragment implements SortListener {

    private static final String BASE_REQUEST_URL = "http://api.themoviedb.org/3/discover/movie?";
    private static final String POPULAR_MOVIES = "sort_by=popularity.desc&api_key=";
    private static final String MOST_RATED_MOVIES = "certification_country=US&certification.lte=G&sort_by=popularity.desc&api_key=";


    private List<Movie> mMovieList;
    private List<Movie> mTopRatedMovieList;
    private MovieAdapter movieAdapter;
    private MovieAdapter mTopRatedAdapter;
    private RecyclerView mRecyclerView;
    private JsonObjectRequest mJsObjRequest;
    private TextView mErrorView;
    private RequestQueue mRequestQueue;
    ProgressDialog mDialog;

    public PosterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LC", "Fragment onCreate");
        Log.d("test", "PosterFragment onCreate");
        setHasOptionsMenu(true);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("LC","Fragment onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_poster, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("LC", "Fragment onResume");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("LC", "Fragment onStart");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("LC", "Fragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("LC", "Fragment onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("LC", "Fragment onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d("test", "Fragment onDetach");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.d("LC", "Fragment onAttach");

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("LC", "Fragment onViewCreated");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("LC", "Fragment onActivityCreated");

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

            Log.d("Test", url);

            mJsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    mErrorView.setVisibility(View.GONE);
                    mJsObjRequest.setTag("popular");

                    Log.d("test", "popular tag has been set");

                    parseJSONResponse(response);

                    mDialog.hide();

                    movieAdapter = new MovieAdapter(getActivity(), mMovieList);
                    mRecyclerView.setAdapter(movieAdapter);

                    // Enable optimizations if all item views are of the same height and width for significantly smoother scrolling:
                    mRecyclerView.setHasFixedSize(true);


                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    handleVolleyError(error);

                    mDialog.hide();

                }
            });

            // Add the request to the RequestQueue.
            mRequestQueue.add(mJsObjRequest);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.poster_fragment_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_by_popular) {

            return true;
        }
        return super.onOptionsItemSelected(item);


    }

    private void parseJSONResponse(JSONObject response) {

        if (response != null && response.length() > 0) {

            Log.d("test", "in parseJSONResponse");
            try {

                if (response.has(KEY_RESULTS)) {

                    Log.d("test", "parsing json has key results");

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

                            Log.d("test", "has key ID");
                            id = movie.getLong(KEY_ID);
                        }

                        if (movie.has(KEY_POSTER_PATH) && !movie.isNull(KEY_POSTER_PATH)) {

                            Log.d("test", "has key path");
                            posterPath = movie.getString(KEY_POSTER_PATH);
                        }

                        if (movie.has(KEY_ORIGINAL_TITLE) && !movie.isNull(KEY_ORIGINAL_TITLE)) {


                            title = movie.getString(KEY_ORIGINAL_TITLE);
                        }

                        if (movie.has(KEY_OVERVIEW) && !movie.isNull(KEY_OVERVIEW)) {

                            overview = movie.getString(KEY_OVERVIEW);
                        }

                        if (movie.has(KEY_VOTE_AVARAGE) && !movie.isNull(KEY_VOTE_AVARAGE)) {

                            average = movie.getDouble(KEY_VOTE_AVARAGE);
                        }

                        if (movie.has(KEY_RELEASE_DATE) && !movie.isNull(KEY_RELEASE_DATE)) {

                            release = movie.getString(KEY_RELEASE_DATE);
                        }


                        // If movie does not have title or id do not but it to the movies list
                        if (id != -1 && !title.equals(Constants.NA)) {

                            if (mJsObjRequest.getTag().equals("popular")) {
                                Log.d("test", "mJsObjRequest tag equals popular");

                                mMovieList.add(new Movie(id, title, posterPath, overview, average, release));

                            }
                            if (mJsObjRequest.getTag().equals("rated")) {

                                Log.d("test", "mJsObjRequest tag equals rated");
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

    private void handleVolleyError(VolleyError error) {

        mErrorView.setVisibility(View.VISIBLE);

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

            mErrorView.setText(R.string.error_timeout);
            //Toast.makeText(getActivity(), R.string.error_timeout, Toast.LENGTH_LONG).show();

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

    @Override
    public void onSortByPopular() {

        Log.d("test", "onSortByPopular");
        mRecyclerView.setAdapter(movieAdapter);

    }

    @Override
    public void onSortByRating() {


        if (mTopRatedMovieList != null && !mTopRatedMovieList.isEmpty() && mTopRatedMovieList.size() > 0) {

            Log.d("test", "mTopRatedMovieList not empty and not null");
            mRecyclerView.setAdapter(mTopRatedAdapter);

        } else {

            Log.d("test", "onSortByRating");
            // Creating url
            String url = BASE_REQUEST_URL + MOST_RATED_MOVIES + ApiKey.API_KEY;
            Log.d("test", "onSortByRating url: " + url);

            mJsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.d("test", "in onSortByRating response");
                    mErrorView.setVisibility(View.GONE);


                    parseJSONResponse(response);

                    mDialog.hide();


                    Log.d("test", mTopRatedMovieList.size() + "");
                    mTopRatedAdapter = new MovieAdapter(getActivity(), mTopRatedMovieList);

                    mRecyclerView.setAdapter(mTopRatedAdapter);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    handleVolleyError(error);

                    mDialog.hide();

                }
            });

            mJsObjRequest.setTag("rated");
            Log.d("test", "mJsObjRequest tag has set to rated");
            // Add the request to the RequestQueue.
            mRequestQueue.add(mJsObjRequest);


        }
    }


}
