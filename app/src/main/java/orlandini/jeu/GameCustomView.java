package orlandini.jeu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

/**
 * Cette vue personnalisée permet d'afficher le jeu et gère les
 * interactions ainsi que l'affichage du timer et du score
 *
 * @author Nicolas Orlandini
 * @version 2016.0.44
 *
 * Date de création : 09/10/2016
 * Dernière modification : 07/09/2017
 */

public class GameCustomView extends Jeu implements View.OnTouchListener {

    private final int IMAGE_SIZE = 180;

    private float mFileX;
    private float mFileY;
    boolean isInvisible = true;
    private Vibrator vibrator;

    public static int getScore() {
        return score;
    }

    public static void setScore(int score) {
        GameCustomView.score = score;
    }

    private SpriteAnimation spriteAnimation;

    private Runnable animator = new Runnable() {
        @Override
        public void run() {
            update();
            invalidate();
            int tempsAttente;
            if (prefVitesse != 0)
                tempsAttente = 100000/prefVitesse;
            else
                tempsAttente = 6000;
            postDelayed(this, tempsAttente);
        }
    };

    //initialisation du jeu
    public void init () {
        Resources res = getResources();

        recupererPreferences(this.getContext());

        parametrerSonPerso();
        chargerBitmap(res);
        parametrerImagePerso(res);

        vibrator = (Vibrator) this.getContext().getSystemService(Activity.VIBRATOR_SERVICE);
        mFileX = 500;
        mFileY = 500;

        super.setOnTouchListener(this);
        removeCallbacks(animator);
        post(animator);

        /*spriteAnimation = new SpriteAnimation(
                bitmapChat
                , 0, 120	// initial position
                , 5, 13);	// FPS and number of frames in the animation*/
    }

    public GameCustomView(Context context) {
        super(context);
        init();
    }

    public GameCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        afficherInfosJeu(canvas);

        if (GameActivity.isGame()) {
            //spriteAnimation.draw(canvas);

            if (isInvisible)
                canvas.drawBitmap(bitmapPerso, mFileX, mFileY, null);
            else {
                canvas.drawBitmap(bitmaptoucher, mFileX, mFileY, null);
            }
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!GameActivity.getPaused()) {
                    if ( x >= mFileX && x <= mFileX + IMAGE_SIZE && y >= mFileY
                            && y <= mFileY + IMAGE_SIZE) {

                        if (prefs.getBoolean("switch_sons", true)) {
                            if (mMediaPlayer != null) {
                                mMediaPlayer.release();
                                mMediaPlayer = null;
                            }
                            parametrerSonPerso();
                            mMediaPlayer.start();
                        }

                        if (prefs.getBoolean("switch_vibreur", true))
                            vibrator.vibrate(100);
                        setVisibility(View.VISIBLE);
                        score++;
                        update();
                        invalidate();
                    }
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                //setVisibility(View.INVISIBLE);
                return true;
            case MotionEvent.ACTION_UP:
                setVisibility(View.INVISIBLE);
            default:
                return false;
        }
    }

    //déplacement du personnage
    public void update() {
        if (!GameActivity.getPaused() && GameActivity.isGame()){
            Random randomValue = new Random();

            mFileX = (float)randomValue.nextInt(screenWidth - IMAGE_SIZE);
            mFileY = (float)randomValue.nextInt(screenHeight - IMAGE_SIZE);

            //spriteAnimation.update();
        }
    }
}
