package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.CommissionAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Commission;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class BonusFragment extends Fragment {
    private static final String TAG = "BonusFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    private Context mContext;
    @BindView(R.id.commission_recycler_view)
    RecyclerView commissionsRecyclerView;
    @BindView(R.id.no_commission_text_view)
    TextView noCommissionTextView;
    @BindView(R.id.total_bonus_text_view)
    TextView totalBonusTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private List<Commission> commissionList = new ArrayList<>();
    private CommissionAdapter commissionAdapter;
    private MenuItem menuItem;

    public BonusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bonus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        apiService = ApiClient.getInstance().getApi();

        commissionAdapter = new CommissionAdapter();
        commissionAdapter.setCommissionList(commissionList);
        commissionsRecyclerView.setHasFixedSize(true);
        commissionsRecyclerView.setAdapter(commissionAdapter);

        User currentUser = new SharedPref(mContext).getUser();
        if (currentUser != null) {
            getAllCommission(currentUser.getUsername());
            getUserBonus(currentUser.getUsername());
        }
    }

    private void getUserBonus(String username) {
        disposable.add(
                apiService.getUserBonus(username)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Double>() {
                            @Override
                            public void onSuccess(Double aDouble) {
                                totalBonusTextView.setText(String.format("Bonus: %s$", aDouble));
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                                e.printStackTrace();
                            }
                        })
        );
    }

    private void getAllCommission(String username) {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getCommissionByUsername(username)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Commission>>() {
                            @Override
                            public void onSuccess(List<Commission> commissions) {
                                progressBar.setVisibility(View.GONE);
                                commissionList.clear();
                                commissionList.addAll(commissions);
                                commissionAdapter.setCommissionList(commissionList);
                                toggleNoCommission();
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

    private void toggleNoCommission() {
        if (commissionList.size() > 0) {
            noCommissionTextView.setVisibility(View.GONE);
        } else {
            noCommissionTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
