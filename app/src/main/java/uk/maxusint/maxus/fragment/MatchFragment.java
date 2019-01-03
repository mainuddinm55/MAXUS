package uk.maxusint.maxus.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.ViewPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchFragment extends Fragment {
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    public MatchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setupWithViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupWithViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(RunningMatchFragment.getInstance(), "RUNNING");
        adapter.addFragment(UpcomingMatchFragment.getInstance(), "UPCOMING");
        adapter.addFragment(FinishMatchFragment.getInstance(), "FINISH");
        viewPager.setAdapter(adapter);
    }
}
