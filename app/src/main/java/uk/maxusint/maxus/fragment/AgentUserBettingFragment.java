package uk.maxusint.maxus.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.maxusint.maxus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgentUserBettingFragment extends Fragment {
    private static AgentUserBettingFragment sInstance;

    public AgentUserBettingFragment() {
        // Required empty public constructor
    }

    public static synchronized AgentUserBettingFragment getInstance() {
        if (sInstance==null)
            sInstance = new AgentUserBettingFragment();
        return sInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agent_user_betting, container, false);
    }

}
