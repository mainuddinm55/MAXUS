package uk.maxusint.maxus.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.activity.LoginActivity;
import uk.maxusint.maxus.adapter.MatchBetAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Bet;
import uk.maxusint.maxus.network.model.BetRate;
import uk.maxusint.maxus.network.model.MatchBetRates;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.DefaultResponse;
import uk.maxusint.maxus.network.response.MatchBetRateResponse;
import uk.maxusint.maxus.utils.SharedPref;


public class UserHomeFragment extends Fragment implements MatchBetAdapter.ItemClickListener {
    public static final String TAG = "UserHomeFragment";
    private CompositeDisposable disposable = new CompositeDisposable();

    static UserHomeFragment sInstance;
    private Context mContext;
    @BindView(R.id.all_bets_recycler_view)
    RecyclerView allBetsRecyclerView;
    @BindView(R.id.no_bets_text_view)
    TextView noBetsTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    MatchBetAdapter adapter;

    private ApiService apiService;
    private User user;
    private List<MatchBetRateResponse.Match_> matches = new ArrayList<>();
    private AlertDialog placeDialog;


    public UserHomeFragment() {
        // Required empty public constructor
    }

    public static synchronized UserHomeFragment getInstance() {
        if (sInstance == null) {
            sInstance = new UserHomeFragment();
        }
        return sInstance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        SharedPref sharedPref = new SharedPref(mContext);
        user = sharedPref.getUser();
        allBetsRecyclerView.setHasFixedSize(true);
        allBetsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        apiService = ApiClient.getInstance().getApi();

        adapter = new MatchBetAdapter(mContext, matches, LoginActivity.NORMAL_TYPE);
        allBetsRecyclerView.setAdapter(adapter);
        adapter.setItemClickListener(UserHomeFragment.this);

        getAllMatches();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void getAllMatches() {
        progressBar.setVisibility(View.VISIBLE);
        int mode = 0;
        switch (user.getTypeId()) {
            case User.UserType.ROYAL:
                mode = Bet.BetMode.TRADE;
                break;
            case User.UserType.CLASSIC:
                mode = Bet.BetMode.TRADE;
                break;
            case User.UserType.PREMIUM:
                mode = Bet.BetMode.ADVANCED;
                break;
        }
        disposable.add(
                apiService.getBetRateWithMatchBetGroup(mode, user.getTypeId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MatchBetRateResponse>() {
                            @Override
                            public void onSuccess(MatchBetRateResponse matchBetRateResponse) {
                                progressBar.setVisibility(View.GONE);
                                matches.clear();
                                matches.addAll(matchBetRateResponse.getMatches());
                                adapter.notifyDataSetChanged();
                                toggleNoBets();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                                progressBar.setVisibility(View.GONE);
                            }
                        })
        );
    }

    private void toggleNoBets() {
        if (matches.size() > 0) {
            noBetsTextView.setVisibility(View.GONE);
        } else {
            noBetsTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBetRateClick(MatchBetRateResponse.Match_ match_, final MatchBetRateResponse.Bet_ bet_, final BetRate betRate) {
        //Toast.makeText(mContext, betRate.getOptions(), Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View view = LayoutInflater.from(mContext).inflate(R.layout.bet_place_layout, null);
        TextView matchTextView = view.findViewById(R.id.match_text_view);
        TextView questionTextView = view.findViewById(R.id.question_text_view);
        TextView answerTextView = view.findViewById(R.id.answer_text_view);
        final TextInputEditText amountEditText = view.findViewById(R.id.amount_edit_text);
        final TextInputLayout amountLayout = view.findViewById(R.id.amount_layout);
        final TextView returnAmountTextView = view.findViewById(R.id.return_amount_text_view);
        final TextView minimumBetTextView = view.findViewById(R.id.minimum_bet_text_view);
        Button placeBetBtn = view.findViewById(R.id.place_bet_btn);
        builder.setView(view);
        placeDialog = builder.create();
        if (user != null) {
            switch (user.getTypeId()) {
                case User.UserType.ROYAL:
                    minimumBetTextView.setText("Maximum: 100$");
                    break;
                case User.UserType.CLASSIC:
                    minimumBetTextView.setText("Maximum: 30$");
                    break;
                case User.UserType.PREMIUM:
                    minimumBetTextView.setText("Minimum: 3$");
                    break;

            }
        }
        String matchTitle = match_.getMatch().getTeam1() + " vs " + match_.getMatch().getTeam2();
        final String question = "Question: " + bet_.getBet().getQuestion();
        String ansWithRate = "Answer: " + betRate.getOptions() + " (" + betRate.getRate() + ") ";
        matchTextView.setText(matchTitle);
        questionTextView.setText(question);
        answerTextView.setText(ansWithRate);
        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double amount;
                double returnAmount;
                if (!TextUtils.isEmpty(s)) {
                    if (user != null) {
                        switch (user.getTypeId()) {
                            case User.UserType.ROYAL:
                                amount = Double.parseDouble(s.toString());
                                if (amount > 100) {
                                    amountLayout.setError("Amount is to big");
                                } else {
                                    returnAmount = amount * betRate.getRate();
                                    amountLayout.setError(null);
                                    returnAmountTextView.setText("Return Amount: " + String.valueOf(returnAmount));
                                }
                                break;
                            case User.UserType.CLASSIC:
                                amount = Double.parseDouble(s.toString());
                                if (amount > 30) {
                                    amountLayout.setError("Amount is to big");
                                } else {
                                    returnAmount = amount * betRate.getRate();
                                    amountLayout.setError(null);
                                    returnAmountTextView.setText("Return Amount: " + String.valueOf(returnAmount));
                                }
                                break;
                            case User.UserType.PREMIUM:
                                amount = Double.parseDouble(s.toString());
                                if (amount < 3) {
                                    amountLayout.setError("Amount is to low");
                                } else {
                                    returnAmount = amount * betRate.getRate();
                                    amountLayout.setError(null);
                                    returnAmountTextView.setText("Return Amount: " + String.valueOf(returnAmount));
                                }
                                break;

                        }
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    returnAmountTextView.setText("Return Amount: ");
                }
            }
        });
        placeBetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(amountEditText.getText())) {
                    amountLayout.setError("Enter amount");
                    amountLayout.requestFocus();
                    return;
                }
                double amount = 0;
                if (user != null) {
                    switch (user.getTypeId()) {
                        case User.UserType.ROYAL:
                            amount = Double.parseDouble(amountEditText.getText().toString());
                            if (amount > 100) {
                                amountLayout.setError("Amount is to big");
                                amountLayout.requestFocus();
                                return;
                            }

                            break;
                        case User.UserType.CLASSIC:
                            amount = Double.parseDouble(amountEditText.getText().toString());
                            if (amount > 30) {
                                amountLayout.setError("Amount is to big");
                                amountLayout.requestFocus();
                                return;
                            }
                            break;
                        case User.UserType.PREMIUM:
                            amount = Double.parseDouble(amountEditText.getText().toString());
                            if (amount < 3) {
                                amountLayout.setError("Amount is to low");
                                amountLayout.requestFocus();
                                return;
                            }
                            break;

                    }

                    if (bet_.getBet().getBetMode() == Bet.BetMode.TRADE) {
                        placeTradeBet(bet_, betRate, amount);
                    } else if (bet_.getBet().getBetMode() == Bet.BetMode.ADVANCED) {
                        placeUserBet(bet_, betRate, amount);
                    }
                }
            }
        });

        placeDialog.show();
    }

    private void placeTradeBet(final MatchBetRateResponse.Bet_ bet_, final BetRate betRate, final double amount) {
        disposable.add(
                apiService.todayAlreadyBetOnTradeMode(user.getUserId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse defaultResponse) {
                                if (!defaultResponse.isError()) {
                                    placeUserBet(bet_, betRate, amount);
                                } else {
                                    placeDialog.dismiss();
                                    Toast.makeText(mContext, defaultResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                placeDialog.dismiss();
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void placeUserBet(final MatchBetRateResponse.Bet_ bet_, final BetRate betRate, double amount) {
        double returnAmount = amount * betRate.getRate();
        disposable.add(
                apiService.placeUserBet(
                        user.getUserId(),
                        bet_.getBet().getBetId(),
                        betRate.getId(),
                        betRate.getRate(),
                        amount,
                        returnAmount,
                        betRate.getBetModeId()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse responseBody) {
                                placeDialog.dismiss();
                                if (!responseBody.isError()) {
                                    Toast.makeText(mContext, responseBody.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, responseBody.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                placeDialog.dismiss();
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }


    @Override
    public void onBetClick(MatchBetRateResponse.Match_ match_, MatchBetRateResponse.Bet_ bet_) {

    }

    @Override
    public void onMatchClick(MatchBetRateResponse.Match_ match_) {

    }

    @Override
    public void onFinishClick(MatchBetRateResponse.Bet_ bet_) {

    }

    @Override
    public void onCancelClick(MatchBetRateResponse.Bet_ bet_) {

    }

}