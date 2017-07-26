package app.com.example.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ankit on 3/7/16.
 */
public class MovieInfo implements Parcelable {
    @SerializedName("poster_path")
    private String _posterPath;
    @SerializedName("overview")
    private String _plot;
    @SerializedName("title")
    private String _title;
    @SerializedName("release_date")
    private String _release;
    @SerializedName("vote_average")
    private String _rating;
    @SerializedName("id")
    private String _id;

    public MovieInfo(Parcel in) {
        _posterPath = in.readString();
        _plot = in.readString();
        _title = in.readString();
        _release = in.readString();
        _rating = in.readString();
        _id = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getUrl());
        dest.writeString(getPlot());
        dest.writeString(getTitle());
        dest.writeString(getRelease());
        dest.writeString(getRating());
        dest.writeString(getId());
    }

    //Constructor for class
    public MovieInfo(String url, String plot, String title, String release, String rating, String id) {
        _posterPath = url;
        _plot = plot;
        _title = title;
        _release = release;
        _rating = rating;
        _id = id;
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
    // Getter methods
    public String getUrl() {
        return _posterPath;
    }

    public String getPlot() {
        return _plot;
    }

    public String getTitle() {
        return _title;
    }

    public String getRelease() {
        return _release;
    }

    public String getRating() {
        return _rating;
    }

    public String getId() {
        return _id;
    }
}
