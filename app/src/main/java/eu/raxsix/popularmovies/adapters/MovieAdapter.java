package eu.raxsix.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.List;

import eu.raxsix.popularmovies.R;
import eu.raxsix.popularmovies.network.VolleySingleton;
import eu.raxsix.popularmovies.pojo.Movie;

/**
 * Created by Ragnar on 8/23/2015.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w500";

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

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView posterImage;

        public ViewHolder(View itemView) {
            super(itemView);

            posterImage = (ImageView) itemView.findViewById(R.id.posterImageView);
        }
    }
}
