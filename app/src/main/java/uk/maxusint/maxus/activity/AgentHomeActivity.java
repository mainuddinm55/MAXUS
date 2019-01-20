package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.fragment.AddMatchFragment;
import uk.maxusint.maxus.fragment.AgentProfileFragment;
import uk.maxusint.maxus.fragment.AgentUserBettingFragment;
import uk.maxusint.maxus.fragment.AgentUsersFragment;
import uk.maxusint.maxus.fragment.AllUserFragment;
import uk.maxusint.maxus.fragment.MoreFragment;
import uk.maxusint.maxus.fragment.ProfileFragment;
import uk.maxusint.maxus.fragment.UserFragment;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

public class AgentHomeActivity extends AppCompatActivity {
    public static final String USER = "uk.maxusint.maxus.activity.USER";
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.bottom_betting:
                            viewPager.setCurrentItem(0);
                            return true;
                        case R.id.bottom_user:
                            viewPager.setCurrentItem(1);
                            return true;
                        case R.id.bottom_profile:
                            viewPager.setCurrentItem(2);
                            return true;
                        case R.id.bottom_more:
                            viewPager.setCurrentItem(3);
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


        BottomNavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            AgentUserBettingFragment userBettingFragment = AgentUserBettingFragment.getInstance();
            AgentUsersFragment usersFragment = AgentUsersFragment.getInstance();
            AgentProfileFragment profileFragment = AgentProfileFragment.getInstance();
            Bundle typeBundle = new Bundle();
            typeBundle.putInt(AgentHomeActivity.USER, User.UserType.AGENT);
            userBettingFragment.setArguments(typeBundle);
            usersFragment.setArguments(typeBundle);
            profileFragment.setArguments(typeBundle);
            switch (position) {
                case 0:
                    return userBettingFragment;
                case 1:
                    return new AllUserFragment();
                case 2:
                    return new ProfileFragment();
                case 3:
                    return new MoreFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
