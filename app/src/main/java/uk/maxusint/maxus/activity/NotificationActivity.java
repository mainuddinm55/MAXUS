package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.NotificationAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Notification;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.DefaultResponse;
import uk.maxusint.maxus.utils.SharedPref;

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = "NotificationActivity";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    private List<Notification> notificationList = new ArrayList<>();
    @BindView(R.id.notification_recycler_view)
    RecyclerView notificationRecyclerView;
    @BindView(R.id.no_notification_text_view)
    TextView noNotificationTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private NotificationAdapter adapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        apiService = ApiClient.getInstance().getApi();
        SharedPref sharedPref = new SharedPref(this);
        currentUser = sharedPref.getUser();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        notificationRecyclerView.setHasFixedSize(true);
        notificationRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new NotificationAdapter(NotificationActivity.this, notificationList);
        notificationRecyclerView.setAdapter(adapter);

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

    @Override
    protected void onStart() {
        super.onStart();
        getUserNotifications();

        adapter.setItemClickListener(new NotificationAdapter.ItemClickListener() {
            @Override
            public void onClick(Notification notification) {
                seenNotification(notification);
                Intent intent = new Intent(NotificationActivity.this, NotificationDetailsActivity.class);
                intent.putExtra(NotificationDetailsActivity.NOTIFICATION, notification);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void seenNotification(Notification notification) {
        disposable.add(
                apiService.seenNotification(notification.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse response) {
                                Log.e(TAG, "onSuccess: " + response.getMessage());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void getUserNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getUserNotification(currentUser.getUsername())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Notification>>() {
                            @Override
                            public void onSuccess(List<Notification> notifications) {
                                progressBar.setVisibility(View.GONE);
                                notificationList.clear();
                                notificationList.addAll(notifications);
                                adapter.notifyDataSetChanged();
                                toggleNoNotification();
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void toggleNoNotification() {
        if (notificationList.size() > 0) {
            noNotificationTextView.setVisibility(View.GONE);
        } else {
            noNotificationTextView.setVisibility(View.VISIBLE);
        }
    }
}
