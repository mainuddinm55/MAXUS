package uk.maxusint.maxus.fragment;


import android.content.Context;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.MatchAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Match;
import uk.maxusint.maxus.network.response.MatchResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinishMatchFragment extends Fragment {
    public static final String TAG = "FinishMatchFragment";
    private static FinishMatchFragment sInstance;
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    @BindView(R.id.finish_match_recycler_view)
    RecyclerView finishMatchRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.no_match_text_view)
    TextView noMatchTextView;
    private Context mContext;
    private List<Match> matchList = new ArrayList<>();
    private MatchAdapter matchAdapter;


    public FinishMatchFragment() {
        // Required empty public constructor
        Log.e(TAG, "FinishMatchFragment: " );
    }

    public static synchronized FinishMatchFragment getInstance() {
        Log.e(TAG, "getInstance: " );
        if (sInstance == null)
            sInstance = new FinishMatchFragment();
        return sInstance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: " );
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finish_match, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        Log.e(TAG, "onViewCreated: " );
        apiService = ApiClient.getInstance().getApi();

        finishMatchRecyclerView.setHasFixedSize(true);
        finishMatchRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        finishMatchRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        matchAdapter = new MatchAdapter(matchList);
        finishMatchRecyclerView.setAdapter(matchAdapter);
        getFinishMatch();

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
    private void getFinishMatch() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getAllFinishMatch()
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
