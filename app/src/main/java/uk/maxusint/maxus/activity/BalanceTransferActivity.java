package uk.maxusint.maxus.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.ViewPagerAdapter;
import uk.maxusint.maxus.fragment.DepositHistoryFragment;
import uk.maxusint.maxus.fragment.DepositRequestFragment;
import uk.maxusint.maxus.utils.Transaction;

public class BalanceTransferActivity extends AppCompatActivity {
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_transfer);
        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        DepositRequestFragment requestFragment = new DepositRequestFragment();
        DepositHistoryFragment historyFragment = new DepositHistoryFragment();
        Bundle requestBundle = new Bundle();
        Bundle historyBundle = new Bundle();
        historyBundle.putInt(DepositHistoryFragment.ACTION, Transaction.Type.BALANCE_TRANSFER_HISTORY);
        requestBundle.putInt(DepositRequestFragment.ACTION, Transaction.Type.BALANCE_TRANSFER_REQUEST);
        requestFragment.setArguments(requestBundle);
        historyFragment.setArguments(historyBundle);
        adapter.addFragment(historyFragment, "HISTORY");
        adapter.addFragment(requestFragment, "REQUEST");
        viewPager.setAdapter(adapter);
    }
}
