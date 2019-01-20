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
import uk.maxusint.maxus.fragment.AccountFragment;
import uk.maxusint.maxus.fragment.AdminHomeFragment;
import uk.maxusint.maxus.fragment.AllPremiumFragment;
import uk.maxusint.maxus.fragment.MatchFragment;
import uk.maxusint.maxus.fragment.MoreFragment;
import uk.maxusint.maxus.fragment.ProfileFragment;
import uk.maxusint.maxus.fragment.UserFragment;
import uk.maxusint.maxus.utils.SharedPref;

public class PremiumUserActivity extends AppCompatActivity {
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;
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
        setContentView(R.layout.activity_premium_user);
        ButterKnife.bind(this);

        BottomNavigationAdapter navigationAdapter = new BottomNavigationAdapter(getSupportFragmentManager());
        viewPager.setAdapter(navigationAdapter);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
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
            switch (position) {
                case 0:
                    return new AllPremiumFragment();
                case 1:
                    return new MatchFragment();
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
