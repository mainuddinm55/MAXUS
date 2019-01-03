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
import uk.maxusint.maxus.network.model.Bet;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminHomeFragment extends Fragment {
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;


    public AdminHomeFragment() {
        // Required empty public constructor
    }

    public static synchronized AdminHomeFragment getInstance() {
        return new AdminHomeFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setupWithViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupWithViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        TradeBetsFragment tradeBetsFragment = new TradeBetsFragment();
        AllPremiumFragment premiumFragment = new AllPremiumFragment();
        Bundle tradeBundle = new Bundle();
        Bundle premiumBundle = new Bundle();
        tradeBundle.putInt(TradeBetsFragment.BET_MODE, Bet.BetMode.TRADE);
        premiumBundle.putInt(AllPremiumFragment.BET_MODE, Bet.BetMode.ADVANCED);
        tradeBetsFragment.setArguments(tradeBundle);
        premiumFragment.setArguments(premiumBundle);
        adapter.addFragment(tradeBetsFragment, "TRADE");
        adapter.addFragment(premiumFragment, "ADVANCED");
        viewPager.setAdapter(adapter);
    }
}
