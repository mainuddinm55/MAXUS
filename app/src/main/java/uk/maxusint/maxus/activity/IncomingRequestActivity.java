package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.IncomingRequestAdapter;
import uk.maxusint.maxus.adapter.TransactionAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Transaction;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.DefaultResponse;
import uk.maxusint.maxus.utils.SharedPref;

public class IncomingRequestActivity extends AppCompatActivity {
    public static final String TAG = "IncomingRequestActivity";
    private CompositeDisposable disposable = new CompositeDisposable();
    @BindView(R.id.incoming_request_recycler_view)
    RecyclerView incomingRequestRecyclerView;

    IncomingRequestAdapter requestAdapter;
    private ApiService apiService;
    private List<Transaction> transactionList = new ArrayList<>();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_request);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        requestAdapter = new IncomingRequestAdapter(this, transactionList);
        incomingRequestRecyclerView.setHasFixedSize(true);
        incomingRequestRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        incomingRequestRecyclerView.setAdapter(requestAdapter);

        apiService = ApiClient.getInstance().getApi();

        SharedPref sharedPref = new SharedPref(this);
        user = sharedPref.getUser();
        getAllRequestedTransaction();

        requestAdapter.setItemClickListener(new TransactionAdapter.ItemClickListener() {
            @Override
            public void onClick(final Transaction transaction) {
                disposable.add(
                        apiService.setToUserSeen(transaction.getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                                    @Override
                                    public void onSuccess(DefaultResponse defaultResponse) {
                                        if (transaction.getStatus().equalsIgnoreCase(uk.maxusint.maxus.utils.Transaction.Status.REQUEST_SEND)) {
                                            updateTransactionStatus(uk.maxusint.maxus.utils.Transaction.Status.PENDING, transaction.getId());
                                        }
                                        Intent intent = new Intent(IncomingRequestActivity.this, IncomingRequestDetailsActivity.class);
                                        intent.putExtra(IncomingRequestDetailsActivity.TRANSACTION, transaction);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(TAG, "onError: " + e.getMessage());
                                    }
                                })
                );

            }
        });
    }

    private void updateTransactionStatus(String status, int id) {
        disposable.add(
                apiService.updateTransactionStatus(status, id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Transaction>() {
                            @Override
                            public void onSuccess(Transaction transaction) {
                                Toast.makeText(IncomingRequestActivity.this, transaction.getStatus(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAllRequestedTransaction() {
        disposable.add(
                apiService.getAllRequestedTransaction(user.getUsername())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Transaction>>() {
                            @Override
                            public void onSuccess(List<Transaction> transactions) {
                                transactionList.clear();
                                transactionList.addAll(transactions);
                                requestAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }
}
