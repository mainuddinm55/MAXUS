package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenu;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.activity.LoginActivity;
import uk.maxusint.maxus.activity.UpdateBetActivity;
import uk.maxusint.maxus.adapter.MatchBetAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Bet;
import uk.maxusint.maxus.network.model.BetRate;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.BetResponse;
import uk.maxusint.maxus.network.response.DefaultResponse;
import uk.maxusint.maxus.network.response.MatchBetRateResponse;
import uk.maxusint.maxus.utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllPremiumFragment extends Fragment implements MatchBetAdapter.ItemClickListener, FabSpeedDial.MenuListener {
    public static final String TAG = "AllPremiumFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    private Context mContext;
    @BindView(R.id.all_bets_recycler_view)
    RecyclerView allBetsRecyclerView;
    @BindView(R.id.no_bets_text_view)
    TextView noBetsTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fab_speed_dial)
    FabSpeedDial fabSpeedDial;

    private ApiService apiService;
    private int betRateId = 0;


    static AllPremiumFragment sInstance;
    private MatchBetAdapter adapter;
    private List<MatchBetRateResponse.Match_> matchBets = new ArrayList<>();
    private User currentUser;

    public AllPremiumFragment() {
        // Required empty public constructor
        Log.e(TAG, "AllPremiumFragment: ");
    }

    public static synchronized AllPremiumFragment getInstance() {
        if (sInstance == null)
            sInstance = new AllPremiumFragment();
        return sInstance;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_premium, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onViewCreated: ");
        ButterKnife.bind(this, view);
        allBetsRecyclerView.setHasFixedSize(true);
        allBetsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        apiService = ApiClient.getInstance().getApi();

        fabSpeedDial.setMenuListener(this);
        SharedPref sharedPref = new SharedPref(mContext);
        currentUser = sharedPref.getUser();
        if (currentUser!=null && currentUser.getTypeId() == User.UserType.ADMIN) {
            fabSpeedDial.setVisibility(View.VISIBLE);
            adapter = new MatchBetAdapter(mContext, matchBets, LoginActivity.ADMIN_TYPE);

        } else {
            adapter = new MatchBetAdapter(mContext, matchBets, LoginActivity.NORMAL_TYPE);
        }
        allBetsRecyclerView.setAdapter(adapter);
        adapter.setItemClickListener(AllPremiumFragment.this);
        getAllMatches();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }

    private void getAllMatches() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getBetRateWithMatchBetGroup(Bet.BetMode.ADVANCED, User.UserType.PREMIUM)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MatchBetRateResponse>() {
                            @Override
                            public void onSuccess(MatchBetRateResponse matchBetRateResponse) {
                                progressBar.setVisibility(View.GONE);
                                matchBets.clear();
                                matchBets.addAll(matchBetRateResponse.getMatches());
                                adapter.notifyDataSetChanged();
                                noToggleBet();
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "onError: " + e.getMessage());
                                e.printStackTrace();
                            }
                        })
        );
    }

    private void noToggleBet() {
        if (matchBets.size() > 0) {
            noBetsTextView.setVisibility(View.GONE);
        } else {
            noBetsTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBetRateClick(MatchBetRateResponse.Match_ match_, MatchBetRateResponse.Bet_ bet_, BetRate betRate) {
        if (currentUser.getTypeId() == User.UserType.PREMIUM ||
                currentUser.getTypeId() == User.UserType.ROYAL ||
                currentUser.getTypeId() == User.UserType.CLASSIC) {
            showPlaceBetDialog(match_, bet_, betRate);
        }
    }

    private void showPlaceBetDialog(final MatchBetRateResponse.Match_ match_, final MatchBetRateResponse.Bet_ bet_, final BetRate betRate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View view = LayoutInflater.from(mContext).inflate(R.layout.bet_place_layout, null);
        TextView questionTextView = view.findViewById(R.id.question_text_view);
        TextView answerTextView = view.findViewById(R.id.answer_text_view);
        final TextInputEditText amountEditText = view.findViewById(R.id.amount_edit_text);
        final TextInputLayout amountLayout = view.findViewById(R.id.amount_layout);
        final TextView returnAmountTextView = view.findViewById(R.id.return_amount_text_view);
        final TextView minimumBetTextView = view.findViewById(R.id.minimum_bet_text_view);
        Button placeBetBtn = view.findViewById(R.id.place_bet_btn);
        Button cancelBetBtn = view.findViewById(R.id.cancel_bet_btn);
        returnAmountTextView.setText("Return Amount: ");
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        if (currentUser != null) {
            switch (currentUser.getTypeId()) {
                case User.UserType.ROYAL:
                    minimumBetTextView.setVisibility(View.GONE);
                    break;
                case User.UserType.CLASSIC:
                    minimumBetTextView.setVisibility(View.GONE);
                    break;
                case User.UserType.PREMIUM:
                    minimumBetTextView.setText("Minimum: 3$");
                    break;

            }
        }
        String matchTitle = match_.getMatch().getTeam1() + " vs " + match_.getMatch().getTeam2();
        final String question = "Question: " + bet_.getBet().getQuestion();
        String ansWithRate = "Answer: " + betRate.getOptions() + " (" + betRate.getRate() + ") ";
        builder.setTitle(matchTitle);
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
                    if (currentUser != null) {
                        switch (currentUser.getTypeId()) {
                            case User.UserType.ROYAL:
                            case User.UserType.CLASSIC:
                                amount = Double.parseDouble(s.toString());
                                returnAmount = amount * betRate.getRate();
                                amountLayout.setError(null);
                                returnAmountTextView.setText("Return Amount: " + String.valueOf(returnAmount));
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
            public void afterTextChanged(final Editable s) {
                if (TextUtils.isEmpty(s)) {
                    returnAmountTextView.setText("Return Amount: ");
                } else if (!TextUtils.isEmpty(s)) {
                    disposable.add(
                            apiService.getUserBalance(currentUser.getUserId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableSingleObserver<Double>() {
                                        @Override
                                        public void onSuccess(Double aDouble) {
                                            if (!TextUtils.isEmpty(s)) {
                                                double amount = Double.parseDouble(s.toString());
                                                if (amount > aDouble) {
                                                    amountLayout.setError("Insufficient balance");
                                                }
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
        });
        cancelBetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        placeBetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amount = 0;
                if (TextUtils.isEmpty(amountEditText.getText())) {
                    amountLayout.setError("Enter amount");
                    amountLayout.requestFocus();
                    return;
                }
                if (currentUser != null) {
                    switch (currentUser.getTypeId()) {
                        case User.UserType.PREMIUM:
                            amount = Double.parseDouble(amountEditText.getText().toString());
                            if (amount < 3) {
                                amountLayout.setError("Amount is to low");
                                amountLayout.requestFocus();
                                return;
                            }
                            break;
                    }
                    amount = Double.parseDouble(amountEditText.getText().toString());

                    dialog.dismiss();
                    placeBet(bet_, betRate, amount);

                }
            }
        });

        dialog.show();
    }

    private void placeBet(final MatchBetRateResponse.Bet_ bet_, final BetRate betRate, final double amount) {
        final double returnAmount = amount * betRate.getRate();


        disposable.add(
                apiService.placeUserBet(
                        currentUser.getUserId(),
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
                            public void onSuccess(DefaultResponse response) {
                                if (!response.isError()) {
                                    assert currentUser != null;
                                    giveUsernameCommission(amount, bet_.getBet().getBetId());
                                    Toast.makeText(mContext, response.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, response.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void giveUsernameCommission(final double amount, final int betId) {
        giveCommission(currentUser.getReference(), 1, amount, betId);
        disposable.add(
                apiService.getUserById(currentUser.getAgentId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<User>() {
                            @Override
                            public void onSuccess(User user) {
                                if (user != null) {
                                    if (user.getTypeId() == User.UserType.AGENT) {
                                        giveCommission(user.getUsername(), 0.5, amount, betId);
                                        disposable.add(
                                                apiService.getUserById(user.getClubId())
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribeWith(new DisposableSingleObserver<User>() {
                                                            @Override
                                                            public void onSuccess(User user) {
                                                                if (user.getTypeId() == User.UserType.CLUB) {
                                                                    giveCommission(user.getUsername(), 0.5, amount, betId);
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(Throwable e) {
                                                                Log.e(TAG, "onError: " + e.getMessage());
                                                                e.printStackTrace();
                                                            }
                                                        })
                                        );
                                    }
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                                e.printStackTrace();
                            }
                        })
        );
    }

    private void giveCommission(String username, double rate, double amount, int betId) {
        double commAmount = (amount / 100) * rate;
        String purpose = "From Reference Betting on Bet";
        disposable.add(
                apiService.giveCommission(rate, commAmount, username, currentUser.getUserId(), betId, purpose)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse response) {
                                if (!response.isError()) {
                                    Toast.makeText(mContext, response.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, response.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                                e.printStackTrace();
                            }
                        })
        );
    }

    @Override
    public void onBetClick(MatchBetRateResponse.Match_ match_, MatchBetRateResponse.Bet_ bet_) {
        Intent intent = new Intent(mContext, UpdateBetActivity.class);
        intent.putExtra(UpdateBetActivity.BET, bet_);
        intent.putExtra(UpdateBetActivity.MATCH, match_);
        intent.putParcelableArrayListExtra(UpdateBetActivity.BET_RATES, (ArrayList<? extends Parcelable>) bet_.getBetRates());
        intent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_UPDATE_BET);
        Log.e(TAG, "onBetClick: " + bet_.getBetRates().size());
        Log.e(TAG, "onBetClick: " + bet_.getBet().getQuestion());
        startActivity(intent);
    }

    @Override
    public void onMatchClick(MatchBetRateResponse.Match_ match_) {
        //Toast.makeText(getContext(), match_.getMatch().getTeam1(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinishClick(final MatchBetRateResponse.Bet_ bet_) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Select Right Answer..");
        View view = LayoutInflater.from(mContext).inflate(R.layout.finish_bet_dialog, null);
        builder.setView(view);
        RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        for (int i = 0; i < bet_.getBetRates().size(); i++) {
            final RadioButton radioButton = new RadioButton(mContext);
            radioButton.setTextSize(16);
            radioButton.setPaddingRelative(10, 10, 10, 10);
            radioButton.setId(bet_.getBetRates().get(i).getId());
            String options = bet_.getBetRates().get(i).getOptions() + " (" + bet_.getBetRates().get(i).getRate() + " )";
            radioButton.setText(options);
            radioGroup.addView(radioButton);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                betRateId = radioButton.getId();
            }
        });
        builder.setPositiveButton("FINISH", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (betRateId != 0) {
                    updateBet(bet_);
                } else {
                    dialog.dismiss();
                    Toast.makeText(mContext, "Select Right answer", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void updateBet(final MatchBetRateResponse.Bet_ bet_) {
        disposable.add(
                apiService.finishBet(
                        "Finish",
                        betRateId,
                        bet_.getBet().getBetId()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BetResponse>() {
                            @Override
                            public void onSuccess(BetResponse betResponse) {
                                if (!betResponse.isErr()) {
                                    Log.e(TAG, "onSuccess: " + betResponse.getMsg());
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
    public void onCancelClick(MatchBetRateResponse.Bet_ bet_) {
        cancelBet(bet_);
    }


    private void cancelBet(MatchBetRateResponse.Bet_ bet_) {
        disposable.add(
                apiService.cancelBet(bet_.getBet().getBetId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BetResponse>() {
                            @Override
                            public void onSuccess(BetResponse betResponse) {
                                Log.e(TAG, "onSuccess: " + betResponse.getMsg());
                                Log.e(TAG, "onSuccess: " + betResponse.isErr());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        //fragmentLoader = (FragmentLoader) context;
    }

    @Override
    public boolean onPrepareMenu(NavigationMenu navigationMenu) {
        return true;
    }

    @Override
    public boolean onMenuItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.fab_new_match:
                Intent newMatchIntent = new Intent(mContext, UpdateBetActivity.class);
                newMatchIntent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_ADD_MATCH);
                startActivity(newMatchIntent);
                break;
            case R.id.fab_existing_match:
                Intent existingMatchIntent = new Intent(mContext, UpdateBetActivity.class);
                existingMatchIntent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_EXISTING_MATCH_BET);
                startActivity(existingMatchIntent);
                break;
        }
        return true;
    }

    @Override
    public void onMenuClosed() {

    }
}
