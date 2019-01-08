package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        SharedPref sharedPref = new SharedPref(this);
        User user = sharedPref.getUser();
        if (user != null) {
            nameTextView.setText(user.getName());
            String balance = String.valueOf(user.getTotalBalance()) + " $";
            balanceTextView.setText(balance);
        }
    }

    @OnClick(R.id.user_request_text_view)
    void goToIncomingRequest() {
        Intent intent = new Intent(this, IncomingRequestActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
