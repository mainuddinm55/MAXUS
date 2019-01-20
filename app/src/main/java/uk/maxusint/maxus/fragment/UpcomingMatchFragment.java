package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import uk.maxusint.maxus.listener.FragmentLoader;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Match;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.MatchResponse;
import uk.maxusint.maxus.utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingMatchFragment extends Fragment {
    public static final String TAG = "UpcomingMatchFragment";
    public static final String ACTION = "uk.maxusint.maxus.fragment.ACTION";
    public static final String ACTION_CREATE_BET = "uk.maxusint.maxus.fragment.ACTION_CREATE_BET";
    private static UpcomingMatchFragment sInstance;
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    @BindView(R.id.upcoming_match_recycler_view)
    RecyclerView upcomingMatchRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.no_match_text_view)
    TextView noMatchTextView;
    @BindView(R.id.add_match)
    FloatingActionButton addMatch;
    private Context mContext;
    private List<Match> matchList = new ArrayList<>();
    private MatchAdapter matchAdapter;
    private String action;

    public UpcomingMatchFragment() {
        // Required empty public constructor
        Log.e(TAG, "UpcomingMatchFragment: ");
    }

    public static synchronized UpcomingMatchFragment getInstance() {
        Log.e(TAG, "getInstance: ");
        if (sInstance == null)
            sInstance = new UpcomingMatchFragment();
        return sInstance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming_match, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        apiService = ApiClient.getInstance().getApi();
        Log.e(TAG, "onViewCreated: ");
        upcomingMatchRecyclerView.setHasFixedSize(true);
        upcomingMatchRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        upcomingMatchRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        matchAdapter = new MatchAdapter(matchList);
        upcomingMatchRecyclerView.setAdapter(matchAdapter);
        User currentUser = new SharedPref(mContext).getUser();
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            addMatch.show();
        } else {
            addMatch.hide();
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            action = bundle.getString(ACTION);
        }
        matchAdapter.setItemClickListener(new MatchAdapter.ItemClickListener() {
            @Override
            public void onClick(Match match) {
                if (action != null && action.equals(ACTION_CREATE_BET)) {
                    Intent intent = new Intent(mContext, UpdateBetActivity.class);
                    intent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_CREATE_NEW_BET);
                    intent.putExtra(UpdateBetActivity.MATCH, match);
                    startActivity(intent);
                }
            }
        });
        getRunningMatch();

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
