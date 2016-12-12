package orlandini.jeu.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import orlandini.jeu.MainActivity;

/**
 * Created by Scott on 28/11/2016.
 */

public class Score extends RealmObject {

    private int score;

    @PrimaryKey
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
