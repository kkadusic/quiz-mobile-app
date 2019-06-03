package ba.unsa.etf.rma.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class Ranglista implements Parcelable {
    private String nazivKviza;
    private String nazivIgraca;
    private String procenatTacnih;
    private String pozicija;

    public Ranglista(String nazivKviza, String nazivIgraca, String procenatTacnih, String pozicija) {
        this.nazivKviza = nazivKviza;
        this.nazivIgraca = nazivIgraca;
        this.procenatTacnih = procenatTacnih;
        this.pozicija = pozicija;
    }

    public Ranglista() {
    }

    protected Ranglista(Parcel in) {
        nazivKviza = in.readString();
        nazivIgraca = in.readString();
        procenatTacnih = in.readString();
        pozicija = in.readString();
    }

    public static final Creator<Ranglista> CREATOR = new Creator<Ranglista>() {
        @Override
        public Ranglista createFromParcel(Parcel in) {
            return new Ranglista(in);
        }

        @Override
        public Ranglista[] newArray(int size) {
            return new Ranglista[size];
        }
    };

    public String getNazivKviza() {
        return nazivKviza;
    }

    public void setNazivKviza(String nazivKviza) {
        this.nazivKviza = nazivKviza;
    }

    public String getNazivIgraca() {
        return nazivIgraca;
    }

    public void setNazivIgraca(String nazivIgraca) {
        this.nazivIgraca = nazivIgraca;
    }

    public String getProcenatTacnih() {
        return procenatTacnih;
    }

    public void setProcenatTacnih(String procenatTacnih) {
        this.procenatTacnih = procenatTacnih;
    }

    public String getPozicija() {
        return pozicija;
    }

    public void setPozicija(String pozicija) {
        this.pozicija = pozicija;
    }

    @Override
    public String toString() {
        return "Ranglista{" +
                "nazivKviza='" + nazivKviza + '\'' +
                ", nazivIgraca='" + nazivIgraca + '\'' +
                ", procenatTacnih='" + procenatTacnih + '\'' +
                ", pozicija='" + pozicija + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nazivKviza);
        dest.writeString(nazivIgraca);
        dest.writeString(procenatTacnih);
        dest.writeString(pozicija);
    }
}
