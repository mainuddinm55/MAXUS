package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.activity.UpdateBetActivity;
import uk.maxusint.maxus.adapter.MatchAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Match;
import uk.maxusint.maxus.network.response.MatchResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingMatchFragment extends Fragment {
    public static final String TAG = "RunningMatchFragment";
    private static UpcomingMatchFragment sInstance;
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    @BindView(R.id.upcoming_match_recycler_view)
    RecyclerView upcomingMatchRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.no_match_text_view)
    TextView noMatchTextView;

    private Context mContext;
    private List<Match> matchList = new ArrayList<>();
    private MatchAdapter matchAdapter;

    public UpcomingMatchFragment() {
        // Required empty public constructor
    }

    public static synchronized UpcomingMatchFragment getInstance() {
        if (sInstance == null)
            sInstance = new UpcomingMatchFragment();
        return sInstance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming_match, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        apiService = ApiClient.getInstance().getApi();

        upcomingMatchRecyclerView.setHasFixedSize(true);
        upcomingMatchRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        upcomingMatchRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        matchAdapter = new MatchAdapter(matchList);
        upcomingMatchRecyclerView.setAdapter(matchAdapter);
        getRunningMatch();

    }

    @OnClick(R.id.add_match)
    void addMatch() {
        Intent intent = new Intent(mContext, UpdateBetActivity.class);
        intent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_ADD_MATCH);
        startActivity(intent);
    }

    private void getRunningMatch() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getAllUpcomingMatch()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MatchResponse>() {
                            @Override
                            public void onSuccess(MatchResponse matchResponse) {
                                progressBar.setVisibility(View.GONE);
                                matchList.clear();
                                matchList.addAll(matchResponse.getMatches());
                                matchAdapter.notifyDataSetChanged();
                                toggleNoMatch();
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void toggleNoMatch() {
        if (matchList.size() > 0) {
            noMatchTextView.setVisibility(View.GONE);
        } else {
            noMatchTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
