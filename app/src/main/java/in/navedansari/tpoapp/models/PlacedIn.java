package in.navedansari.tpoapp.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

public class PlacedIn implements Parcelable {
    @SerializedName("company")
    private String company;
    @SerializedName("drive")
    private String drive;
    @SerializedName("ctc")
    private Float ctc;

    public PlacedIn(String company, String drive, Float ctc) {
        this.company = company;
        this.drive = drive;
        this.ctc = ctc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(company);
        dest.writeString(drive);
        dest.writeFloat(ctc);
    }

    protected PlacedIn(Parcel in) {
        company = in.readString();
        drive = in.readString();
        ctc = in.readFloat();
    }

    public static final Parcelable.Creator<PlacedIn> CREATOR = new Parcelable.Creator<PlacedIn>() {
        @Override
        public PlacedIn createFromParcel(Parcel in) {
            return new PlacedIn(in);
        }

        @Override
        public PlacedIn[] newArray(int size) {
            return new PlacedIn[size];
        }
    };

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDrive() {
        return drive;
    }

    public void setDrive(String drive) {
        this.drive = drive;
    }

    public Float getCtc() {
        return ctc;
    }

    public void setCtc(Float ctc) {
        this.ctc = ctc;
    }
}
