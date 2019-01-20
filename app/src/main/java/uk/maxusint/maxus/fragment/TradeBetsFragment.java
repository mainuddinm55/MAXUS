package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenu;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import uk.maxusint.maxus.network.model.BetRate;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.MatchBetRateResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class TradeBetsFragment extends Fragment {
    public static final String TAG = "TradeBetsFragment";
    public static final String BET_MODE = "BET_MODE";
    private CompositeDisposable disposable = new CompositeDisposable();
    private static TradeBetsFragment sInstance;
    @BindView(R.id.classic_text_view)
    TextView classicTextView;
    @BindView(R.id.classic_recycler_view)
    RecyclerView classicRecyclerView;
    @BindView(R.id.royal_text_view)
    TextView royalTextView;
    @BindView(R.id.royal_recycler_view)
    RecyclerView royalRecyclerView;
    @BindView(R.id.fab_speed_dial)
    FabSpeedDial fabSpeedDial;

    private ApiService apiService;
    private Context mContext;
    private int betMode;
    private MatchBetAdapter.ItemClickListener classicListener = new MatchBetAdapter.ItemClickListener() {
        @Override
        public void onBetRateClick(MatchBetRateResponse.Match_ match_, MatchBetRateResponse.Bet_ bet_, BetRate betRate) {

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
    private MatchBetAdapter.ItemClickListener royalListener = new MatchBetAdapter.ItemClickListener() {
        @Override
        public void onBetRateClick(MatchBetRateResponse.Match_ match_, MatchBetRateResponse.Bet_ bet_, BetRate betRate) {

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
        classicRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        royalRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        Bundle betModeBundle = getArguments();
        if (betModeBundle != null) {
            betMode = betModeBundle.getInt(BET_MODE);
            getAllClassicBets();
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
                        Intent intent = new Intent(mContext, UpdateBetActivity.class);
                        intent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_EXISTING_MATCH_BET);
                        startActivity(intent);
                        break;
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @OnClick(R.id.see_all_classic_text_view)
    public void seeAllClassicBets() {
        Intent intent = new Intent(mContext, UpdateBetActivity.class);
        intent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_ALL_CLASSIC_BETS);
        startActivity(intent);
    }

    @OnClick(R.id.see_all_royal_text_view)
    public void seeAllRoyalBets() {
        Intent intent = new Intent(mContext, UpdateBetActivity.class);
        intent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_ALL_ROYAL_BETS);
        startActivity(intent);
    }

    public void getAllClassicBets() {
        disposable.add(
                apiService.getBetRateWithMatchBetGroup(
                        betMode,
                        User.UserType.CLASSIC
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MatchBetRateResponse>() {
                            @Override
                            public void onSuccess(MatchBetRateResponse matchBetRateResponse) {
                                MatchBetAdapter adapter = new MatchBetAdapter(mContext, matchBetRateResponse.getMatches(), LoginActivity.ADMIN_TYPE);
                                classicRecyclerView.setAdapter(adapter);
                                adapter.setItemClickListener(classicListener);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }


    public void getAllRoyalBets() {
        disposable.add(
                apiService.getBetRateWithMatchBetGroup(
                        betMode,
                        User.UserType.ROYAL
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MatchBetRateResponse>() {
                            @Override
                            public void onSuccess(MatchBetRateResponse matchBetRateResponse) {
                                MatchBetAdapter adapter = new MatchBetAdapter(mContext, matchBetRateResponse.getMatches(), LoginActivity.ADMIN_TYPE);
                                royalRecyclerView.setAdapter(adapter);
                                adapter.setItemClickListener(royalListener);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
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
