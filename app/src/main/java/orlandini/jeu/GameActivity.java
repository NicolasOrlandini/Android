package orlandini.jeu;


import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import io.realm.Realm;
import orlandini.jeu.Fragments.FatalityDialogFragment;
import orlandini.jeu.Models.Score;

/**
 * Cette activité gère l'affichage de la durée d'une partie,
 * contient la custom view du jeu,
 * initialise l'action bar
 * Gère les actions liées à l'action bar.
 *
 * @author Nicolas Orlandini & Valentin Leon
 * @version 2016.0.46
 *
 * Date de création : 09/10/2016
 * Dernière modification : 04/11/2016
 */

public class GameActivity extends AppCompatActivity{

    private Button StartButton;
    private Handler customHandler = new Handler();
    private ScoreDataBase scoreDB;
    private MyCount counter = null;
    private MediaPlayer mMediaPlayerTheme;
    private Menu menu;

    private boolean recommencer = false;
    private String temps = null;
    private String color;
    private long s1 = 0;
    private boolean isPlaying = true;

    public static boolean getPaused() {
        return isPaused;
    }
    private static boolean isPaused = false;

    public static boolean isGame() {
        return isGame;
    }
    private static boolean isGame = false;

    public static int getSecs() {
        return secs;
    }
    private static int secs = 0;

    public static void setCondition(int condition) {
        GameActivity.condition = condition;
    }
    private static int condition = 1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Chargement d'un xml différent si il s'agit du niveau caché ou du jeu original
        switch (condition) {
            case 1:
                setContentView(R.layout.activity_game);
                break;
            case 2:
                setContentView(R.layout.activity_easter_egg);
                break;
        }

        //chargement des préférences
        recupererPreferences();

        // Configuration de l'actionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupActionBar();

        scoreDB = new ScoreDataBase(getApplicationContext());

        // Préparation du mediaPlayer pour la musique d'ambiance
        mMediaPlayerTheme = MediaPlayer.create(this.getApplicationContext(), R.raw.main);

        // Récupération du bouton
        StartButton = (Button) findViewById(R.id.startButton);

        configurerRippleEffect();

        StartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //Réinitialisation du score
                switch (condition) {
                    case 1:
                        GameCustomView.setScore(0);
                        break;
                    case 2:
                         EasterEggCustomView.setScore(0);
                        break;
                }

                // On cache le bouton "Commencer la partie
                StartButton.setVisibility(View.INVISIBLE);

                // Préparation du compte à rebours (temps de jeu)
                counter = new MyCount((Integer.parseInt(temps)+1)*1000, 1000);

