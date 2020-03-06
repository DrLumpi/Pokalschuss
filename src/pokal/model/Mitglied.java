package pokal.model;

public class Mitglied {
    private int id = 0;
    private String name = "PLACEHOLDER";
    private int shot = -1;
    private boolean youth = false;

    public boolean isYouth() {
        return youth;
    }

    public void setYouth(boolean youth) {
        this.youth = youth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getShot() {
        return shot;
    }

    public void setShot(int score) {
        if(score > 50 || score < -1) {
            return;
        }
        shot = score;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }
}
