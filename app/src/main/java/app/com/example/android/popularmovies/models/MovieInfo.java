package app.com.example.android.popularmovies.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ankit on 3/7/16.
 */
@Entity(tableName = "movies")
public class MovieInfo implements Parcelable {

    @Ignore
    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("overview")
    private String plot;

    @SerializedName("title")
    private String title;

    @SerializedName("release_date")
    private String release;

    @Ignore
    @SerializedName("vote_average")
    private String rating;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "movieId")
    @SerializedName("id")
    private String id;

    @ColumnInfo(name = "poster_url")
    private String posterUrl;

    @ColumnInfo(name = "movie_rating")
    private String voterRating;

    @ColumnInfo(name = "sort_setting")
    private String sortSetting;

    @ColumnInfo(name = "favorite")
    private Boolean favorite;

    public MovieInfo(Parcel in) {
        posterPath = in.readString();
        plot = in.readString();
        title = in.readString();
        release = in.readString();
        rating = in.readString();
        id = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getPosterPath());
        dest.writeString(getPlot());
        dest.writeString(getTitle());
        dest.writeString(getRelease());
        dest.writeString(getRating());
        dest.writeString(getId());
    }

    public MovieInfo() {
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getVoterRating() {
        return voterRating;
    }

    public void setVoterRating(String voterRating) {
        this.voterRating = voterRating;
    }

    public String getSortSetting() {
        return sortSetting;
    }

    public void setSortSetting(String sortSetting) {
        this.sortSetting = sortSetting;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}
