package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.DefaultResponse;
import uk.maxusint.maxus.network.response.UserResponse;
import uk.maxusint.maxus.utils.SharedPref;
import uk.maxusint.maxus.utils.Transaction;


/**
 * A simple {@link Fragment} subclass.
 */
public class DepositRequestFragment extends Fragment implements TextWatcher {
    public static final String TAG = "DepositRequestFragment";
    public static final String ACTION = "uk.maxusint.maxus.fragment.ACTION";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;

    @BindView(R.id.send_to_text_view)
    TextView sendToTextView;
    @BindView(R.id.username_edit_text)
    TextInputEditText usernameEditText;
    @BindView(R.id.username_layout)
    TextInputLayout usernameLayout;
    @BindView(R.id.amount_edit_text)
    TextInputEditText amountEditText;
    @BindView(R.id.amount_layout)
    TextInputLayout amountLayout;
    @BindView(R.id.send_request_btn)
    Button sendRequestBtn;
    private int transactionType;
    private User agent;
    private User currentUser;
    private User club;
    private Context mContext;
    private User admin;

    public DepositRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deposit_request, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        Log.e(TAG, "onViewCreated: ");
        amountEditText.addTextChangedListener(this);
        usernameEditText.addTextChangedListener(this);
        apiService = ApiClient.getInstance().getApi();
        Bundle typeBundle = getArguments();

        SharedPref sharedPref = new SharedPref(mContext);
        currentUser = sharedPref.getUser();
        if (currentUser.getTypeId() == User.UserType.ROYAL ||
                currentUser.getTypeId() == User.UserType.CLASSIC ||
                currentUser.getTypeId() == User.UserType.PREMIUM) {
            getAgentById(currentUser.getAgentId());
        } else if (currentUser.getTypeId() == User.UserType.AGENT) {
            getClubById(currentUser.getClubId());
        } else if (currentUser.getTypeId() == User.UserType.CLUB) {
            getAdmin();
        }

