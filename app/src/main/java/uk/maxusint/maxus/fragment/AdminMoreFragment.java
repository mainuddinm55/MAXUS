package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.NoSuchElementException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.activity.IncomingRequestActivity;
import uk.maxusint.maxus.activity.LoginActivity;
import uk.maxusint.maxus.activity.PinActivity;
import uk.maxusint.maxus.activity.ProfitSharedActivity;
import uk.maxusint.maxus.activity.UserBettingActivity;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    public static final String TAG = "AccountFragment";
    private CompositeDisposable disposable = new CompositeDisposable();

    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.user_type_text_view)
    TextView userTypeTextView;
    @BindView(R.id.balance_text_view)
    TextView balanceTextView;
    private Context mContext;

    private ApiService apiService;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        apiService = ApiClient.getInstance().getApi();
        SharedPref sharedPref = new SharedPref(mContext);
        User currentUser = sharedPref.getUser();
        nameTextView.setText(currentUser.getName());
        int type = currentUser.getTypeId();

        disposable.add(
                apiService.getUserBalance(currentUser.getUserId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Double>() {
                            @Override
                            public void onSuccess(Double aDouble) {
                                String amount = String.valueOf(aDouble) + " $";
                                balanceTextView.setText(amount);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof NoSuchElementException) {
                                    Toast.makeText(mContext, "User invalid", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
        );
        String userType = null;
        switch (type) {
            case User.UserType.ROYAL:
                userType = "Royal";
                break;
            case User.UserType.CLASSIC:
                userType = "Classic";
                break;
            case User.UserType.PREMIUM:
                userType = "Premium";
                break;
            case User.UserType.AGENT:
                userType = "Agent";
                break;
            case User.UserType.CLUB:
                userType = "Club";
                break;
            case User.UserType.ADMIN:
                userType = "Admin";
                break;
        }
        userTypeTextView.setText(userType);
    }

    @OnClick(R.id.transaction_text_view)
    void transaction() {
        Intent intent = new Intent(mContext, IncomingRequestActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.pin_text_view)
    void pinClick() {
        startActivity(new Intent(mContext, PinActivity.class));
    }

    @OnClick(R.id.shared_user_text_view)
    void sharedUser() {
        startActivity(new Intent(mContext, ProfitSharedActivity.class));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @OnClick(R.id.current_user_betting_text_view)
    void currentUserBetting() {
        startActivity(new Intent(mContext, UserBettingActivity.class));
    }

    @OnClick(R.id.logout_text_view)
    void logout() {
        new SharedPref(mContext).clearUser();
        startActivity(new Intent(mContext, LoginActivity.class));
    }
}
