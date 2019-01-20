package uk.maxusint.maxus.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.ViewPagerAdapter;
import uk.maxusint.maxus.fragment.BonusFragment;
import uk.maxusint.maxus.fragment.RankFragment;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

public class BonusActivity extends AppCompatActivity {
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setupWithViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupWithViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BonusFragment(), "BONUS");
        adapter.addFragment(new RankFragment(), "RANKING");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rank_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_rank);
        User user = new SharedPref(this).getUser();
        if (user != null)
            menuItem.setTitle(getRankTitle(user.getRankId()));
        return super.onCreateOptionsMenu(menu);
    }

    private String getRankTitle(int rankId) {
        String rank = null;
        switch (rankId) {
            case User.UserRank.GENERAL_MEMBER:
                rank = "General Member";
                break;
            case User.UserRank.ASSOCIATE:
                rank = "Associate";
                break;
            case User.UserRank.SR_ASSOCIATE:
                rank = "Sr. Associate";
                break;
            case User.UserRank.BRONZE:
                rank = "Bronze";
                break;
            case User.UserRank.SILVER:
                rank = "Silver";
                break;
            case User.UserRank.GOLD:
                rank = "Gold";
                break;
            case User.UserRank.P_D:
                rank = "P.D";
                break;
            case User.UserRank.A_M:
                rank = "A.M";
                break;
        }
        return rank;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_rank) {
            viewPager.setCurrentItem(2);
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }
}
