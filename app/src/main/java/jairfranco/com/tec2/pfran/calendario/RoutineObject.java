package jairfranco.com.tec2.pfran.calendario;

import android.os.Parcel;
import android.os.Parcelable;

public class RoutineObject implements Parcelable{


    private String mfechIni;
    private String descr;
    private String mtitulo;
    private String mHourIni;
    private String mHourFin;


    public RoutineObject(String mfechIni, String descr, String mtitulo, String mHourIni, String mHourFin) {
        this.mfechIni = mfechIni;
        this.descr = descr;
        this.mtitulo = mtitulo;
        this.mHourIni = mHourIni;
        this.mHourFin = mHourFin;
    }

    public RoutineObject() {
    }

    protected RoutineObject(Parcel in) {
        mfechIni = in.readString();
        descr = in.readString();
        mtitulo = in.readString();
        mHourIni = in.readString();
        mHourFin = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mfechIni);
        dest.writeString(descr);
        dest.writeString(mtitulo);
        dest.writeString(mHourIni);
        dest.writeString(mHourFin);
    }

    @SuppressWarnings("unused")
    public static final Creator<RoutineObject> CREATOR = new Creator<RoutineObject>() {
        @Override
        public RoutineObject createFromParcel(Parcel in) {
            return new RoutineObject(in);
        }

        @Override
        public RoutineObject[] newArray(int size) {
            return new RoutineObject[size];
        }
    };


    public String getMfechIni() {
        return mfechIni;
    }

    public void setMfechIni(String mfechIni) {
        this.mfechIni = mfechIni;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getMtitulo() {
        return mtitulo;
    }

    public void setMtitulo(String mtitulo) {
        this.mtitulo = mtitulo;
    }

    public String getmHourIni() {
        return mHourIni;
    }

    public void setmHourIni(String mHourIni) {
        this.mHourIni = mHourIni;
    }

    public String getmHourFin() {
        return mHourFin;
    }

    public void setmHourFin(String mHourFin) {
        this.mHourFin = mHourFin;
    }

    public static Creator<RoutineObject> getCREATOR() {
        return CREATOR;
    }
}
