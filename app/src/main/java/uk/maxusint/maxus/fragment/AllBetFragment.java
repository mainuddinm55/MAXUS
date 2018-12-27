package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.MatchBetAdapter;
import uk.maxusint.maxus.listener.FragmentLoader;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.BetRate;
import uk.maxusint.maxus.network.response.MatchBetRateResponse;
import uk.maxusint.maxus.network.response.MatchResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllBetFragment extends Fragment implements MatchBetAdapter.ItemClickListener {
    private static final String TAG = "AllBetFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    private FragmentLoader fragmentLoader;
    private Context mContext;
    @BindView(R.id.all_bets_recycler_view)
    RecyclerView allBetsRecyclerView;
    private ApiService apiService;

    public AllBetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_bet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        allBetsRecyclerView.setHasFixedSize(true);
        allBetsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        apiService = ApiClient.getInstance().getApi();

        getAllMatches();


    }

    private void getAllMatches() {
        disposable.add(
                apiService.getBetRateWithMatchBetGroup()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MatchBetRateResponse>() {
                            @Override
                            public void onSuccess(MatchBetRateResponse matchBetRateResponse) {
                                MatchBetAdapter adapter = new MatchBetAdapter(mContext, matchBetRateResponse.getMatches());
                                allBetsRecyclerView.setAdapter(adapter);
                                adapter.setItemClickListener(AllBetFragment.this);
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
        betBundle.putParcelable(UpdateBetFragment.BET,bet_);
        betBundle.putParcelable(UpdateBetFragment.MATCH,match_);
        UpdateBetFragment fragment = new UpdateBetFragment();
        fragment.setArguments(betBundle);
        fragmentLoader.loadFragment(fragment,UpdateBetFragment.TAG);
        //Toast.makeText(mContext, bet_.getBet().getQuestion(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMatchClick(MatchBetRateResponse.Match_ match_) {
        //Toast.makeText(getContext(), match_.getMatch().getTeam1(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        fragmentLoader = (FragmentLoader) context;
    }
}
