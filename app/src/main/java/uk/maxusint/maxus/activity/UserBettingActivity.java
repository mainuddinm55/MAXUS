package uk.maxusint.maxus.activity;

import android.app.ProgressDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import uk.maxusint.maxus.adapter.UserBetAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.model.UserBet;
import uk.maxusint.maxus.utils.SharedPref;

public class UserBettingActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "UserBettingActivity";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    @BindView(R.id.user_bets_recycler_view)
    RecyclerView userBetsRecyclerView;
    @BindView(R.id.no_user_betting_text_view)
    TextView noUserBettingTextView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private User currentUser;
    private UserBetAdapter userBetAdapter;
    private List<UserBet> userBetList = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_betting);
        ButterKnife.bind(this);

        apiService = ApiClient.getInstance().getApi();
        currentUser = new SharedPref(this).getUser();
        userBetAdapter = new UserBetAdapter(userBetList);
        userBetsRecyclerView.setHasFixedSize(true);
        userBetsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        userBetsRecyclerView.setAdapter(userBetAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            getCurrentlyUserBetting();
        }
    }

    private void getCurrentlyUserBetting() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getAllUserBets()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<UserBet>>() {
                            @Override
                            public void onSuccess(List<UserBet> userBets) {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                userBetList.clear();
                                userBetList.addAll(userBets);
                                userBetAdapter.notifyDataSetChanged();
                                toggleNoUserBets();
                            }

                            @Override
                            public void onError(Throwable e) {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "onError: " + e.getMessage());
                                e.printStackTrace();
                            }
                        })
        );
    }

    private void toggleNoUserBets() {
        if (userBetList.size() > 0) {
            noUserBettingTextView.setVisibility(View.GONE);
        } else {
            noUserBettingTextView.setVisibility(View.VISIBLE);
        }
    }

    private void showSpinner() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }

    private void dismissSpinner() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onRefresh() {
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            getCurrentlyUserBetting();
        }
    }
}
