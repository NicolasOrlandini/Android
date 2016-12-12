package orlandini.jeu;

import android.widget.Toast;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Scott on 21/11/2016.
 */

public class Repo {

    final String name;

    public String getName() {
        return name;
    }

    public Repo(String aName) {
        this.name = aName;
    }
}
