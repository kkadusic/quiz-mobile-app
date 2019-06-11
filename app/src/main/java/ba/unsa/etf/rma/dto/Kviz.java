package ba.unsa.etf.rma.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Kviz implements Parcelable {
    private String naziv;
    private ArrayList<Pitanje> pitanja = new ArrayList<>();
    private Kategorija kategorija;

    public Kviz() {
    }

    public Kviz(String naziv, ArrayList<Pitanje> pitanja, Kategorija kategorija) {
        this.naziv = naziv;
        this.pitanja = pitanja;
        this.kategorija = kategorija;
    }

    public Kviz(String naziv, Kategorija kategorija) {
        this.naziv = naziv;
        this.kategorija = kategorija;
    }

    protected Kviz(Parcel in) {
        naziv = in.readString();
        pitanja = in.createTypedArrayList(Pitanje.CREATOR);
        kategorija = in.readParcelable(Kategorija.class.getClassLoader());
    }

    public static final Creator<Kviz> CREATOR = new Creator<Kviz>() {
        @Override
        public Kviz createFromParcel(Parcel in) {
            return new Kviz(in);
        }

        @Override
        public Kviz[] newArray(int size) {
            return new Kviz[size];
        }
    };

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

    @Override
    public String toString() {
        return naziv;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(naziv);
        dest.writeTypedList(pitanja);
        dest.writeParcelable(kategorija, flags);
    }

    public void dodajPitanje(Pitanje p) {
        pitanja.add(p);
    }

    public ArrayList<String> getNaziviPitanja(){
        ArrayList<String> naziviPitanja = new ArrayList<>();
        for (Pitanje p : pitanja){
            naziviPitanja.add(p.getNaziv());
        }
        return naziviPitanja;
    }
}
