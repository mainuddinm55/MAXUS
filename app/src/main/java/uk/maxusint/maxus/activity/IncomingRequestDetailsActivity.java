package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.gc.materialdesign.widgets.ProgressDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Transaction;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.DefaultResponse;
import uk.maxusint.maxus.utils.SharedPref;

public class IncomingRequestDetailsActivity extends AppCompatActivity {
    public static final String TAG = "IncomingRequestDetailsA";
    public static final String TRANSACTION = "uk.maxusint.maxus.activity.TRANSACTION";

    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;

    @BindView(R.id.date_text_view)
    TextView dateTextView;
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.username_text_view)
    TextView usernameTextView;
    @BindView(R.id.mobile_text_view)
    TextView mobileTextView;
    @BindView(R.id.amount_text_view)
    TextView amountTextView;
    @BindView(R.id.status_text_view)
    TextView statusTextView;
    @BindView(R.id.transaction_done_btn)
    Button transactionDoneBtn;

    private Transaction transaction;
    private User currentUser;
    private ProgressDialog dialog;
    private User requestFromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_request_details);
        ButterKnife.bind(this);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPref sharedPref = new SharedPref(this);
        currentUser = sharedPref.getUser();
        apiService = ApiClient.getInstance().getApi();
        Intent intent = getIntent();
        transaction = (Transaction) intent.getSerializableExtra(TRANSACTION);
        if (transaction != null) {
            getUserInfo(transaction.getFromUsername());
            if (transaction.getStatus().equalsIgnoreCase(uk.maxusint.maxus.utils.Transaction.Status.SUCCESS)) {
                transactionDoneBtn.setVisibility(View.GONE);
            }
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

    private void getUserInfo(String fromUsername) {
        disposable.add(
                apiService.getUserByUsername(fromUsername)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<User>() {
                            @Override
                            public void onSuccess(User user) {
                                updateUI(user);
                                requestFromUser = user;
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void updateUI(User user) {
        dateTextView.setText(getDateTimeFromString(transaction.getTransDate()));
        nameTextView.setText(user.getName());
        usernameTextView.setText(user.getUsername());
        mobileTextView.setText(user.getMobile());
        String amount = transaction.getAmount() + " $";
        amountTextView.setText(amount);
        statusTextView.setText(transaction.getStatus());
    }

    private String getDateTimeFromString(String dateString) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date date = dateTimeFormat.parse(dateString);
            SimpleDateFormat timeFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aaa", Locale.US);
            return timeFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @OnClick(R.id.transaction_done_btn)
    void transactionDone() {
        if (transaction.getTransType().equalsIgnoreCase(uk.maxusint.maxus.utils.Transaction.Type.TypeString.WITHDRAW)) {
            showSpinner();
            disposable.add(
                    apiService.getUserBalance(requestFromUser.getUserId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<Double>() {
                                @Override
                                public void onSuccess(Double aDouble) {
                                    if (transaction.getAmount() > aDouble) {
                                        dismissSpinner();
                                        Toast.makeText(IncomingRequestDetailsActivity.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                                    } else {
                                        sendWithdrawTransactionAmount();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (e instanceof NoSuchElementException) {
                                        Toast.makeText(IncomingRequestDetailsActivity.this, "User invalid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
            );
        } else {
            showSpinner();
            disposable.add(
                    apiService.getUserBalance(currentUser.getUserId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<Double>() {
                                @Override
                                public void onSuccess(Double aDouble) {
                                    if (transaction.getAmount() > aDouble) {
                                        dismissSpinner();
                                        Toast.makeText(IncomingRequestDetailsActivity.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                                    } else {
                                        sendDepositTransactionAmount();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (e instanceof NoSuchElementException) {
                                        Toast.makeText(IncomingRequestDetailsActivity.this, "User invalid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
            );
        }
    }

    private void sendWithdrawTransactionAmount() {
        disposable.add(
                apiService.transactionApproved(
                        transaction.getAmount(),
                        transaction.getFromUsername(),
                        transaction.getToUsername()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse defaultResponse) {
                                dismissSpinner();
                                if (!defaultResponse.isError()) {
                                    updateTransactionStatus(transaction.getId());
                                }
                                Toast.makeText(IncomingRequestDetailsActivity.this, defaultResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissSpinner();
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void sendDepositTransactionAmount() {
        disposable.add(
                apiService.transactionApproved(
                        transaction.getAmount(),
                        transaction.getToUsername(),
                        transaction.getFromUsername()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse defaultResponse) {
                                dismissSpinner();
                                if (!defaultResponse.isError()) {
                                    updateTransactionStatus(transaction.getId());
                                }
                                Toast.makeText(IncomingRequestDetailsActivity.this, defaultResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissSpinner();
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void updateTransactionStatus(int id) {
        disposable.add(
                apiService.updateTransactionStatus(uk.maxusint.maxus.utils.Transaction.Status.SUCCESS, id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Transaction>() {
                            @Override
                            public void onSuccess(Transaction transaction) {
                                Toast.makeText(IncomingRequestDetailsActivity.this, transaction.getStatus(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void showSpinner() {
        dialog = new ProgressDialog(this, "Loading...");
        dialog.show();
    }

    private void dismissSpinner() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
