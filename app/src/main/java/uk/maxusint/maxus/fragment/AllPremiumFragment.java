package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.activity.LoginActivity;
import uk.maxusint.maxus.adapter.MatchBetAdapter;
import uk.maxusint.maxus.listener.FragmentLoader;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.BetRate;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.BetResponse;
import uk.maxusint.maxus.network.response.MatchBetRateResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllPremiumFragment extends Fragment implements MatchBetAdapter.ItemClickListener {
    public static final String TAG = "AllPremiumFragment";
    public static final String BET_MODE = "BET_MODE";
    private CompositeDisposable disposable = new CompositeDisposable();
    private FragmentLoader fragmentLoader;
    private Context mContext;
    @BindView(R.id.all_bets_recycler_view)
    RecyclerView allBetsRecyclerView;
    private ApiService apiService;
    private int betRateId = 0;
    private int betMode;

    public AllPremiumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_premium, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        allBetsRecyclerView.setHasFixedSize(true);
        allBetsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        apiService = ApiClient.getInstance().getApi();
        Bundle betModeBundle = getArguments();
        if (betModeBundle != null) {
            betMode = betModeBundle.getInt(BET_MODE);
        }
        getAllMatches();
    }

    private void getAllMatches() {
        disposable.add(
                apiService.getBetRateWithMatchBetGroup(betMode, User.UserType.PREMIUM)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MatchBetRateResponse>() {
                            @Override
                            public void onSuccess(MatchBetRateResponse matchBetRateResponse) {
                                if (matchBetRateResponse.getMatches().size() > 0) {
                                    MatchBetAdapter adapter = new MatchBetAdapter(mContext, matchBetRateResponse.getMatches(), LoginActivity.ADMIN_TYPE);
                                    allBetsRecyclerView.setAdapter(adapter);
                                    adapter.setItemClickListener(AllPremiumFragment.this);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        );
    }

    @Override
    public void onBetRateClick(MatchBetRateResponse.Match_ match_, MatchBetRateResponse.Bet_ bet_, BetRate betRate) {
        Toast.makeText(mContext, betRate.getOptions(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBetClick(MatchBetRateResponse.Match_ match_, MatchBetRateResponse.Bet_ bet_) {
        Bundle betBundle = new Bundle();
        betBundle.putParcelable(UpdateBetFragment.BET, bet_);
        betBundle.putParcelable(UpdateBetFragment.MATCH, match_);
        UpdateBetFragment fragment = new UpdateBetFragment();
        fragment.setArguments(betBundle);
        fragmentLoader.loadFragment(fragment, UpdateBetFragment.TAG);
        //Toast.makeText(mContext, bet_.getBet().getQuestion(), Toast.LENGTH_SHORT).show();
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
        fragmentLoader = (FragmentLoader) context;
    }
}
