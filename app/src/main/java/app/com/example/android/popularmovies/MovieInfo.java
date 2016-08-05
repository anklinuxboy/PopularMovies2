package app.com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ankit on 3/7/16.
 */
public class MovieInfo implements Parcelable {
    private String _posterPath;
    private String _plot;
    private String _title;
    private String _release;
    private String _rating;

    //Constructor for class
    public MovieInfo(String url, String plot, String title, String release, String rating) {
        _posterPath = url;
        _plot = plot;
        _title = title;
        _release = release;
        _rating = rating;
    }

    // Constructor for reading from Parcel.
    public MovieInfo(Parcel in) {
        readFromParcel(in);
    }

    // Reads data from the parcel
    private void readFromParcel(Parcel in) {
        _posterPath = in.readString();
        _plot = in.readString();
        _title = in.readString();
        _release = in.readString();
        _rating = in.readString();
    }

    // Write to parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getUrl());
        out.writeString(getPlot());
        out.writeString(getTitle());
        out.writeString(getRelease());
        out.writeString(getRating());
    }

    // Parcel creator
    public static final Parcelable.Creator<MovieInfo> CREATOR
            = new Parcelable.Creator<MovieInfo>() {
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    // Must have method.
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


}
