package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Kviz implements Serializable, Parcelable {
    private String naziv;
    private ArrayList<Pitanje> pitanja = new ArrayList<>();
    private Kategorija kategorija;

    public Kviz(String naziv, ArrayList<Pitanje> pitanja, Kategorija kategorija) {
        this.naziv = naziv;
        this.pitanja = pitanja;
        this.kategorija = kategorija;
    }

    public Kviz() {
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public ArrayList<Pitanje> getPitanja() {
        return pitanja;
    }

    public void setPitanja(ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public void dodajPitanje(Pitanje p) {
        pitanja.add(p);
    }

    protected Kviz(Parcel in) {
        naziv = in.readString();
        if (in.readByte() == 0x01) {
            pitanja = new ArrayList<Pitanje>();
            in.readList(pitanja, Pitanje.class.getClassLoader());
        } else {
            pitanja = null;
        }
        kategorija = (Kategorija) in.readValue(Kategorija.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(naziv);
        if (pitanja == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(pitanja);
        }
        dest.writeValue(kategorija);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Kviz> CREATOR = new Parcelable.Creator<Kviz>() {
        @Override
        public Kviz createFromParcel(Parcel in) {
            return new Kviz(in);
        }

        @Override
        public Kviz[] newArray(int size) {
            return new Kviz[size];
        }
    };
}