        if (typeBundle != null) {
            transactionType = typeBundle.getInt(ACTION);
            if (transactionType == Transaction.Type.BALANCE_TRANSFER_REQUEST) {
                sendToTextView.setVisibility(View.GONE);
                usernameLayout.setVisibility(View.VISIBLE);
            } else {
                usernameLayout.setVisibility(View.GONE);
                sendToTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getAdmin() {
        disposable.add(
                apiService.getAdmin()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<User>() {
                            @Override
                            public void onSuccess(User user) {
                                if (user != null) {
                                    String requestTo = "Request to: " + user.getName();
                                    admin = user;
                                    sendToTextView.setText(requestTo);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void getClubById(int clubId) {
        disposable.add(
                apiService.getClubByID(clubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (!userResponse.getError()) {
                                    club = userResponse.getUser();
                                    String requestTo = "Request to: " + userResponse.getUser().getName();
                                    sendToTextView.setText(requestTo);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void getAgentById(int id) {
        disposable.add(
                apiService.getAgentById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                String requestTo = "Request to: " + userResponse.getUser().getName();
                                sendToTextView.setText(requestTo);
                                agent = userResponse.getUser();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );

    }

    @OnClick(R.id.send_request_btn)
    void sendRequest(View view) {
        if (TextUtils.isEmpty(amountEditText.getText())) {
            amountLayout.setError("Enter amount");
            amountLayout.requestFocus();
            return;
        }
        switch (transactionType) {
            case Transaction.Type.DEPOSIT_REQUEST:
                switch (currentUser.getTypeId()) {
                    case User.UserType.CLUB:
                        clubDepositRequest();
                        break;
                    case User.UserType.AGENT:
                        agentDepositRequest();
                        break;
                    case User.UserType.CLASSIC:
                    case User.UserType.ROYAL:
                    case User.UserType.PREMIUM:
                        userDepositRequest();
                        break;
                }
                break;
            case Transaction.Type.WITHDRAW_REQUEST:
                switch (currentUser.getTypeId()) {
                    case User.UserType.CLUB:
                        clubWithdrawRequest();
                        break;
                    case User.UserType.AGENT:
                        agentWithdrawRequest();
                        break;
                    case User.UserType.CLASSIC:
                    case User.UserType.ROYAL:
                    case User.UserType.PREMIUM:
                        userWithdrawRequest();
                        break;
                }
                break;
            case Transaction.Type.BALANCE_TRANSFER_REQUEST:
                if (currentUser.getTypeId() != User.UserType.AGENT || currentUser.getTypeId() != User.UserType.ADMIN) {
                    if (TextUtils.isEmpty(usernameEditText.getText())) {
                        usernameLayout.setError("Enter username");
                        usernameLayout.requestFocus();
                        return;
                    }
                    if (currentUser.getUsername().equals(usernameEditText.getText().toString())) {
                        usernameLayout.setError("User is invalid");
                        usernameLayout.requestFocus();
                        return;
                    }

                    switch (currentUser.getTypeId()) {
                        case User.UserType.CLUB:
                            break;
                        case User.UserType.CLASSIC:
                        case User.UserType.ROYAL:
                        case User.UserType.PREMIUM:
                            userBalanceTransferRequest();
                            break;
                    }
                }
                break;
        }
    }

    private void clubWithdrawRequest() {
        if (admin != null) {
            sendClubWithdrawRequest();
        }

    }

    private void sendClubWithdrawRequest() {
        disposable.add(
                apiService.getUserBalance(currentUser.getUserId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Double>() {
                            @Override
                            public void onSuccess(Double aDouble) {
                                Double amount = Double.parseDouble(amountEditText.getText().toString());
                                if (amount >= aDouble) {
                                    amountLayout.setError("Insufficient Balance");
                                    amountLayout.requestFocus();
                                    return;
                                }
                                disposable.add(
                                        apiService.addTransaction(
                                                currentUser.getUsername(),
                                                admin.getUsername(),
                                                amount,
                                                Transaction.Type.TypeString.WITHDRAW,
                                                Transaction.Charge.WITHDRAW
                                        ).subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                                                    @Override
                                                    public void onSuccess(DefaultResponse defaultResponse) {
                                                        if (!defaultResponse.isError()) {
                                                            Toast.makeText(mContext, defaultResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        Log.e(TAG, "onError: " + e.getMessage());
                                                    }
                                                })
                                );
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: "+e.getMessage() );
                            }
                        })
        );
    }

    private void clubDepositRequest() {
        if (admin != null) {
            sendClubDepositRequest();
        } else {
            disposable.add(
                    apiService.getAdmin()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<User>() {
                                @Override
                                public void onSuccess(User user) {
                                    if (user != null) {
                                        String requestTo = "Request to: " + user.getName();
                                        admin = user;
                                        sendToTextView.setText(requestTo);
                                        sendClubDepositRequest();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, "onError: " + e.getMessage());
                                }
                            })
            );
        }
    }

    private void sendClubDepositRequest() {
        disposable.add(
                apiService.addTransaction(
                        currentUser.getUsername(),
                        admin.getUsername(),
                        Double.parseDouble(amountEditText.getText().toString()),
                        Transaction.Type.TypeString.DEPOSIT,
                        Transaction.Charge.DEPOSIT
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse defaultResponse) {
                                if (!defaultResponse.isError()) {
                                    Toast.makeText(mContext, defaultResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })

        );
    }

    private void agentWithdrawRequest() {
        if (club != null) {
            sendAgentWithdrawRequest();
        } else {
            disposable.add(
                    apiService.getClubByID(currentUser.getClubId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                                @Override
                                public void onSuccess(UserResponse userResponse) {
                                    if (!userResponse.getError()) {
                                        club = userResponse.getUser();
                                        String requestTo = "Request to: " + userResponse.getUser().getName();
                                        sendToTextView.setText(requestTo);
                                        club = userResponse.getUser();
                                        sendAgentWithdrawRequest();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, "onError: " + e.getMessage());
                                }
                            })
            );
        }
    }

    private void sendAgentWithdrawRequest() {
        disposable.add(
                apiService.getUserBalance(currentUser.getUserId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Double>() {
                            @Override
                            public void onSuccess(Double aDouble) {
                                Double amount = Double.valueOf(amountEditText.getText().toString());
                                if (amount >= aDouble) {
                                    amountLayout.setError("Insufficient Balance");
                                    amountLayout.requestFocus();
                                    return;
                                }
                                disposable.add(
                                        apiService.addTransaction(
                                                currentUser.getUsername(),
                                                club.getUsername(),
                                                Double.valueOf(amountEditText.getText().toString()),
                                                Transaction.Type.TypeString.WITHDRAW,
                                                Transaction.Charge.WITHDRAW
                                        ).subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                                                    @Override
                                                    public void onSuccess(DefaultResponse defaultResponse) {
                                                        if (!defaultResponse.isError()) {
                                                            Toast.makeText(mContext, defaultResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        Log.e(TAG, "onError: " + e.getMessage());
                                                    }
                                                })
                                );
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof NoSuchElementException) {
                                    Toast.makeText(mContext, "User not found", Toast.LENGTH_SHORT).show();
                                }
                                e.printStackTrace();
                            }
                        })
        );

    }

    private void agentDepositRequest() {
        if (club != null) {
            sendAgentDepositRequest();
        } else {
            disposable.add(
                    apiService.getClubByID(currentUser.getClubId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                                @Override
                                public void onSuccess(UserResponse userResponse) {
                                    if (!userResponse.getError()) {
                                        club = userResponse.getUser();
                                        String requestTo = "Request to: " + userResponse.getUser().getName();
                                        sendToTextView.setText(requestTo);
                                        club = userResponse.getUser();
                                        sendAgentDepositRequest();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, "onError: " + e.getMessage());
                                }
                            })
            );
        }
    }

    private void sendAgentDepositRequest() {
        disposable.add(
                apiService.addTransaction(
                        currentUser.getUsername(),
                        club.getUsername(),
                        Double.parseDouble(amountEditText.getText().toString()),
                        Transaction.Type.TypeString.DEPOSIT,
                        Transaction.Charge.DEPOSIT
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse defaultResponse) {
                                if (!defaultResponse.isError()) {
                                    Toast.makeText(mContext, defaultResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void userBalanceTransferRequest() {
        disposable.add(
                apiService.isUserExist(usernameEditText.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse defaultResponse) {
                                if (!defaultResponse.isError()) {
                                    userBalanceTransferRequestSend();
                                } else {
                                    usernameLayout.setError("User is invalid");
                                    Log.e(TAG, "onSuccess: " + defaultResponse.getMessage());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );

    }

    private void userBalanceTransferRequestSend() {
        disposable.add(
                apiService.addTransaction(
                        currentUser.getUsername(),
                        usernameEditText.getText().toString(),
                        Double.valueOf(amountEditText.getText().toString()),
                        Transaction.Type.TypeString.BALANCE_TRANSFER,
                        Transaction.Charge.BALANCE_TRANSFER
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse defaultResponse) {
                                if (!defaultResponse.isError()) {
                                    Toast.makeText(mContext, defaultResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e(TAG, "onSuccess: " + defaultResponse.getMessage());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void userWithdrawRequest() {
        disposable.add(
                apiService.getUserBalance(currentUser.getUserId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Double>() {
                            @Override
                            public void onSuccess(Double aDouble) {
                                Double amount = Double.valueOf(amountEditText.getText().toString());
                                if (amount >= aDouble) {
                                    amountLayout.setError("Insufficient Balance");
                                    amountLayout.requestFocus();
                                    return;
                                }
                                sendUserWithdrawRequest();
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof NoSuchElementException) {
                                    Toast.makeText(mContext, "User not found", Toast.LENGTH_SHORT).show();
                                }
                                e.printStackTrace();
                            }
                        })
        );

    }

    private void sendUserWithdrawRequest() {
        disposable.add(
                apiService.addTransaction(
                        currentUser.getUsername(),
                        agent.getUsername(),
                        Double.valueOf(amountEditText.getText().toString()),
                        Transaction.Type.TypeString.WITHDRAW,
                        Transaction.Charge.WITHDRAW
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse defaultResponse) {
                                if (!defaultResponse.isError()) {
                                    Toast.makeText(mContext, defaultResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e(TAG, "onSuccess: " + defaultResponse.getMessage());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void userDepositRequest() {
        disposable.add(
                apiService.addTransaction(
                        currentUser.getUsername(),
                        agent.getUsername(),
                        Double.parseDouble(amountEditText.getText().toString()),
                        Transaction.Type.TypeString.DEPOSIT,
                        Transaction.Charge.DEPOSIT
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse defaultResponse) {
                                if (!defaultResponse.isError()) {
                                    Toast.makeText(mContext, defaultResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e(TAG, "onSuccess: " + defaultResponse.getMessage());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        usernameLayout.setError(null);
        amountLayout.setError(null);
        Log.e(TAG, "onTextChanged: " + count);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
