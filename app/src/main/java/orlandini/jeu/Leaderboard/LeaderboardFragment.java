package orlandini.jeu.Leaderboard;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import orlandini.jeu.MainActivity;
import orlandini.jeu.Models.Score;
import orlandini.jeu.R;

import orlandini.jeu.ScoreDataBase;

/**
 * @author Valentin Leon
 * @version 2016.0.10
 *
 * Date de création : 29/10/2016
 * Dernière modification : 03/11/2016
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment {

    //private ScoreDataBase db = MainActivity._scoreDataBase;

    public LeaderboardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LeaderboardViewAdapter adapter = new LeaderboardViewAdapter(getContext(), addInDB());
        mRecyclerView.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);


        /*if(db.getFiveBestScores().length > 0){
            RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            LeaderboardViewAdapter adapter = new LeaderboardViewAdapter(getContext(), db.getFiveBestScores());
            mRecyclerView.setAdapter(adapter);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(llm);
        }*/


        return rootView;
    }

    public Integer[] addInDB(){
        Integer[] maListe = new Integer[10];
        RealmResults<Score> result = MainActivity._realm.where(Score.class)
                .findAll();

        return maListe;
    }

}