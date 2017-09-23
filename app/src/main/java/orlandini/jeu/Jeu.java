package orlandini.jeu;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;


/**
 * Classe permettant de gérer la configuration du jeu
 *
 * @author Nicolas Orlandini
 * @version 2016.0.44
 *
 * Date de création : 04/11/2016
 * Dernière modification : 07/09/2017
 */

public class Jeu extends View {

    protected Bitmap bitmapPerso;
    protected Bitmap bitmaptoucher;
    protected Bitmap bitmapTemps;
    protected Bitmap bitmapChat;
    protected int perso;
    protected int son;
    protected MediaPlayer mMediaPlayer;
    protected String color;
    protected boolean isInvisible = true;
    protected int prefVitesse = 25;
    protected int screenWidth;
    protected int screenHeight;

    protected static int score = 0;

    protected SharedPreferences prefs;

    protected Paint paint;

    public Jeu(Context context) {
        super(context);
        init();
    }

    public Jeu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Initialisation du pinceau pour l'affichage du texte dans les custom view
     */
    private void init() {
        paint = new Paint();
        paint.setTextSize(50);
        paint.setColor(Color.WHITE);
    }

    /**
     * Paramétrage du personnage (défini par l'utilisateur dans les préférences)
     * @param res ressources de l'application
     */
    protected void parametrerImagePerso(Resources res) {
        switch (perso) {
            case 1:
                bitmapPerso = BitmapFactory.decodeResource(res, R.drawable.bender_ghost);
                break;
            case 2:
                bitmapPerso = BitmapFactory.decodeResource(res, R.drawable.blinky_pacman);
                break;
            case 3 :
                bitmapPerso = BitmapFactory.decodeResource(res, R.drawable.space_invaders_alien);
                break;
            case 4 :
                bitmapPerso = BitmapFactory.decodeResource(res, R.drawable.roi_boo);
                break;
        }
    }

    /**
     * Paramétrage du son joué lorsque l'utilisateur touche le personnage
     * (défini par l'utilisateur dans les préférences)
     */
    protected void parametrerSonPerso() {
        switch (son) {
            case 1:
                mMediaPlayer = MediaPlayer.create(this.getContext(), R.raw.fantome);
                break;
            case 2:
                mMediaPlayer = MediaPlayer.create(this.getContext(), R.raw.yoshi);
                break;
            case 3 :
                mMediaPlayer = MediaPlayer.create(this.getContext(), R.raw.doh);
                break;
            case 4 :
                mMediaPlayer = MediaPlayer.create(this.getContext(), R.raw.nope);
                break;
            case 5 :
                mMediaPlayer = MediaPlayer.create(this.getContext(), R.raw.error_windows);
                break;
        }
    }

    /**
     * Chargement des bitmap necessaires au jeu
     * @param res ressources
     */
    protected void chargerBitmap(Resources res) {
        bitmapTemps = BitmapFactory.decodeResource(res, R.drawable.ic_timer);
        bitmapChat = BitmapFactory.decodeResource(res, R.drawable.chat);
        //si on est sur le thème halloween, on met le RIP
        if (color.equals("#EE7600"))
            bitmaptoucher = BitmapFactory.decodeResource(res, R.drawable.rip_game);
            //sinon on met l'image POW
        else if (color.equals("#3f51b5"))
            bitmaptoucher = BitmapFactory.decodeResource(res, R.drawable.pow);
    }

    /**
     * Affichage des information de jeu :
     * - Score actuel
     * - Meilleur score
     * - Temps restant
     * @param canvas canvas
     */
    protected void afficherInfosJeu(Canvas canvas) {
        String topScore = MainActivity._scoreDataBase.getTopScore();
        canvas.drawText("Score : " + String.valueOf(score), 50, 50, paint);
        canvas.drawText("Meilleur score : " + String.valueOf(topScore), 50, 120, paint);
        canvas.drawBitmap(bitmapTemps, screenWidth - 360, 0, null);
        canvas.drawText(String.valueOf(GameActivity.getSecs()) + " s", screenWidth - 150, 110, paint);
    }

    /**
     * Récupération des préférences utilisateur
     */
    protected void recupererPreferences(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        color = prefs.getString("pref_theme", "#FFA500");
        perso = Integer.parseInt(prefs.getString("pref_perso", "1"));
        son = Integer.parseInt(prefs.getString("pref_son", "1"));
        prefVitesse = prefs.getInt("seekbar_vitesse", 25);
    }

    /**
     * Changement de la visibilité des éléments de jeu
     * @param visibility entier 0 ou 1
     */
    @Override
    public void setVisibility(int visibility) {
        isInvisible = visibility == View.INVISIBLE;
    }

    /**
     * Récupération de la largeur et hauteur de l'activité
     * @param changed ?
     * @param left délimitation gauche
     * @param top délimitation haute
     * @param right délimitation droit
     * @param bottom délimitation bas
     */
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        screenWidth = getWidth();
        screenHeight = getHeight();
    }
}
