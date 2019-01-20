package uk.maxusint.maxus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.SharedUser;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

public class UpdateUserActivity extends AppCompatActivity {
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    public static final String SHARED_USER = "uk.maxusint.maxus.activity.SHARED_USER";
    public static final String USER = "uk.maxusint.maxus.activity.USER";
    public static final String TAG = "UpdateUserActivity";
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.name_layout)
    LinearLayout nameLayout;
    @BindView(R.id.username_text_view)
    TextView usernameTextView;
    @BindView(R.id.username_layout)
    LinearLayout usernameLayout;
    @BindView(R.id.email_text_view)
    TextView emailTextView;
    @BindView(R.id.email_layout)
    LinearLayout emailLayout;
    @BindView(R.id.mobile_text_view)
    TextView mobileTextView;
    @BindView(R.id.mobile_layout)
    LinearLayout mobileLayout;
    @BindView(R.id.reference_text_view)
    TextView referenceTextView;
    @BindView(R.id.reference_layout)
    LinearLayout referenceLayout;
    @BindView(R.id.agent_text_view)
    TextView agentTextView;
    @BindView(R.id.agent_layout)
    LinearLayout agentLayout;
    @BindView(R.id.district_text_view)
    TextView districtTextView;
    @BindView(R.id.district_layout)
    LinearLayout districtLayout;
    @BindView(R.id.balance_text_view)
    TextView balanceTextView;
    @BindView(R.id.balance_layout)
    LinearLayout balanceLayout;
    @BindView(R.id.rank_text_text_view)
    TextView rankTextTextView;
    @BindView(R.id.rank_text_view)
    TextView rankTextView;
    @BindView(R.id.rank_layout)
    LinearLayout rankLayout;
    @BindView(R.id.active_inactive_btn)
    Button activeInactiveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        apiService = ApiClient.getInstance().getApi();
        User currentUser = new SharedPref(this).getUser();
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            activeInactiveBtn.setVisibility(View.VISIBLE);
        }
        Bundle userBundle = getIntent().getExtras();
        if (userBundle != null) {
            User user = userBundle.getParcelable(USER);
            SharedUser sharedUser = (SharedUser) userBundle.getSerializable(SHARED_USER);
            if (user != null) {
                if ((currentUser != null && currentUser.getTypeId() != User.UserType.ADMIN) &&
                        (user.getTypeId() == User.UserType.CLUB || user.getTypeId() == User.UserType.AGENT)) {
                    referenceLayout.setVisibility(View.GONE);
                    agentLayout.setVisibility(View.GONE);
                    balanceLayout.setVisibility(View.GONE);
                    rankLayout.setVisibility(View.GONE);
                } else if (currentUser != null && (currentUser.getTypeId() == User.UserType.CLUB ||
                        currentUser.getTypeId() == User.UserType.AGENT)) {
                    balanceLayout.setVisibility(View.GONE);
                }
                if (user.getTypeId() == User.UserType.ADMIN) {
                    referenceLayout.setVisibility(View.GONE);
                    agentLayout.setVisibility(View.GONE);
                    balanceLayout.setVisibility(View.GONE);
                    rankLayout.setVisibility(View.GONE);
                }
                nameTextView.setText(user.getName());
                usernameTextView.setText(user.getUsername());
                emailTextView.setText(user.getEmail());
                mobileTextView.setText(user.getMobile());
                referenceTextView.setText(user.getReference());
                setAgentTitle(user.getAgentId());
                StringBuilder address = new StringBuilder();
                if (user.getUp() != null) {
                    address.append(user.getUp()).append(", ");
                }
                if (user.getUpazilla() != null) {
                    address.append(user.getUpazilla()).append(", ");
                }
                if (user.getDistrict() != null) {
                    address.append(user.getDistrict());
                }
                districtTextView.setText(address);
                balanceTextView.setText(String.valueOf(user.getTotalBalance()));
                setRankTitle(user.getRankId());
                if (user.getStatus().equals("Active")) {
                    activeInactiveBtn.setText(R.string.deactive_text);
                } else {
                    activeInactiveBtn.setText(R.string.active_text);
                }
            }
            if (sharedUser != null) {
                updateUI(sharedUser);
            }
        }
    }

    private void updateUI(SharedUser sharedUser) {
        nameTextView.setText(sharedUser.getName());
        usernameTextView.setText(sharedUser.getUsername());
        emailTextView.setText(sharedUser.getEmail());
        mobileTextView.setText(sharedUser.getMobile());
        referenceLayout.setVisibility(View.GONE);
        agentLayout.setVisibility(View.GONE);
        StringBuilder address = new StringBuilder();
        if (sharedUser.getUp() != null) {
            address.append(sharedUser.getUp()).append(", ");
        }
        if (sharedUser.getUpazilla() != null) {
            address.append(sharedUser.getUpazilla()).append(", ");
        }
        if (sharedUser.getDistrict() != null) {
            address.append(sharedUser.getDistrict());
        }

        districtTextView.setText(address);
        balanceLayout.setVisibility(View.GONE);
        rankTextTextView.setText(R.string.shared_parcent);
        rankTextView.setText(String.format("%s%%", sharedUser.getSharedPercent()));
        activeInactiveBtn.setVisibility(View.GONE);

    }

    private void setAgentTitle(int agentId) {
        disposable.add(
                apiService.getUserById(agentId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<User>() {
                            @Override
                            public void onSuccess(User user) {
                                agentTextView.setText(user.getName());
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void setRankTitle(int rankId) {
        switch (rankId) {
            case User.UserRank.GENERAL_MEMBER:
                rankTextView.setText(User.UserRank.UserRankName.GENERAL_MEMBER);
                break;
            case User.UserRank.ASSOCIATE:
                rankTextView.setText(User.UserRank.UserRankName.ASSOCIATE);
                break;
            case User.UserRank.SR_ASSOCIATE:
                rankTextView.setText(User.UserRank.UserRankName.SR_ASSOCIATE);
                break;
            case User.UserRank.BRONZE:
                rankTextView.setText(User.UserRank.UserRankName.BRONZE);
                break;
            case User.UserRank.SILVER:
                rankTextView.setText(User.UserRank.UserRankName.SILVER);
                break;
            case User.UserRank.GOLD:
                rankTextView.setText(User.UserRank.UserRankName.GOLD);
                break;
            case User.UserRank.P_D:
                rankTextView.setText(User.UserRank.UserRankName.P_D);
                break;
            case User.UserRank.A_M:
                rankTextView.setText(User.UserRank.UserRankName.A_M);
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
