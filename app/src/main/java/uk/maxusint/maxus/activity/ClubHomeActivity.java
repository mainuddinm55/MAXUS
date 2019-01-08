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
import uk.maxusint.maxus.fragment.AgentProfileFragment;
import uk.maxusint.maxus.fragment.AgentUserBettingFragment;
import uk.maxusint.maxus.fragment.AgentUsersFragment;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

public class ClubHomeActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_club_home);
        ButterKnife.bind(this);

        BottomNavigationAdapter adapter = new BottomNavigationAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                SharedPref sharedPref = new SharedPref(this);
                sharedPref.clearUser();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
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
            typeBundle.putInt(AgentHomeActivity.USER, User.UserType.CLUB);
            userBettingFragment.setArguments(typeBundle);
            usersFragment.setArguments(typeBundle);
            profileFragment.setArguments(typeBundle);
            switch (position) {
                case 0:
                    return userBettingFragment;
                case 1:
                    return usersFragment;
                case 2:
                    return profileFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
