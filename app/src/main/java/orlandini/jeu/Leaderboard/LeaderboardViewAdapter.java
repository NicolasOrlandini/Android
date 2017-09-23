package orlandini.jeu.Leaderboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import orlandini.jeu.R;

/**
 * @author Valentin Leon
 * @version 2016.0.11
 *
 * Date de création : 29/10/2016
 * Dernière modification : 23/09/2017
 */

public class LeaderboardViewAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {

    //variables
    private final Context mCtx; //get le context
    private Integer[] tableaudemerde;

    public LeaderboardViewAdapter(Context context, Integer[] unBonGrosTableauDeMerde)
    {
        this.tableaudemerde = unBonGrosTableauDeMerde;
        this.mCtx = context;
    }

    @Override
    public LeaderboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LeaderboardViewHolder holder, int position) {
        //ajout de la police
        Typeface typeface = Typeface.createFromAsset(mCtx.getAssets(), "BTTF.ttf");
        //set les margin des paramètres
        //params.setMargins(0, 20, 0, 0);
        //holder.getmCardView().setAlpha(0.6f);

        holder.getmTextViewView().setTextSize(18);
        //suivant la position
        if(position==0) {
            holder.getmCardView().setCardBackgroundColor(Color.parseColor("#ffd700"));
            holder.getmImageView().setImageResource(R.drawable.ic_or);
            holder.getmTextViewView().setTypeface(typeface);
            if(tableaudemerde[0] == null){
                holder.getmTextViewView().setText(R.string.iftextnull);
            }else{
                holder.getmTextViewView().setText("Score : " + String.valueOf(tableaudemerde[position]));
            }
        }
        else if(position==1) {
            holder.getmCardView().setCardBackgroundColor(Color.parseColor("#CECECE"));
            holder.getmImageView().setImageResource(R.drawable.ic_argent);
            holder.getmTextViewView().setTypeface(typeface);
            if(tableaudemerde[1] == null){
                holder.getmTextViewView().setText(R.string.iftextnull);
            }
            else{
                holder.getmTextViewView().setText("Score : " + String.valueOf(tableaudemerde[position]));
            }

        }
        else if(position==2) {
            holder.getmCardView().setCardBackgroundColor(Color.parseColor("#cd7f32"));
            holder.getmImageView().setImageResource(R.drawable.ic_bronze);
            holder.getmTextViewView().setTypeface(typeface);
            if(tableaudemerde[2] == null){
                holder.getmTextViewView().setText(R.string.iftextnull);
            }
            else{
                holder.getmTextViewView().setText("Score : " + String.valueOf(tableaudemerde[position]));
            }
        }
        else if(tableaudemerde[position] == null) {
            holder.getmTextViewView().setTypeface(typeface);
            holder.getmTextViewView().setText(">");
            holder.getmTextViewView().setTextSize(30);
            holder.getmImageView().setImageResource(R.drawable.delorean1);
        }
        else {
            holder.getmTextViewView().setTextSize(20);
            holder.getmTextViewView().setTypeface(typeface);
            holder.getmTextViewView().setText("Score : " + String.valueOf(tableaudemerde[position]));
            holder.getmImageView().setImageResource(R.drawable.ic_trophee);
        }
    }

    /**
     * On récupère le nombre d'item
     * @return longueur du tableau
     */
    @Override
    public int getItemCount() {
        return tableaudemerde.length;
    }
}
