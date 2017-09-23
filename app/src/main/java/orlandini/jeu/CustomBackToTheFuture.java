package orlandini.jeu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Singu_Admin on 29/10/2016.
 */

/**
 * Classe de customisation de font
 */

public class CustomBackToTheFuture extends android.support.v7.widget.AppCompatTextView {
    public CustomBackToTheFuture(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(isInEditMode()){
            return;
        }

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomBackToTheFuture);
        String fontName = "CustomBackToTheFuture";
        styledAttrs.recycle();

        setTypeFace(fontName);
    }

    //on crée et on set la font
    public void setTypeFace(String fontName){
        if(fontName != null){
            try {
                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), fontName);
                setTypeface(typeface);
            }
            catch (Exception e){
                Log.e("FONT", fontName + " not found", e);
            }
        }
    }
}
