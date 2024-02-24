package in.navedansari.tpoapp.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlacementRecord implements Parcelable {
    @SerializedName("rollNo")
    private Integer rollNo;
    @SerializedName("name")
    private String name;
    @SerializedName("gender")
    private String gender;
    @SerializedName("course")
    private String course;
    @SerializedName("branch")
    private String branch;
    @SerializedName("email")
    private String email;
    @SerializedName("phoneNo")
    private String phoneNo;
    @SerializedName("placedIn")
    private List<PlacedIn> placedIn;

    public PlacementRecord(){
    }

    public PlacementRecord(Integer rollNo, String name, String gender, String course, String branch, String email, String phoneNo, List<PlacedIn> placedIn) {
        this.rollNo = rollNo;
        this.name = name;
        this.gender = gender;
        this.course = course;
        this.branch = branch;
        this.email = email;
        this.phoneNo = phoneNo;
        this.placedIn = placedIn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(rollNo);
        dest.writeString(name);
        dest.writeString(gender);
        dest.writeString(course);
        dest.writeString(branch);
        dest.writeString(email);
        dest.writeString(phoneNo);
        dest.writeTypedList(placedIn);
    }

    protected PlacementRecord(Parcel in) {
        rollNo = in.readInt();
        name = in.readString();
        gender = in.readString();
        course = in.readString();
        branch = in.readString();
        email = in.readString();
        phoneNo = in.readString();
        placedIn = in.createTypedArrayList(PlacedIn.CREATOR);
    }

    public static final Parcelable.Creator<PlacementRecord> CREATOR = new Parcelable.Creator<PlacementRecord>() {
        @Override
        public PlacementRecord createFromParcel(Parcel in) {
            return new PlacementRecord(in);
        }

        @Override
        public PlacementRecord[] newArray(int size) {
            return new PlacementRecord[size];
        }
    };

    public Integer getRollNo() {
        return rollNo;
    }

    public void setRollNo(Integer rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public List<PlacedIn> getPlacedIn() {
        return placedIn;
    }

    public void setPlacedIn(List<PlacedIn> placedIn) {
        this.placedIn = placedIn;
    }
}
