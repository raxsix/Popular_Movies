package eu.raxsix.popularmovies.database;

import android.provider.BaseColumns;

/**
 * Created by Ragnar on 8/27/2015.
 * Defines table and column names for the Movie database.
 */
public class MovieContract {


    /**
     * Inner class that defines the contents of the movie table
     */
    public static final class MovieEntry implements BaseColumns{

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_IMAGE_PATH = "image_path";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_RATING = "rating";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_IS_FAVORITE = "favorite";

    }


    public static final class TrailerEntry implements BaseColumns{


        public static final String TABLE_NAME = "trailer";

        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_YOUTUBE_KEY = "youtube_key";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_SITE = "site";

        public static final String COLUMN_SIZE = "size";

        public static final String COLUMN_TYPE = "type";

    }

    public static final class ReviewEntry implements BaseColumns{

        public static final String TABLE_NAME = "review";

        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_AUTHOR = "author";

        public static final String COLUMN_CONTENT = "content";
    }
}
