package uk.maxusint.maxus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.model.User;

public class UpdateUserActivity extends AppCompatActivity {
    public static final String USER = "uk.maxusint.maxus.activity.USER";
    public static final String TAG = "UpdateUserActivity";
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.username_text_view)
    TextView usernameTextView;
    @BindView(R.id.email_text_view)
    TextView emailTextView;
    @BindView(R.id.mobile_text_view)
    TextView mobileTextView;
    @BindView(R.id.reference_text_view)
    TextView referenceTextView;
    @BindView(R.id.agent_text_view)
    TextView agentTextView;
    @BindView(R.id.district_text_view)
    TextView districtTextView;
    @BindView(R.id.balance_text_view)
    TextView balanceTextView;
    @BindView(R.id.rank_text_view)
    TextView rankTextView;
    @BindView(R.id.active_inactive_btn)
    Button activeInactiveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        ButterKnife.bind(this);
        Bundle userBundle = getIntent().getExtras();
        if (userBundle != null) {
            User user = userBundle.getParcelable(USER);
            if (user != null) {
                nameTextView.setText("Name: " + user.getName());
                usernameTextView.setText("Username: " + user.getUsername());
                emailTextView.setText("Email: " + user.getEmail());
                mobileTextView.setText("Mobile: " + user.getMobile());
                referenceTextView.setText("Reference: " + user.getReference());
                agentTextView.setText("Agent ID: " + String.valueOf(user.getAgentId()));
                districtTextView.setText("District: " + user.getDistrict());
                balanceTextView.setText("Balance: " + String.valueOf(user.getTotalBalance()));
                rankTextView.setText("Rank: " + String.valueOf(user.getRankId()));
                if (user.getStatus().equals("Active")) {
                    activeInactiveBtn.setText("Deactivate");
                } else {
                    activeInactiveBtn.setText("Activate");
                }
            }
        }
    }
}
