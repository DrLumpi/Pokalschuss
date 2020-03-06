package pokal.model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class Zug {
    private final int id;
    private final String name;

    private ArrayList<Mitglied> mitglieder = new ArrayList<>();
    private int zehner = 0;

    public Zug(final int id, final String name) {
        this.id = id;
        this.name = name;
        for (int i = 0; i < 10; i++) {
            final Mitglied mitglied = new Mitglied();
            mitglied.setId(id + i);
            mitglieder.add(mitglied);
        }
    }

    @JsonCreator
    public Zug(@JsonProperty("id") final int id,
               @JsonProperty("name") final String name,
               @JsonProperty("mitglieder") final ArrayList<Mitglied> mitglieder,
               @JsonProperty("zehner") final int zehner) {
        this.id = id;
        this.name = name;
        this.mitglieder = mitglieder;
        this.zehner = zehner;
    }

    public int getZehner() {
        return zehner;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Mitglied> getMitglieder() {
        return mitglieder;
    }

    @JsonIgnore
    public String getIdFormatted() {
        return String.format("%03d", id);
    }

    @JsonIgnore
    public ArrayList<Mitglied> getSortedMitglieder() {
        ArrayList<Mitglied> ret = new ArrayList<>(mitglieder);
        ret.sort(Comparator.comparingInt(Mitglied::getShot).reversed());
        return ret;
    }

    @JsonIgnore
    public int getScore() {
        final ArrayList<Mitglied> tmp = new ArrayList<>(mitglieder);
        tmp.sort(Comparator.comparingInt(Mitglied::getShot).reversed());
        int ret = 0;
        for (int i = 0; i < 5; i++) {
            if (tmp.get(i).getShot() > 0) {
                ret += tmp.get(i).getShot();
            }
        }
        return ret;
    }
}
