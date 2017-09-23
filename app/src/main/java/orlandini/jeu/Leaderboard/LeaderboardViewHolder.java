package orlandini.jeu.Leaderboard;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import orlandini.jeu.R;

/**
 * @author Valentin Leon
 * @version 2016.0.11
 *
 * Date de création : 29/10/2016
 * Dernière modification : 23/09/2017
 */

public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

    private CardView mCardView;
    private TextView mTextViewView;
    private ImageView mImageView;

    public LeaderboardViewHolder(View itemView) {
        super(itemView);

        mTextViewView =  itemView.findViewById(R.id.myscore);
        mImageView = itemView.findViewById(R.id.picture);
        mCardView = itemView.findViewById(R.id.card_view);
    }

    public CardView getmCardView() {
        return mCardView;
    }

    public void setmCardView(CardView mCardView) {
        this.mCardView = mCardView;
    }

    public TextView getmTextViewView() {
        return mTextViewView;
    }

    public void setmTextViewView(TextView mTextViewView) {
        this.mTextViewView = mTextViewView;
    }

    public ImageView getmImageView() {
        return mImageView;
    }

    public void setmImageView(ImageView mImageView) {
        this.mImageView = mImageView;
    }

}
