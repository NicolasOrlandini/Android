package orlandini.jeu.Fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;

import orlandini.jeu.GameCustomView;
import orlandini.jeu.R;

/**
 * Dialg affiché lorsqu'une partie est terminée
 * L'utilisateur peut alors revenir au menu principal ou rejouer.
 *
 * @author Nicolas Orlandini
 * @version 2016.0.39
 *
 * Date de création : 09/10/2016
 * Dernière modification : 07/09/2017
 */

public class FatalityDialogFragment extends DialogFragment {

    private MediaPlayer mMediaPlayerRejouer;
    private MediaPlayer mMediaPlayerFatality;
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        mMediaPlayerFatality = MediaPlayer.create(this.getContext(), R.raw.fatality);
        mMediaPlayerFatality.start();
        mMediaPlayerRejouer = MediaPlayer.create(this.getContext(), R.raw.excellent);

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(R.string.dialog_fatality);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mMediaPlayerRejouer.start();
                    }
                })
                //TODO: voir pourquoi ça kill l'application
                /*.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })*/
        .setMessage("Votre score : " + GameCustomView.getScore());
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public FatalityDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fatality_dialog, container, false);
    }
}
