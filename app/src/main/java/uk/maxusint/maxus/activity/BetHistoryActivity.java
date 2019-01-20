package uk.maxusint.maxus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.UserBetHistoryAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.model.UserBetHistory;
import uk.maxusint.maxus.utils.SharedPref;

public class BetHistoryActivity extends AppCompatActivity {
    private static final String TAG = "BetHistoryActivity";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    @BindView(R.id.bet_history_recycler_view)
    RecyclerView betHistoryRecyclerView;
    @BindView(R.id.no_bets_history)
    TextView noBetsHistory;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private List<UserBetHistory> betHistoryList = new ArrayList<>();
    private UserBetHistoryAdapter betHistoryAdapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bet_history);
        ButterKnife.bind(this);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        currentUser = new SharedPref(this).getUser();
        apiService = ApiClient.getInstance().getApi();

        betHistoryRecyclerView.setHasFixedSize(true);
        betHistoryAdapter = new UserBetHistoryAdapter();
        betHistoryRecyclerView.setAdapter(betHistoryAdapter);

        if (currentUser != null) {
            getAllUserBetHistory();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAllUserBetHistory() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getUserBetHistory(currentUser.getUserId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<UserBetHistory>>() {
                            @Override
                            public void onSuccess(List<UserBetHistory> userBetHistories) {
                                progressBar.setVisibility(View.GONE);
                                betHistoryList.clear();
                                betHistoryList.addAll(userBetHistories);
                                betHistoryAdapter.setBetHistoryList(betHistoryList);
                                toggleNoHistory();
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

    private void toggleNoHistory() {
        if (betHistoryList.size() > 0) {
            noBetsHistory.setVisibility(View.GONE);
        } else {
            noBetsHistory.setVisibility(View.VISIBLE);
        }
    }
}
