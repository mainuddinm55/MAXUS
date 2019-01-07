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
public class AgentProfileFragment extends Fragment {
    private static AgentProfileFragment sInstance;

    public AgentProfileFragment() {
        // Required empty public constructor
    }

    public static synchronized AgentProfileFragment getInstance() {
        if (sInstance == null)
            sInstance = new AgentProfileFragment();
        return sInstance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agent_profile, container, false);
    }

}
