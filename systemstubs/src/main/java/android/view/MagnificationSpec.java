package android.view;

import android.os.Parcel;
import android.os.Parcelable;

//Stub
public class MagnificationSpec implements Parcelable {
    protected MagnificationSpec(Parcel in) {
    }

    public static final Creator<MagnificationSpec> CREATOR = new Creator<MagnificationSpec>() {
        @Override
        public MagnificationSpec createFromParcel(Parcel in) {
            return new MagnificationSpec(in);
        }

        @Override
        public MagnificationSpec[] newArray(int size) {
            return new MagnificationSpec[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
