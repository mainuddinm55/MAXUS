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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
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
public class TradeBetsFragment extends Fragment {
    public static final String TAG = "TradeBetsFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    private static TradeBetsFragment sInstance;
    @BindView(R.id.classic_layout)
    RelativeLayout classicLayout;
    @BindView(R.id.classic_text_view)
    TextView classicTextView;
    @BindView(R.id.classic_recycler_view)
    RecyclerView classicRecyclerView;
    @BindView(R.id.royal_layout)
    RelativeLayout royalLayout;
    @BindView(R.id.royal_text_view)
    TextView royalTextView;
    @BindView(R.id.royal_recycler_view)
    RecyclerView royalRecyclerView;
    @BindView(R.id.fab_speed_dial)
    FabSpeedDial fabSpeedDial;
    @BindView(R.id.no_classic_bets_text_view)
    TextView noClassicBetsTextview;
    @BindView(R.id.no_royal_bets_text_view)
    TextView noRoyalBetsTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;


    private MatchBetAdapter classicAdapter;
    private MatchBetAdapter royalAdapter;
    private List<MatchBetRateResponse.Match_> royalMatchBets = new ArrayList<>();
    private List<MatchBetRateResponse.Match_> classicMatchBets = new ArrayList<>();

    private ApiService apiService;
    private Context mContext;
    private MatchBetAdapter.ItemClickListener classicListener = new MatchBetAdapter.ItemClickListener() {
        @Override
        public void onBetRateClick(MatchBetRateResponse.Match_ match_, MatchBetRateResponse.Bet_ bet_, BetRate betRate) {
            if (currentUser.getTypeId() == User.UserType.CLASSIC) {
                showPlaceBetDialog(match_, bet_, betRate);
            }
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

        }

        @Override
        public void onFinishClick(MatchBetRateResponse.Bet_ bet_) {

        }

        @Override
        public void onCancelClick(MatchBetRateResponse.Bet_ bet_) {

        }


    };
    private int betRateId = 0;

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
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        if (currentUser != null) {
            switch (currentUser.getTypeId()) {
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

                    }

                    dialog.dismiss();
                    placeBet(match_, bet_, betRate, amount);

                }
            }
        });

        dialog.show();
    }

    private void placeBet(MatchBetRateResponse.Match_ match_, final MatchBetRateResponse.Bet_ bet_, final BetRate betRate, final double amount) {
        final double returnAmount = (amount * betRate.getRate()) - amount;

        disposable.add(
                apiService.isUserAlreadyPlaceTradeBet(currentUser.getUserId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse defaultResponse) {
                                if (!defaultResponse.isError()) {
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
                                } else {
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

    private MatchBetAdapter.ItemClickListener royalListener = new MatchBetAdapter.ItemClickListener() {

        @Override
        public void onBetRateClick(MatchBetRateResponse.Match_ match_, MatchBetRateResponse.Bet_ bet_, BetRate betRate) {
            if (currentUser.getTypeId() == User.UserType.ROYAL) {
                showPlaceBetDialog(match_, bet_, betRate);
            }
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

        }

        @Override
        public void onFinishClick(final MatchBetRateResponse.Bet_ bet_) {
            if (currentUser.getTypeId() == User.UserType.ADMIN) {
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
                            updateBet(bet_, "Finish");
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
        }

        @Override
        public void onCancelClick(MatchBetRateResponse.Bet_ bet_) {

        }


    };
    private User currentUser;

    private void updateBet(final MatchBetRateResponse.Bet_ bet_, String result) {
        disposable.add(
                apiService.finishBet(
                        result,
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

    public TradeBetsFragment() {
        // Required empty public constructor
        Log.e(TAG, "TradeBetsFragment: ");
    }

    public static TradeBetsFragment getInstance() {
        if (sInstance == null)
            sInstance = new TradeBetsFragment();
        return sInstance;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_trade_bets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        Log.e(TAG, "onViewCreated: ");
        apiService = ApiClient.getInstance().getApi();

        classicRecyclerView.setHasFixedSize(true);
        royalRecyclerView.setHasFixedSize(true);


        SharedPref sharedPref = new SharedPref(mContext);
        currentUser = sharedPref.getUser();
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ROYAL) {
            royalLayout.setVisibility(View.GONE);
            classicLayout.setVisibility(View.GONE);
            royalRecyclerView.setVisibility(View.VISIBLE);
            classicRecyclerView.setVisibility(View.GONE);
            noClassicBetsTextview.setVisibility(View.GONE);
            noRoyalBetsTextView.setVisibility(View.GONE);

            royalRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            royalAdapter = new MatchBetAdapter(mContext, royalMatchBets, LoginActivity.NORMAL_TYPE);
            royalAdapter.setItemClickListener(royalListener);
            royalRecyclerView.setAdapter(royalAdapter);
            progressBar.setVisibility(View.VISIBLE);
            getAllRoyalBets();
        } else if (currentUser != null && currentUser.getTypeId() == User.UserType.CLASSIC) {
            royalLayout.setVisibility(View.GONE);
            classicLayout.setVisibility(View.GONE);
            royalRecyclerView.setVisibility(View.GONE);
            classicRecyclerView.setVisibility(View.VISIBLE);

            noClassicBetsTextview.setVisibility(View.GONE);
            noRoyalBetsTextView.setVisibility(View.GONE);

            classicRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            classicAdapter = new MatchBetAdapter(mContext, classicMatchBets, LoginActivity.NORMAL_TYPE);
            classicAdapter.setItemClickListener(classicListener);
            classicRecyclerView.setAdapter(classicAdapter);
            progressBar.setVisibility(View.VISIBLE);
            getAllClassicBets();
        } else if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            royalLayout.setVisibility(View.VISIBLE);
            classicLayout.setVisibility(View.VISIBLE);
            royalRecyclerView.setVisibility(View.VISIBLE);
            classicRecyclerView.setVisibility(View.VISIBLE);
            fabSpeedDial.setVisibility(View.VISIBLE);

            royalRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            royalAdapter = new MatchBetAdapter(mContext, royalMatchBets, LoginActivity.ADMIN_TYPE);
            royalAdapter.setItemClickListener(royalListener);

            classicRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            classicAdapter = new MatchBetAdapter(mContext, classicMatchBets, LoginActivity.ADMIN_TYPE);
            classicAdapter.setItemClickListener(classicListener);

            royalRecyclerView.setAdapter(royalAdapter);
            classicRecyclerView.setAdapter(classicAdapter);
            progressBar.setVisibility(View.VISIBLE);
            getAllRoyalBets();
            getAllClassicBets();
        }

        if (currentUser == null) {
            royalLayout.setVisibility(View.GONE);
            classicLayout.setVisibility(View.GONE);
            royalRecyclerView.setVisibility(View.VISIBLE);
            classicRecyclerView.setVisibility(View.GONE);
            noClassicBetsTextview.setVisibility(View.GONE);
            noRoyalBetsTextView.setVisibility(View.GONE);

            royalRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            royalAdapter = new MatchBetAdapter(mContext, royalMatchBets, LoginActivity.NORMAL_TYPE);
            royalAdapter.setItemClickListener(royalListener);
            royalRecyclerView.setAdapter(royalAdapter);
            progressBar.setVisibility(View.VISIBLE);
            getAllRoyalBets();
        }

        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
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
        });
    }

    @OnClick(R.id.see_all_classic_text_view)
    void seeAllClassicBetsClick() {
        if (classicMatchBets.size() > 0) {
            Intent intent = new Intent(mContext, UpdateBetActivity.class);
            intent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_ALL_CLASSIC_BETS);
            startActivity(intent);
        }
    }

    @OnClick(R.id.see_all_royal_text_view)
    void seeAllRoyalBetsClick() {
        if (royalMatchBets.size() > 0) {
            Intent intent = new Intent(mContext, UpdateBetActivity.class);
            intent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_ALL_ROYAL_BETS);
            startActivity(intent);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public void getAllClassicBets() {
        disposable.add(
                apiService.getBetRateWithMatchBetGroup(
                        Bet.BetMode.TRADE,
                        User.UserType.CLASSIC
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MatchBetRateResponse>() {
                            @Override
                            public void onSuccess(MatchBetRateResponse matchBetRateResponse) {
                                progressBar.setVisibility(View.GONE);
                                classicLayout.setVisibility(View.VISIBLE);
                                classicMatchBets.clear();
                                classicMatchBets.addAll(matchBetRateResponse.getMatches());
                                classicAdapter.notifyDataSetChanged();
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


    public void getAllRoyalBets() {
        disposable.add(
                apiService.getBetRateWithMatchBetGroup(
                        Bet.BetMode.TRADE,
                        User.UserType.ROYAL
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MatchBetRateResponse>() {
                            @Override
                            public void onSuccess(MatchBetRateResponse matchBetRateResponse) {
                                progressBar.setVisibility(View.GONE);
                                royalLayout.setVisibility(View.VISIBLE);
                                royalMatchBets.clear();
                                royalMatchBets.addAll(matchBetRateResponse.getMatches());
                                royalAdapter.notifyDataSetChanged();
                                toggleNoBets();
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void toggleNoBets() {
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ROYAL) {
            if (royalMatchBets.size() > 0) {
                noRoyalBetsTextView.setVisibility(View.GONE);
            } else {
                noRoyalBetsTextView.setVisibility(View.VISIBLE);
            }

        } else if (currentUser != null && currentUser.getTypeId() == User.UserType.CLASSIC) {
            if (classicMatchBets.size() > 0) {
                noClassicBetsTextview.setVisibility(View.GONE);
            } else {
                noClassicBetsTextview.setVisibility(View.VISIBLE);
            }
        } else if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            if (royalMatchBets.size() > 0) {
                noRoyalBetsTextView.setVisibility(View.GONE);
            } else {
                noRoyalBetsTextView.setVisibility(View.VISIBLE);
            }

            if (classicMatchBets.size() > 0) {
                noClassicBetsTextview.setVisibility(View.GONE);
            } else {
                noClassicBetsTextview.setVisibility(View.VISIBLE);
            }
        } else {
            if (royalMatchBets.size() > 0) {
                noRoyalBetsTextView.setVisibility(View.GONE);
            } else {
                noRoyalBetsTextView.setVisibility(View.VISIBLE);
            }
        }

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
}
