package eu.raxsix.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import eu.raxsix.popularmovies.MovieDetailActivity;
import eu.raxsix.popularmovies.R;
import eu.raxsix.popularmovies.extras.Constants;
import eu.raxsix.popularmovies.network.VolleySingleton;
import eu.raxsix.popularmovies.pojo.Movie;

import static eu.raxsix.popularmovies.extras.Constants.*;
/**
 * Created by Ragnar on 8/23/2015.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie> movies;
    private LayoutInflater inflater;
    private Context context;

    private VolleySingleton mVolleySingleton;
    private ImageLoader mImageLoader;


    public MovieAdapter(Context context, List<Movie> movies) {
        this.movies = movies;
        this.inflater = LayoutInflater.from(context);
        this.context = context;

        mVolleySingleton = VolleySingleton.getsInstance();
        mImageLoader = mVolleySingleton.getImageLoader();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_movie, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Movie current = movies.get(position);

        if (!current.getPosterImagePath().equals(Constants.NA)) {

            mImageLoader.get(BASE_URL + IMAGE_SIZE + current.getPosterImagePath(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    holder.posterImage.setImageBitmap(response.getBitmap());
                }


                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView posterImage;

        public ViewHolder(View itemView) {
            super(itemView);

            posterImage = (ImageView) itemView.findViewById(R.id.posterImageView);

            posterImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getLayoutPosition(); // gets item position
            Movie movie = movies.get(position);


            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("path", movie.getPosterImagePath());
            intent.putExtra("overview", movie.getOverview());
            intent.putExtra("rating", movie.getRating());
            intent.putExtra("date", movie.getReleaseDate());
            context.startActivity(intent);

        }
    }
}
