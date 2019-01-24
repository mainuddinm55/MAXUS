package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.ViewPagerAdapter;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchFragment extends Fragment {
    private static final String TAG = "MatchFragment";
    private static MatchFragment sInstance;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private Context mContext;

    public MatchFragment() {
        // Required empty public constructor
    }

    public static synchronized MatchFragment getInstance() {
        if (sInstance == null)
            sInstance = new MatchFragment();
        return sInstance;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        Log.e(TAG, "onViewCreated: ");
        SharedPref sharedPref = new SharedPref(mContext);
        User user = sharedPref.getUser();
        setupWithViewPager(viewPager, user);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupWithViewPager(ViewPager viewPager, User user) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        if (user!=null && user.getTypeId() == User.UserType.ADMIN){
            adapter.addFragment(RunningMatchFragment.getInstance(), "RUNNING");
            adapter.addFragment(UpcomingMatchFragment.getInstance(), "UPCOMING");
            adapter.addFragment(FinishMatchFragment.getInstance(), "FINISH");
        }else {
            adapter.addFragment(RunningMatchFragment.getInstance(), "RUNNING");
            adapter.addFragment(UpcomingMatchFragment.getInstance(), "UPCOMING");
        }
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }
}
