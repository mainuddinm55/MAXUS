package uk.maxusint.maxus.fragment;


import android.content.Context;
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
import uk.maxusint.maxus.activity.AgentHomeActivity;
import uk.maxusint.maxus.adapter.UserBetAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.model.UserBet;
import uk.maxusint.maxus.utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgentUserBettingFragment extends Fragment {
    public static final String TAG = "AgentUserBettingFragmen";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    private List<UserBet> userBetList = new ArrayList<>();
    private static AgentUserBettingFragment sInstance;

    @BindView(R.id.user_bets_recycler_view)
    RecyclerView userBetsRecyclerView;
    @BindView(R.id.no_user_betting_text_view)
    TextView noBettingTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private UserBetAdapter userBetAdapter;
    private Context mContext;

    public AgentUserBettingFragment() {
        // Required empty public constructor
    }

    public static synchronized AgentUserBettingFragment getInstance() {
        if (sInstance == null)
            sInstance = new AgentUserBettingFragment();
        return sInstance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agent_user_betting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        apiService = ApiClient.getInstance().getApi();
        ButterKnife.bind(this, view);

        userBetAdapter = new UserBetAdapter(userBetList);
        userBetsRecyclerView.setHasFixedSize(true);
        userBetsRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        userBetsRecyclerView.setAdapter(userBetAdapter);
        SharedPref sharedPref = new SharedPref(mContext);
        User user = sharedPref.getUser();
        Bundle typeBundle = getArguments();
        if (typeBundle != null) {
            int type = typeBundle.getInt(AgentHomeActivity.USER);
            switch (type) {
                case User.UserType.AGENT:
                    getAllUsersBettingByAgent(user.getUserId());
                    break;
                case User.UserType.CLUB:
                    getAllUsersBettingByClub(user.getUserId());
                    break;
            }
        }
    }

    private void getAllUsersBettingByAgent(int agentId) {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getUsersBetsByAgent(agentId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<UserBet>>() {
                            @Override
                            public void onSuccess(List<UserBet> userBets) {
                                progressBar.setVisibility(View.GONE);
                                userBetList.clear();
                                userBetList.addAll(userBets);
                                userBetAdapter.notifyDataSetChanged();
                                toggleNoBets();
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setVisibility(View.GONE);
                            }
                        })
        );
    }

    private void getAllUsersBettingByClub(int clubId) {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getUsersBetByClub(clubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<UserBet>>() {
                            @Override
                            public void onSuccess(List<UserBet> userBets) {
                                progressBar.setVisibility(View.GONE);
                                userBetList.clear();
                                userBetList.addAll(userBets);
                                userBetAdapter.notifyDataSetChanged();
                                Log.e(TAG, "onSuccess: " + userBets.size());
                                toggleNoBets();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                                progressBar.setVisibility(View.GONE);
                            }
                        })
        );
    }

    private void toggleNoBets() {
        if (userBetList.size() > 0) {
            noBettingTextView.setVisibility(View.GONE);
        } else {
            noBettingTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
