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
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
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
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.MatchResponse;
import uk.maxusint.maxus.utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class RunningMatchFragment extends Fragment {
    public static final String TAG = "RunningMatchFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    @BindView(R.id.running_match_recycler_view)
    RecyclerView runningMatchRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.no_match_text_view)
    TextView noMatchTextView;
    @BindView(R.id.add_match)
    FloatingActionButton addMatch;

    private static RunningMatchFragment sInstance;
    private Context mContext;
    private List<Match> matchList = new ArrayList<>();
    private MatchAdapter matchAdapter;

    private User currentUser;

    public RunningMatchFragment() {
        Log.e(TAG, "RunningMatchFragment: ");
        // Required empty public constructor
    }

    public static synchronized RunningMatchFragment getInstance() {
        Log.e(TAG, "getInstance: ");
        if (sInstance == null)
            sInstance = new RunningMatchFragment();
        return sInstance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_running_match, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        Log.e(TAG, "onViewCreated: ");
        apiService = ApiClient.getInstance().getApi();

        runningMatchRecyclerView.setHasFixedSize(true);
        runningMatchRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        runningMatchRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        matchAdapter = new MatchAdapter(matchList);
        runningMatchRecyclerView.setAdapter(matchAdapter);
        SharedPref sharedPref = new SharedPref(mContext);
        currentUser = sharedPref.getUser();
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            addMatch.show();
        }else {
            addMatch.hide();
        }
        getAllRunningMatch();

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

    private void getAllRunningMatch() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getAllRunningMatch()
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

    @OnClick(R.id.add_match)
    void addMatch() {
        Intent intent = new Intent(mContext, UpdateBetActivity.class);
        intent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_ADD_MATCH);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