                // Le jeu démarre
                isGame = true;
                customHandler.postDelayed(updateTimerThread, 0);
                if (!mMediaPlayerTheme.isPlaying())
                    mMediaPlayerTheme.start();
                // Démarrage du compte à rebours
                counter.start();
            }
        });
    }

    /**
     * Runnable permettant de gérer la partie
     */
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            // L'utilisateur souhaite recomencer la patie
            if (recommencer) {
                reinitialiserJeu();
            }
            // Le jeu est en cours
            else {
                customHandler.postDelayed(updateTimerThread, 0);
            }
        }
    };

    /**
     * Méthode exécutée lorsque l'utilisateur appuie sur le bouton retour de l'appareil.
     */
    @Override
    public void onBackPressed()
    {
        reinitialiserJeu();
        // Retour à l'activité précédente
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            reinitialiserJeu();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * Association du menu et du xml (partie graphique).
     * @param menu action bar
     * @return booléen
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_jeu, menu);
        this.menu = menu;
        return true;
    }

    /**
     * Définit les actions a exécuter suivant l'item de l'action bar sélectionné par l'utilisateur.
     * @param item item sélectionné par l'utilisateur
     * @return booléen
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Lorsque l'utilisateur sélectionne la flèche a gauche de l'actionBar, le jeu se réinitialise
                reinitialiserJeu();
                // Retour à l'activité précédente
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.new_game:
                if (isGame)
                    recommencer = true;
                // Si le jeu est en cours et qu'il est en pause, on le redémarre avant du recommencer la partie
                if (isGame && isPaused) {
                    MenuItem pauseMenuItem = menu.findItem(R.id.pause);
                    pause(pauseMenuItem);
                }

                // L'utilisateur est averti qu'une nouvelle partie commence
                Toast.makeText(this, "Nouvelle partie", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pause:
                // Gestion du menu pause
                // Le jeu ne peut être mis en pause que si il est démarré (isGame = true)
                if (isGame)
                    pause(item);
                break;
            case R.id.musique:
                if (isGame  && !isPaused) {
                    // Arrêt ou redémarrage de la musique
                    gererMusique(item);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Gestion de la musique d'ambiance pour le bouton musique de l'action bar
     * @param item item sélectionné par l'utilisateur
     */
    private void gererMusique(MenuItem item)  {
        if (isPlaying) {
            isPlaying = false;
            gererMediaPlayerTheme();
            item.setIcon(R.drawable.ic_volume_off);
        }
        else {
            isPlaying = true;
            gererMediaPlayerTheme();
            item.setIcon(R.drawable.ic_volume);
        }
    }

    /**
     * Gestion du menu pause/play avec arrêt et redémarrage du rennable
     *
     * @param item item sélectionné
     */
    private void pause(MenuItem item) {
        if (isPaused) {
            isPaused = false;
            // On redémarre le runnable
            customHandler.postDelayed(updateTimerThread, 0);
            // On créer un nouveau timer qui commence au temps ou l'ancien timer a été arrêté.
            counter= new MyCount(s1, 1000);
            counter.start();

            // On redémarre la musique d'ambiance
            if (isPlaying) {
                gererMediaPlayerTheme();
            }


            // On change l'icon correspondant à l'item
            item.setIcon(R.drawable.ic_pause);
        }
        else {
            isPaused = true;
            // On stop le runnable
            customHandler.removeCallbacks(updateTimerThread);
            // On arrête le timer
            counter.cancel();
            // On met la musique d'ambiance en pause
            if (isPlaying) {
                gererMediaPlayerTheme();
            }

            // On change l'icon correspondant à l'item
            item.setIcon(R.drawable.ic_play);
        }
    }

    /**
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

    /**
     * Récupération des préférences utilisateur
     */
    private void recupererPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        color = prefs.getString("pref_theme", "#FFA500");
        temps = prefs.getString("pref_temps_jeu", "30");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configurerRippleEffect() {
        RippleDrawable rippleDrawable = (RippleDrawable)StartButton.getBackground();

        int[][] states = new int[][] {
                new int[]{android.R.attr.state_pressed}
        };

        int[] colors = new int[] {
                recupererCouleur()
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);
        rippleDrawable.setColor(colorStateList);
        rippleDrawable.setColorFilter(recupererCouleur(), PorterDuff.Mode.MULTIPLY);
    }

    /**
     * Fcnction permettant de récupérer le thème actuel défini dans les paramètres (code couleur hexadecimal)
     * @return Entier correspondant au code couleur hexadéciaml
     */
    private int recupererCouleur() {
        return Color.parseColor(color);
    }

    /**
     * Mise en play ou pause du media player suivant son état actuel
     */
    private void gererMediaPlayerTheme() {
        if (!mMediaPlayerTheme.isPlaying())
            mMediaPlayerTheme.start();
        else
            mMediaPlayerTheme.pause();
    }

    /**
     * Méthode de réinitialisation du jeu
     */
    public void reinitialiserJeu(){
        isPaused = false;
        recommencer = false;
        isGame = false;
        isPlaying = false;

        // Remise en marche de la musique avant arrêt
        MenuItem musiqueItem = menu.findItem(R.id.musique);
        gererMusique(musiqueItem);

        // Arrêt de la musique d'ambiance
        if (mMediaPlayerTheme.isPlaying()) {
            mMediaPlayerTheme.pause();
            mMediaPlayerTheme.seekTo(0);
        }

        // Arrêt du compte à rebours
        if (counter != null)
            counter.cancel();

        secs = Integer.parseInt(temps);

        // Arrêt du thread timer
        customHandler.removeCallbacks(updateTimerThread);

        // Affichage du bouton "Commencer la partie"
        StartButton.setVisibility(View.VISIBLE);
    }

    /**
     * Classe permettant du gérer le timer
     * Compte à rebours pendant le jeu
     */
    public class MyCount extends CountDownTimer {

        Score monScore = new Score();
        int id = 1;

        private MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        // Executé lorsque le temps de jeu est écoulé (timer à zero)
        @Override
        public void onFinish() {
            MainActivity._realm.beginTransaction();
            //Ajouter le score dans la base de données
            switch (condition) {
                case 1:
                    //scoreDB.addScore(GameCustomView.getScore());
                    Score realmScore = MainActivity._realm.createObject(Score.class, monScore.getId());
                    realmScore.setScore(GameCustomView.getScore());
                    MainActivity._realm.copyToRealmOrUpdate(realmScore);
                    MainActivity._realm.commitTransaction();
                    id++;
                    break;
                case 2:
                    scoreDB.addScore(EasterEggCustomView.getScore());
                    break;
            }

            // Affichage d'un dialogBox pour donner le score
            FragmentManager fm = getSupportFragmentManager();
            FatalityDialogFragment newFragment = new FatalityDialogFragment();
            newFragment.show(fm, "Fragment_fatality_dialog");

            MainActivity._realm.close();
            // Réinitialisation du jeu
            reinitialiserJeu();
        }

        // Executé lors de la mise en pause du timer
        @Override
        public void onTick(long millisUntilFinished) {
            s1 = millisUntilFinished;
            long mill = millisUntilFinished / 1000;
            secs = (int) mill - 1;
        }
    }
}
