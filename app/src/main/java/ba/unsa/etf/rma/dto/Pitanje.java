package ba.unsa.etf.rma.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Pitanje implements Parcelable {
    private String naziv;
    private String tekstPitanja;
    private ArrayList<String> odgovori = new ArrayList<>();
    private String tacan;

    public Pitanje() {
    }

    public Pitanje(String naziv, String tekstPitanja, ArrayList<String> odgovori, String tacan) {
        this.naziv = naziv;
        this.tekstPitanja = tekstPitanja;
        this.odgovori = odgovori;
        this.tacan = tacan;
    }

    public Pitanje(String naziv, String tekstPitanja, String tacan) {
        this.naziv = naziv;
        this.tekstPitanja = tekstPitanja;
        odgovori.add(tacan);
        this.tacan = tacan;
    }

    protected Pitanje(Parcel in) {
        naziv = in.readString();
        tekstPitanja = in.readString();
        odgovori = in.createStringArrayList();
        tacan = in.readString();
    }

    public static final Creator<Pitanje> CREATOR = new Creator<Pitanje>() {
        @Override
        public Pitanje createFromParcel(Parcel in) {
            return new Pitanje(in);
        }

        @Override
        public Pitanje[] newArray(int size) {
            return new Pitanje[size];
        }
    };

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getTekstPitanja() {
        return tekstPitanja;
    }

    public void setTekstPitanja(String tekstPitanja) {
        this.tekstPitanja = tekstPitanja;
    }

    public ArrayList<String> getOdgovori() {
        return odgovori;
    }

    public void setOdgovori(ArrayList<String> odgovori) {
        this.odgovori = odgovori;
    }

    public String getTacan() {
        return tacan;
    }

    public void setTacan(String tacan) {
        this.tacan = tacan;
    }

    public ArrayList<String> dajRandomOdgovore() {
        ArrayList<String> randomOdgovori = odgovori;
        Collections.shuffle(randomOdgovori);
        return randomOdgovori;
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
        dest.writeString(tekstPitanja);
        dest.writeStringList(odgovori);
        dest.writeString(tacan);
    }

    public boolean postojiOdgovor(String odgovor) {
        return odgovori.contains(odgovor);
    }

    public void dodajOdgovor(String odgovor) {
        odgovori.add(odgovor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pitanje pitanje = (Pitanje) o;
        return Objects.equals(naziv, pitanje.naziv);
    }

}