package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.fragment.AccountFragment;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

public class AccountActivity extends AppCompatActivity {
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.balance_text_view)
    TextView balanceTextView;
    @BindView(R.id.deposit_text_view)
    TextView depositTextView;
    @BindView(R.id.withdraw_text_view)
    TextView withdrawTextView;
    @BindView(R.id.balance_transfer_text_view)
    TextView balanceTransferTextView;
    @BindView(R.id.bonus_text_view)
    TextView bonusTextView;
    @BindView(R.id.user_request_text_view)
    TextView incomingRequestTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPref sharedPref = new SharedPref(this);
        User user = sharedPref.getUser();
        if (user != null) {
            if (user.getTypeId() == User.UserType.AGENT) {
                balanceTransferTextView.setVisibility(View.GONE);
            }
            nameTextView.setText(user.getName());
            String balance = String.valueOf(user.getTotalBalance()) + " $";
            balanceTextView.setText(balance);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.user_request_text_view)
    void goToIncomingRequest() {
        Intent intent = new Intent(this, IncomingRequestActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick(R.id.deposit_text_view)
    void depositRequest() {
        Intent intent = new Intent(this, DepositActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick(R.id.withdraw_text_view)
    void withdrawRequest() {
        Intent intent = new Intent(this, WithdrawActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick(R.id.balance_transfer_text_view)
    void balanceTransfer() {
        Intent intent = new Intent(this, BalanceTransferActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
