package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.NoSuchElementException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.fragment.AccountFragment;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
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
        Log.e(TAG, "onCreate: " );
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        apiService = ApiClient.getInstance().getApi();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPref sharedPref = new SharedPref(this);
        User user = sharedPref.getUser();
        if (user != null) {
            if (user.getTypeId() == User.UserType.AGENT) {
                balanceTransferTextView.setVisibility(View.GONE);
            }
            nameTextView.setText(user.getName());
            disposable.add(
                    apiService.getUserBalance(user.getUserId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<Double>() {
                                @Override
                                public void onSuccess(Double aDouble) {
                                    String amount = String.valueOf(aDouble) + " $";
                                    balanceTextView.setText(amount);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (e instanceof NoSuchElementException) {
                                        Toast.makeText(AccountActivity.this, "User invalid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
            );

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
