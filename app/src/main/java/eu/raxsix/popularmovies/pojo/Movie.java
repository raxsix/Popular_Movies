package eu.raxsix.popularmovies.pojo;

/**
 * Created by Ragnar on 8/23/2015.
 */
public class Movie {

    private String title;
    private String posterImagePath;
    private String overview;
    private double rating;
    private String releaseDate;

    public String getPosterImagePath() {
        return posterImagePath;
    }

    public Movie(String title, String posterImagePath, String overview, double rating, String releaseDate) {
        this.title = title;
        this.posterImagePath = posterImagePath;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterImagePath(String posterImagePath) {
        this.posterImagePath = posterImagePath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
