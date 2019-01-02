package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.MatchSpinnerAdapter;
import uk.maxusint.maxus.listener.FragmentLoader;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Bet;
import uk.maxusint.maxus.network.model.Match;
import uk.maxusint.maxus.network.response.AllBetResponse;
import uk.maxusint.maxus.network.response.BetResponse;
import uk.maxusint.maxus.network.response.MatchBetRateResponse;
import uk.maxusint.maxus.network.response.MatchResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateBetFragment extends Fragment {
    public static final String MATCH = "uk.maxusint.maxus.fragment.MATCH";
    public static final String BET = "uk.maxusint.maxus.fragment.BET";
    public static final String TAG = "CreateBetFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    @BindView(R.id.existing_match_layout)
    LinearLayout existingMatchLayout;
    @BindView(R.id.inserted_match_layout)
    LinearLayout insertedMatchLayout;
    @BindView(R.id.match_spinner)
    Spinner matchSpinner;
    @BindView(R.id.match_text_view)
    TextView matchTextView;
    @BindView(R.id.question_edit_text)
    TextInputEditText questionEditText;
    @BindView(R.id.bet_mode_spinner)
    Spinner betModeSpinner;
    @BindView(R.id.bet_mode_text_view)
    TextView betModeTextView;
    @BindView(R.id.inserted_bet_mode_layout)
    LinearLayout insertedBetModeLayout;
    @BindView(R.id.update_bet_mode_layout)
    LinearLayout updateBetModeLayout;
    @BindView(R.id.create_bet_btn)
    Button createBetBtn;
    MatchBetRateResponse.Bet_ bet_;

    private Unbinder unbinder;
    private Context mContext;

    private String[] betModes = new String[]{"Select bet mode", "Both", "Trade", "Advanced"};

    private int betMode;
    private int matchId;
    String date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date(System.currentTimeMillis()));

    private ApiService apiService;
    private FragmentLoader fragmentLoader;

    public CreateBetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_bet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        apiService = ApiClient.getInstance().getApi();

        final ArrayAdapter<String> betModeAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdown_spinner_item, R.id.text_view_list_item, betModes);
        betModeSpinner.setAdapter(betModeAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            existingMatchLayout.setVisibility(View.GONE);
            insertedMatchLayout.setVisibility(View.VISIBLE);
            insertedBetModeLayout.setVisibility(View.GONE);
            updateBetModeLayout.setVisibility(View.VISIBLE);
            Match match = bundle.getParcelable(MATCH);
            bet_ = bundle.getParcelable(BET);
            if (bet_ != null) {
                matchId = match.getId();
                String insertedMatch = match.getTeam1() + " vs " + match.getTeam2();
                matchTextView.setText(insertedMatch);
                betMode = bet_.getBet().getBetMode();
                if (bet_.getBet().getBetMode() == Bet.BetMode.TRADE) {
                    betModeTextView.setText("Trade");
                } else if (bet_.getBet().getBetMode() == Bet.BetMode.ADVANCED) {
                    betModeTextView.setText("Advanced");
                }
                questionEditText.setText(bet_.getBet().getQuestion());
                createBetBtn.setText("Update");
            } else {
                matchId = match.getId();
                String insertedMatch = match.getTeam1() + " vs " + match.getTeam2();
                matchTextView.setText(insertedMatch);
            }

        } else {
            insertedMatchLayout.setVisibility(View.GONE);
            existingMatchLayout.setVisibility(View.VISIBLE);
            insertedBetModeLayout.setVisibility(View.VISIBLE);
            updateBetModeLayout.setVisibility(View.GONE);
            disposable.add(
                    apiService.getAllRunningResponse()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<MatchResponse>() {
                                @Override
                                public void onSuccess(final MatchResponse matchResponse) {
                                    Log.e(TAG, "onSuccess: " + matchResponse.getMatches().size());
                                    setMatchSpinnerAdapter(matchResponse.getMatches());

                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            })
            );
        }


        betModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    switch ((String) parent.getItemAtPosition(position)) {
                        case "Both":
                            betMode = 3;
                            break;
                        case "Trade":
                            betMode = 1;
                            break;
                        case "Advanced":
                            betMode = 2;
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        disposable.add(
                apiService.getAllBets()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllBetResponse>() {
                            @Override
                            public void onSuccess(AllBetResponse allBetResponse) {
                                Log.e(TAG, "onSuccess: " + allBetResponse.getBets().size());
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        );

    }

    private void setMatchSpinnerAdapter(final List<Match> matches) {
        MatchSpinnerAdapter adapter = new MatchSpinnerAdapter(mContext, matches);
        matchSpinner.setAdapter(adapter);
        matchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    matchId = matches.get(position - 1).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        fragmentLoader = (FragmentLoader) context;
    }

    @OnClick(R.id.create_bet_btn)
    void setBet() {
        if (TextUtils.isEmpty(questionEditText.getText())) {
            questionEditText.setError("Question required");
            questionEditText.requestFocus();
            return;
        }
        if (betMode == 0) {
            Toast.makeText(mContext, "Please select bet mode", Toast.LENGTH_SHORT).show();
            return;
        }
        if (matchId == 0) {
            Toast.makeText(mContext, "Please select match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bet_ != null) {
            updateBet();
        } else {
            createBet();
        }
    }

    private void updateBet() {
        disposable.add(
                apiService.updateBet(
                        questionEditText.getText().toString(),
                        matchId,
                        betMode,
                        bet_.getBet().getBetId()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BetResponse>() {
                            @Override
                            public void onSuccess(BetResponse betResponse) {
                                if (!betResponse.isErr()) {
                                    Toast.makeText(mContext, betResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void createBet() {
        if (betMode == 3) {
            disposable.add(
                    apiService.createBet(
                            questionEditText.getText().toString(),
                            date,
                            matchId,
                            Bet.BetMode.TRADE
                    ).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<BetResponse>() {
                                @Override
                                public void onSuccess(BetResponse responseBody) {
                                    if (!responseBody.isErr()) {
                                        disposable.add(
                                                apiService.createBet(
                                                        questionEditText.getText().toString(),
                                                        date,
                                                        matchId,
                                                        Bet.BetMode.ADVANCED
                                                ).subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribeWith(new DisposableSingleObserver<BetResponse>() {
                                                            @Override
                                                            public void onSuccess(BetResponse responseBody) {
                                                                if (!responseBody.isErr()) {
                                                                    Toast.makeText(mContext, responseBody.getMsg(), Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(mContext, responseBody.getMsg(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(Throwable e) {
                                                                Log.e(TAG, "onError: " + e.getMessage());
                                                            }
                                                        })
                                        );
                                    } else {
                                        Toast.makeText(mContext, responseBody.getMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, "onError: " + e.getMessage());
                                }
                            })
            );
        } else {
            disposable.add(
                    apiService.createBet(
                            questionEditText.getText().toString(),
                            date,
                            matchId,
                            betMode
                    ).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<BetResponse>() {
                                @Override
                                public void onSuccess(BetResponse responseBody) {
                                    if (!responseBody.isErr()) {
                                        showSetBetRateDialog(responseBody.getBet());
                                    } else {
                                        Toast.makeText(mContext, responseBody.getMsg(), Toast.LENGTH_SHORT).show();
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

    private void showSetBetRateDialog(final Bet bet) {
        AlertDialog.Builder betRateDialog = new AlertDialog.Builder(mContext);
        betRateDialog.setTitle("Set Bet_ Rate");
        betRateDialog.setMessage("Do you want to set bet rate?");
        betRateDialog.setCancelable(false);
        betRateDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putParcelable(SetBetRateFragment.BET, bet);
                SetBetRateFragment fragment = new SetBetRateFragment();
                fragment.setArguments(bundle);
                fragmentLoader.loadFragment(fragment, "setbetrate");
            }
        });
        betRateDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        betRateDialog.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        betRateDialog.show();
    }
}
