package orlandini.jeu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import orlandini.jeu.Fragments.AProposFragment;
import orlandini.jeu.Fragments.HelpDialogFragment;
import orlandini.jeu.Fragments.HomeFragment;
import orlandini.jeu.Leaderboard.LeaderboardFragment;
import orlandini.jeu.Fragments.SettingFragment;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Activité principale, contient le navigation drawer
 * ainsi que le fragment sélectionné par l'utilisateur depuis
 * le navigation drawer.
 *
 * @author Nicolas Orlandini
 * @version 2016.0.46
 *
 * Date de création : 09/10/2016
 * Dernière modification : 05/11/2016
 */

public class MainActivity extends AppCompatActivity{

    //Définitions des variables
    private DrawerLayout mDrawer;
    private Toolbar toolbar;

    public static NavigationView getNvDrawer() {
        return nvDrawer;
    }

    private static NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private SharedPreferences prefs;
    boolean isLeaderboard = false;
    private String nomJoueur;
    private String color;
    private boolean reinitLeaderboard;
    View nav = null;

    //variables static (pour la BDD)
    public static ScoreDataBase _scoreDataBase;
    public static Realm _realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //chargement des préférences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        recupererPreferences();

        //instanciation de la base de données
        _scoreDataBase = new ScoreDataBase(getBaseContext());
        _realm.init(this);
        _realm = Realm.getDefaultInstance();

        //changement de couleur pour la toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(recupererCouleur());
        setSupportActionBar(toolbar);

        //chargement du drawer
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        appliquerThemeNavigationDrawer();
        setupDrawerContent(nvDrawer);

        Intent intent = this.getIntent();
        reinitLeaderboard = intent.getBooleanExtra("reinitLeaderboard", false);
        // Chargement de l'actionBar
        setupActionBar();

        if (reinitLeaderboard) {
            //affiche l'écran d'accueil par défaut dans le main activity
            getSupportFragmentManager().beginTransaction().replace(R.id.main_Content, new LeaderboardFragment()).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_Content, new HomeFragment()).commit();
        }
    }

    /**
     * Configuration de l'actionBar
     * Affiche les boutons dans l'action bar
     */
    private void setupActionBar(){
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(recupererCouleur()));
        }
    }

    //Toggle l'icone hamburger
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    //sélection des items
    private void setupDrawerContent(NavigationView navigationView) {
        nvDrawer = navigationView;
        nav = navigationView.getHeaderView(0);
        nav.setBackgroundColor(recupererCouleur());
        TextView myView = (TextView) nav.findViewById(R.id.nom_joueur);
        myView.setText(nomJoueur);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    /**
     * Spécifie les actions a effectuer suivant l'item sélectionné par l'utilisateur dans le navigation drawer.
     * @param menuItem item sélectionné
     */
    public void selectDrawerItem(MenuItem menuItem) {

        Fragment fragment = null;
        Class fragmentClass;
        isLeaderboard = false;
        supportInvalidateOptionsMenu();

        // Sélection de la vue à afficher
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_leaderboard:
                fragmentClass = LeaderboardFragment.class;
                isLeaderboard = true;
                supportInvalidateOptionsMenu();
                break;
            case R.id.nav_settings:
                fragmentClass = SettingFragment.class;
                break;
            case R.id.nav_a_propos:
                fragmentClass = AProposFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Remplacer le fragment
        transaction.replace(R.id.main_Content, fragment).commit();

        // Surligner l'item sélectionné
        menuItem.setChecked(true);
        // Applique le titre de l'actionBar
        setTitle(menuItem.getTitle());

        mDrawer.closeDrawers();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Ajouter les item à l'action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem deleteItem = menu.findItem(R.id.deleteScores);
        // Afficher l'item delete uniquement si le leaderboard est affiché (isLiderboard = true)
        if (isLeaderboard) {
            deleteItem.setVisible(true);
        }
        return true;
    }

    /**
     * Défini les actions a exécuter suivant l'item de l'action bar sélectionné par l'utilisateur.
     * @param item item sélectionné par l'utilisateur
     * @return booléen
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.help:
                afficherAide();
                break;
            case R.id.deleteScores:
                _scoreDataBase.deleteAllScore();
                Class leaderboardClass = LeaderboardFragment.class;
                try {
                    this.finish();
                    final Intent intent = this.getIntent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    this.startActivity(intent);
                    getSupportFragmentManager().beginTransaction().add(R.id.main_Content, new LeaderboardFragment(), "un tag").commit();
                    Toast.makeText(getBaseContext(), "Score(s) réinitialisé(s)", Toast.LENGTH_LONG).show();

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Score(s) réinitialisé(s)", Toast.LENGTH_LONG).show();
                reinitialiserLeaderboard();
                break;
            default:
                break;
        }
        return  super.onOptionsItemSelected(item);
    }

    /**
     * Méthode permettant d'afficher la boîte de dialogue contenant l'aide
     */
    private void afficherAide() {
        FragmentManager fm = getSupportFragmentManager();
        HelpDialogFragment newFragment = new HelpDialogFragment();
        newFragment.show(fm, "Helper");
    }

    private void recupererPreferences() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        color = prefs.getString("pref_theme", "#FFA500");
        nomJoueur = prefs.getString("id_joueur", "Veuillez configurer les paramètres");
    }

    /**
     * Cette fonction permet de réinitialiser le leaderboard puis de réouvrir
     * l'activité principale avec le nouveau leaderboard vide
     */
    private void reinitialiserLeaderboard(){
        _scoreDataBase.deleteAllScore();
        try {
            this.finish();
            final Intent intent = this.getIntent();
            intent.putExtra("reinitLeaderboard", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(intent);
            Toast.makeText(getBaseContext(), "Score(s) réinitialisé(s)", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode exécutée lorsque l'utilisateur sélectionne l'image du navigation drawer header
     * Démarrage du niveau secret BTTF
     * @param v vue
     */
    public void easterEgg(View v) {
        Toast.makeText(getApplicationContext(), "Bonjour, je suis un easter egg", Toast.LENGTH_LONG).show();
        // La condition permet ici de charger le XML associé à la EasterEggCustomView (jeu BTTF)
        GameActivity.setCondition(2);
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(intent);
    }

    /**
     * Méthode éxecutée lorsque l'utilisateur sélectionne le bouton JOUER
     * Démarrage du jeu
     * @param v vue
     */
    public void jouer(View v) {
        // La condition permet ici de charger le XML associé à la GameCustomView (jeu original)
        GameActivity.setCondition(1);
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(intent);
    }

    /**
     * On change de couleur
     * @return Color couleur contenue dans la variable color
     */
    private int recupererCouleur() {
        supportInvalidateOptionsMenu();
        return Color.parseColor(color);
    }

    /**
     * Application du thème sur le navigation drawer
     * Change la couleur des items et des icons associés aux item suivant l'action
     * de l'utilisateur
     */
    private void appliquerThemeNavigationDrawer(){
        int[][] state = new int[][] {
                new int [] {android.R.attr.state_pressed},
                new int [] {android.R.attr.state_focused},
                new int [] {android.R.attr.state_checked},
                new int [] {}
        };

        int[] color = new int[] {
                recupererCouleur(),
                recupererCouleur(),
                recupererCouleur(),
                Color.DKGRAY
        };

        // On associe les état possibles du bouton avec une couleur
        ColorStateList colorStateList = new ColorStateList(state, color);
        // On défini les couleurs en fonction des états pour chaque item (texte + icone)
        nvDrawer.setItemTextColor(colorStateList);
        nvDrawer.setItemIconTintList(colorStateList);
    }
}
