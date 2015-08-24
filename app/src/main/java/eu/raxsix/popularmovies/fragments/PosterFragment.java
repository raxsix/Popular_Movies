package eu.raxsix.popularmovies.fragments;


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
public class PosterFragment extends Fragment {

    private static final String BASE_REQUEST_URL = "http://api.themoviedb.org/3/discover/movie?";
    private static final String POPULAR_MOVIES = "/discover/movie?sort_by=popularity.desc&api_key=";

    private List<Movie> mMovieList;
    private RecyclerView mRecyclerView;
    private TextView mErrorView;
    ProgressDialog mDialog;

    public PosterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

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


        mDialog.show();
        // Instantiate the RequestQueue.
        RequestQueue requestQueue = VolleySingleton.getsInstance().getRequestQueue();

        // Creating url
        String url = BASE_REQUEST_URL + POPULAR_MOVIES + ApiKey.API_KEY;

        Log.d("Test", url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                mErrorView.setVisibility(View.GONE);

                parseJSONResponse(response);

                mDialog.hide();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                handleVolleyError(error);

                mDialog.hide();

            }
        });

        // Add the request to the RequestQueue.
        requestQueue.add(jsObjRequest);

        MovieAdapter adapter = new MovieAdapter(getActivity(), mMovieList);
        mRecyclerView.setAdapter(adapter);

        // Enable optimizations if all item views are of the same height and width for significantly smoother scrolling:
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));


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


                        title = movie.getString(KEY_ORIGINAL_TITLE);
                        overview = movie.getString(KEY_OVERVIEW);
                        average = movie.getDouble(KEY_VOTE_AVARAGE);
                        release = movie.getString(KEY_RELEASE_DATE);


                        // If movie does not have title or id do not but it to the moves list
                        if (id != -1 && !title.equals(Constants.NA)) {

                            mMovieList.add(new Movie(id, title, posterPath, overview, average, release));
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

        }else if (error instanceof AuthFailureError){

            //TODO
        }else if (error instanceof ServerError){

            //TODO
        }else if (error instanceof NetworkError){

            //TODO
        }else if (error instanceof ParseError){

            //TODO
        }


    }

}
