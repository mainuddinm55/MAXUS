package uk.maxusint.maxus.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.fragment.AccountFragment;
import uk.maxusint.maxus.fragment.AdminHomeFragment;
import uk.maxusint.maxus.fragment.HomeFragment;
import uk.maxusint.maxus.fragment.MatchFragment;
import uk.maxusint.maxus.fragment.MoreFragment;
import uk.maxusint.maxus.fragment.UserFragment;
import uk.maxusint.maxus.network.model.User;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView navigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.bottom_home:
                            viewPager.setCurrentItem(0);
                            return true;
                        case R.id.bottom_match:
                            viewPager.setCurrentItem(1);
                            return true;
                        case R.id.bottom_more:
                            viewPager.setCurrentItem(2);
                            return true;
                        case R.id.action_more:
                            viewPager.setCurrentItem(3);
                            return true;
                    }

                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        BottomNavigationAdapter adapter = new BottomNavigationAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private static class BottomNavigationAdapter extends FragmentPagerAdapter {
        User user;

        BottomNavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new MatchFragment();
                case 2:
                    return new MoreFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
