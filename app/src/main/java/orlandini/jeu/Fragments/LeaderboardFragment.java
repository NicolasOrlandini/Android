package orlandini.jeu.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import orlandini.jeu.LeaderboardViewAdapter;
import orlandini.jeu.MainActivity;
import orlandini.jeu.R;

import orlandini.jeu.ScoreDataBase;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment {

    private ScoreDataBase db = MainActivity._scoreDataBase;

    public LeaderboardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);


        if(db.getFiveBestScores().length > 0){
            RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            LeaderboardViewAdapter adapter = new LeaderboardViewAdapter(getContext(), db.getFiveBestScores());
            mRecyclerView.setAdapter(adapter);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(llm);
        }


        return rootView;
    }

}