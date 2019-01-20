package uk.maxusint.maxus.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.activity.AddAgentActivity;
import uk.maxusint.maxus.activity.BetHistoryActivity;
import uk.maxusint.maxus.activity.BonusActivity;
import uk.maxusint.maxus.activity.LoginActivity;
import uk.maxusint.maxus.activity.PinActivity;
import uk.maxusint.maxus.activity.UpdateUserActivity;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

public class MoreFragment extends Fragment {
    private static final String TAG = "MoreFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;

    @BindView(R.id.bet_history_text_view)
    TextView betHistoryTextView;
    @BindView(R.id.agent_text_view)
    TextView agentTextView;
    @BindView(R.id.login_text_view)
    TextView loginTextView;
    @BindView(R.id.logout_text_view)
    TextView logoutTextView;
    @BindView(R.id.pin_text_view)
    TextView pinTextView;
    @BindView(R.id.add_agent_text_view)
    TextView addAgentTextView;
    private Context mContext;
    private User agent;


    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        apiService = ApiClient.getInstance().getApi();
        User currentUser = new SharedPref(mContext).getUser();
        if (currentUser != null) {
            switch (currentUser.getTypeId()) {
                case User.UserType.CLUB:
                    getAgentById(1);
                    addAgentTextView.setVisibility(View.VISIBLE);
                    agentTextView.setText(R.string.admin_text);
                    betHistoryTextView.setVisibility(View.GONE);
                    break;
                case User.UserType.AGENT:
                    getAgentById(currentUser.getClubId());
                    betHistoryTextView.setVisibility(View.GONE);
                    agentTextView.setText(R.string.club_text);
                    break;
                case User.UserType.CLASSIC:
                case User.UserType.PREMIUM:
                case User.UserType.ROYAL:
                    pinTextView.setVisibility(View.GONE);
                    getAgentById(currentUser.getAgentId());
                    agentTextView.setText(R.string.agent_text);
                    break;
            }
            loginTextView.setVisibility(View.GONE);
            logoutTextView.setVisibility(View.VISIBLE);
        } else {
            loginTextView.setVisibility(View.VISIBLE);
            logoutTextView.setVisibility(View.GONE);

        }
    }

    @OnClick(R.id.login_text_view)
    void gotoLoginActivity() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.logout_text_view)
    void logout() {
        new SharedPref(mContext).clearUser();
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.bet_history_text_view)
    void betHistory() {
        Intent intent = new Intent(mContext, BetHistoryActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.bonus_text_view)
    void bonus() {
        Intent intent = new Intent(mContext, BonusActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.agent_text_view)
    void myAgent() {
        Intent intent = new Intent(mContext, UpdateUserActivity.class);
        if (agent != null) {
            intent.putExtra(UpdateUserActivity.USER, agent);
        }
        startActivity(intent);
    }

    @OnClick(R.id.pin_text_view)
    void gotoPinActivity() {
        startActivity(new Intent(mContext, PinActivity.class));
    }

    @OnClick(R.id.add_agent_text_view)
    void addAgent() {
        Intent intent = new Intent(mContext, AddAgentActivity.class);
        startActivity(intent);
    }

    private void getAgentById(int id) {
        disposable.add(
                apiService.getUserById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<User>() {
                            @Override
                            public void onSuccess(User user) {
                                agent = user;
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }
}
