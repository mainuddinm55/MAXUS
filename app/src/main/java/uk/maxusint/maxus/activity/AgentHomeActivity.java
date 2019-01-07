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
import uk.maxusint.maxus.fragment.AddMatchFragment;
import uk.maxusint.maxus.fragment.AgentProfileFragment;
import uk.maxusint.maxus.fragment.AgentUserBettingFragment;
import uk.maxusint.maxus.fragment.AgentUsersFragment;

public class AgentHomeActivity extends AppCompatActivity {
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_betting:
                            viewPager.setCurrentItem(0);
                            return true;
                        case R.id.action_user:
                            viewPager.setCurrentItem(1);
                            return true;
                        case R.id.action_profile:
                            viewPager.setCurrentItem(2);
                            return true;
                    }

                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_home);
        ButterKnife.bind(this);

        BottomNavigationAdapter adapter = new BottomNavigationAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private static class BottomNavigationAdapter extends FragmentPagerAdapter {


        public BottomNavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return AgentUserBettingFragment.getInstance();
                case 1:
                    return AgentUsersFragment.getInstance();
                case 2:
                    return AgentProfileFragment.getInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
