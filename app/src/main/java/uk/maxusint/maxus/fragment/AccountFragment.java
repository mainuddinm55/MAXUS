package uk.maxusint.maxus.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.activity.BalanceTransferActivity;
import uk.maxusint.maxus.activity.DepositActivity;
import uk.maxusint.maxus.activity.WithdrawActivity;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "AccountFragment";

    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.user_type_text_view)
    TextView userTypeTextView;
    @BindView(R.id.balance_text_view)
    TextView balanceTextView;
    @BindView(R.id.deposit_text_view)
    TextView depositTextView;
    @BindView(R.id.withdraw_text_view)
    TextView withdrawTextView;
    @BindView(R.id.balance_transfer_text_view)
    TextView balanceTransferTextView;

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

        SharedPref sharedPref = new SharedPref(getContext());
        User currentUser = sharedPref.getUser();
        nameTextView.setText(currentUser.getName());
        int type = currentUser.getTypeId();
       
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
        balanceTextView.setText(String.valueOf(currentUser.getTotalBalance()));

        depositTextView.setOnClickListener(this);
        withdrawTextView.setOnClickListener(this);
        balanceTransferTextView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deposit_text_view:
                Intent depositIntent = new Intent(getContext(), DepositActivity.class);
                startActivity(depositIntent);
                break;
            case R.id.withdraw_text_view:
                Intent withdrawIntent = new Intent(getContext(), WithdrawActivity.class);
                startActivity(withdrawIntent);
                break;
            case R.id.balance_transfer_text_view:
                Intent balanceTransferIntent = new Intent(getContext(), BalanceTransferActivity.class);
                startActivity(balanceTransferIntent);
                break;
        }
    }
}
