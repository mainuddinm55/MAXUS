package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
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
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.SharedUserAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.SharedUser;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

public class ProfitSharedActivity extends AppCompatActivity implements SharedUserAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ProfitSharedActivity";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.users_recycler_view)
    RecyclerView usersRecyclerView;
    @BindView(R.id.no_user_text_view)
    TextView noUserTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private User currentUser;

    private List<SharedUser> sharedUserList = new ArrayList<>();
    private SharedUserAdapter sharedUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit_shared);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        apiService = ApiClient.getInstance().getApi();

        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        sharedUserAdapter = new SharedUserAdapter(sharedUserList);
        usersRecyclerView.setAdapter(sharedUserAdapter);
        sharedUserAdapter.setItemClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(this);
        currentUser = new SharedPref(this).getUser();
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            getAllSharedUser();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAllSharedUser() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getAllSharedUser()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<SharedUser>>() {
                            @Override
                            public void onSuccess(List<SharedUser> sharedUsers) {
                                progressBar.setVisibility(View.GONE);
                                swipeRefreshLayout.setRefreshing(false);
                                sharedUserList.clear();
                                sharedUserList.addAll(sharedUsers);
                                sharedUserAdapter.notifyDataSetChanged();
                                toggleNoUsers();
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setVisibility(View.GONE);
                                swipeRefreshLayout.setRefreshing(false);
                                e.printStackTrace();
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void toggleNoUsers() {
        if (sharedUserList.size() > 0) {
            noUserTextView.setVisibility(View.GONE);
        } else {
            noUserTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(SharedUser user) {
        startActivity(new Intent(this, UpdateUserActivity.class).putExtra(UpdateUserActivity.SHARED_USER, user));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onRefresh() {
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            getAllSharedUser();
        }
    }

    @OnClick(R.id.fab_add)
    void addSharedUser() {
        startActivity(new Intent(this, AddEditSharedUserActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
