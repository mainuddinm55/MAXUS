package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.BetResult;
import uk.maxusint.maxus.network.model.Commission;
import uk.maxusint.maxus.network.model.Notification;
import uk.maxusint.maxus.network.model.Transaction;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

public class NotificationDetailsActivity extends AppCompatActivity {
    private static final String TAG = "NotificationDetailsActi";
    public static final String NOTIFICATION = "uk.maxusint.maxus.activity.NOTIFICATION";
    private static final Integer RIGHT_ANSWER = 1;
    public static final Integer PICK_ANSWER = 2;
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    @BindView(R.id.match_or_trans_text_view)
    TextView matchOrTransTextView;
    @BindView(R.id.from_or_question_text_view)
    TextView fromNameOrQuestionTextView;
    @BindView(R.id.from_or_question_text_text_view)
    TextView fromNameOrQuestionTextTextView;
    @BindView(R.id.email_or_right_ans_text_view)
    TextView emailOrRightAnsTextView;
    @BindView(R.id.right_ans_text_view)
    TextView rightAnsTextView;
    @BindView(R.id.pick_and_text_view)
    TextView pickAnsTextView;
    @BindView(R.id.mobile_or_pick_ans_text_view)
    TextView mobileOrPickAnsTextView;
    @BindView(R.id.amount_text_view)
    TextView amountTextView;
    @BindView(R.id.amount_text_text_view)
    TextView amountTextTextView;
    @BindView(R.id.return_amount_text_view)
    TextView returnAmountTextView;
    @BindView(R.id.return_amount_layout)
    LinearLayout returnAmountLayout;
    @BindView(R.id.return_amount_text_text_view)
    TextView returnAmountTextTextView;
    @BindView(R.id.status_text_view)
    TextView statusTextView;
    private Notification notification;
    private Transaction mTransaction;
    private User mFromUser;
    private User currentUser;
    private Commission mCommission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        currentUser = new SharedPref(this).getUser();
        apiService = ApiClient.getInstance().getApi();
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

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        notification = (Notification) intent.getSerializableExtra(NotificationDetailsActivity.NOTIFICATION);
        if (notification != null) {
            String type = notification.getType();
            switch (type) {
                case Notification.Type.TRANSACTION:
                    returnAmountLayout.setVisibility(View.GONE);
                    getTransaction();
                    break;
                case Notification.Type.BET_RESULT:
                    if (currentUser != null) {
                        getBetResult();
                    }
                    break;
                case Notification.Type.COMMISSION:
                    returnAmountLayout.setVisibility(View.GONE);
                    getCommission();
                    break;
            }
        }
    }

    private void getBetResult() {
        disposable.add(
                apiService.getBetResult(currentUser.getUserId(), notification.getTypeId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BetResult>() {
                            @Override
                            public void onSuccess(BetResult betResult) {
                                getBetOption(PICK_ANSWER, betResult.getBetOptionId());
                                getBetOption(RIGHT_ANSWER, betResult.getRightAns());
                                matchOrTransTextView.setText(betResult.getMatch());
                                fromNameOrQuestionTextTextView.setText(getResources().getText(R.string.question_text));
                                fromNameOrQuestionTextView.setText(betResult.getQuestion());
                                amountTextView.setText(String.format("%s$", betResult.getBetAmount()));
                                rightAnsTextView.setText(getResources().getString(R.string.right_ans_text));
                                pickAnsTextView.setText(getResources().getString(R.string.pick_ans_text));
                                returnAmountLayout.setVisibility(View.VISIBLE);
                                returnAmountTextView.setText(String.format("%s$", betResult.getBetReturnAmount()));
                                amountTextTextView.setText(getResources().getString(R.string.amount_text));
                                returnAmountTextTextView.setText(getResources().getString(R.string.return_amount_text));
                                statusTextView.setText(notification.getBody());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void getBetOption(final Integer type, Integer betOptionId) {
        disposable.add(
                apiService.getOptionById(betOptionId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<String>() {
                            @Override
                            public void onSuccess(String s) {
                                if (type.equals(PICK_ANSWER)) {
                                    mobileOrPickAnsTextView.setText(s);
                                } else if (type.equals(RIGHT_ANSWER)) {
                                    emailOrRightAnsTextView.setText(s);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void getCommission() {
        disposable.add(
                apiService.getCommissionById(notification.getTypeId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Commission>() {
                            @Override
                            public void onSuccess(Commission commission) {
                                mCommission = commission;
                                getFromUser(commission.getFromUserId());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: "+e.getMessage() );
                                e.printStackTrace();
                            }
                        })
        );
    }

    private void getFromUser(Integer fromUserId) {
        disposable.add(
                apiService.getUserById(fromUserId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<User>() {
                            @Override
                            public void onSuccess(User user) {
                                updateUI(user);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                                e.printStackTrace();
                            }
                        })
        );
    }

    private void updateUI(User user) {
        matchOrTransTextView.setText(getResources().getString(R.string.commission_text));
        fromNameOrQuestionTextView.setText(user.getName());
        fromNameOrQuestionTextTextView.setText("From");
        mobileOrPickAnsTextView.setText(user.getMobile());
        emailOrRightAnsTextView.setText(user.getEmail());
        statusTextView.setText(mCommission.getPurpose());
        amountTextTextView.setText(getResources().getString(R.string.amount_text));
        String amount = mCommission.getAmount() + " $";
        amountTextView.setText(amount);
    }

    private void getTransaction() {
        disposable.add(
                apiService.getTransactionById(notification.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Transaction>() {
                            @Override
                            public void onSuccess(Transaction transaction) {
                                mTransaction = transaction;
                                getFromUser();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void getFromUser() {
        disposable.add(
                apiService.getUserByUsername(mTransaction.getToUsername())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<User>() {
                            @Override
                            public void onSuccess(User user) {
                                mFromUser = user;
                                updateUI();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void updateUI() {
        matchOrTransTextView.setText(mTransaction.getTransType());
        fromNameOrQuestionTextView.setText(mFromUser.getName());
        fromNameOrQuestionTextTextView.setText("From");
        mobileOrPickAnsTextView.setText(mFromUser.getMobile());
        emailOrRightAnsTextView.setText(mFromUser.getEmail());
        amountTextTextView.setText("Amount");
        String amount = mTransaction.getAmount() + " $";
        amountTextView.setText(amount);
        if (mTransaction.getStatus().equalsIgnoreCase(uk.maxusint.maxus.utils.Transaction.Status.SUCCESS)) {
            statusTextView.setText("Transaction Completed");
        } else {
            statusTextView.setText("Transaction Failed");
        }
    }
}
