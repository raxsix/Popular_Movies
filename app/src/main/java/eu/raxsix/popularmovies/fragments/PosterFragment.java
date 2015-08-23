package eu.raxsix.popularmovies.fragments;


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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.raxsix.popularmovies.MainActivity;
import eu.raxsix.popularmovies.R;
import eu.raxsix.popularmovies.adapters.MovieAdapter;
import eu.raxsix.popularmovies.api_key.ApiKey;
import eu.raxsix.popularmovies.network.VolleySingleton;
import eu.raxsix.popularmovies.pojo.Movie;

import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_RESULTS;
import static eu.raxsix.popularmovies.extras.JsonKeys.ORIGINAL_TITLE;
import static eu.raxsix.popularmovies.extras.JsonKeys.OVERVIEW;
import static eu.raxsix.popularmovies.extras.JsonKeys.POSTER_PATH;
import static eu.raxsix.popularmovies.extras.JsonKeys.RELEASE_DATE;
import static eu.raxsix.popularmovies.extras.JsonKeys.VOTE_AVARAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PosterFragment extends Fragment {

    private static final String BASE_REQUEST_URL = "http://api.themoviedb.org/3/discover/movie?";
    private static final String POPULAR_MOVIES = "/discover/movie?sort_by=popularity.desc&api_key=";

    private List<Movie> mMovieList;
    private RecyclerView mRecyclerView;

    public PosterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        mMovieList = new ArrayList<>();

        // Instantiate the RequestQueue.
        RequestQueue requestQueue = VolleySingleton.getsInstance().getRequestQueue();

        // Creating url
        String url = BASE_REQUEST_URL + POPULAR_MOVIES + ApiKey.API_KEY;

        Log.d("Test", url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //Log.d("Test", response.toString());

                parseJSONResponse(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        requestQueue.add(jsObjRequest);

        MovieAdapter adapter = new MovieAdapter(getActivity(), mMovieList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_poster, container, false);
    }

    private void parseJSONResponse(JSONObject response) {

        if (response == null || response.length() == 0) {

            return;
        }

        try {

            if (response.has(KEY_RESULTS)) {

                JSONArray jsonArray = response.getJSONArray(KEY_RESULTS);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject movie = jsonArray.getJSONObject(i);

                    String posterPath = movie.getString(POSTER_PATH);
                    String title = movie.getString(ORIGINAL_TITLE);
                    String overview = movie.getString(OVERVIEW);
                    double average = movie.getDouble(VOTE_AVARAGE);
                    String release = movie.getString(RELEASE_DATE);

                    mMovieList.add(new Movie(title, posterPath, overview, average, release));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

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
}
