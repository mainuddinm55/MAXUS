package uk.maxusint.maxus.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.TransactionAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Transaction;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class DepositHistoryFragment extends Fragment {
    public static final String TAG = "DepositHistoryFragment";
    public static final String ACTION = "uk.maxusint.maxus.fragment.ACTION";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;

    @BindView(R.id.deposit_history_recycler_view)
    RecyclerView depositHistoryRecyclerView;
    private List<Transaction> transactionList = new ArrayList<>();
    private TransactionAdapter transactionAdapter;
    private int action;
    private User currentUser;

    public DepositHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deposit_history, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        Log.e(TAG, "onViewCreated: ");

        SharedPref sharedPref = new SharedPref(getContext());
        currentUser = sharedPref.getUser();
        Bundle transBundle = getArguments();
        if (transBundle != null) {
            action = transBundle.getInt(ACTION);
        }
        apiService = ApiClient.getInstance().getApi();
        transactionAdapter = new TransactionAdapter(transactionList);
        depositHistoryRecyclerView.setHasFixedSize(true);
        depositHistoryRecyclerView.addItemDecoration(new DividerItemDecoration(getContext().getApplicationContext(), DividerItemDecoration.VERTICAL));
        depositHistoryRecyclerView.setAdapter(transactionAdapter);

        switch (action) {
            case uk.maxusint.maxus.utils.Transaction.Type.DEPOSIT_HISTORY:
                getAllDepositTransaction();
                break;
            case uk.maxusint.maxus.utils.Transaction.Type.WITHDRAW_HISTORY:
                getAllWithdrawTransaction();
                break;
            case uk.maxusint.maxus.utils.Transaction.Type.BALANCE_TRANSFER_HISTORY:
                getAllBalanceTransferTransaction();
                break;
        }

    }

    private void getAllDepositTransaction() {
        disposable.add(
                apiService.getAllDepositTransaction(currentUser.getUsername())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Transaction>>() {
                            @Override
                            public void onSuccess(List<Transaction> transactions) {
                                transactionList.clear();
                                transactionList.addAll(transactions);
                                transactionAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void getAllWithdrawTransaction() {
        disposable.add(
                apiService.getAllWithDrawTransaction(currentUser.getUsername())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Transaction>>() {
                            @Override
                            public void onSuccess(List<Transaction> transactions) {
                                transactionList.clear();
                                transactionList.addAll(transactions);
                                transactionAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void getAllBalanceTransferTransaction() {
        disposable.add(
                apiService.getAllBalanceTransferTransaction(currentUser.getUsername())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Transaction>>() {
                            @Override
                            public void onSuccess(List<Transaction> transactions) {
                                transactionList.clear();
                                transactionList.addAll(transactions);
                                transactionAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }
}
