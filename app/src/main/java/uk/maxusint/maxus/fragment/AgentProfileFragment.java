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

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.activity.AccountActivity;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgentProfileFragment extends Fragment {
    public static final String TAG = "AgentProfileFragment";
    private static AgentProfileFragment sInstance;

    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.username_text_view)
    TextView usernameTextView;
    @BindView(R.id.mobile_text_view)
    TextView mobileTextView;
    @BindView(R.id.email_text_view)
    TextView emailTextView;
    @BindView(R.id.address_text_view)
    TextView addressTextView;

    private Context mContext;

    public AgentProfileFragment() {
        // Required empty public constructor
    }

    public static synchronized AgentProfileFragment getInstance() {
        if (sInstance == null)
            sInstance = new AgentProfileFragment();
        return sInstance;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agent_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        SharedPref sharedPref = new SharedPref(mContext);
        User user = sharedPref.getUser();

        if (user != null) {
            nameTextView.setText(user.getName());
            usernameTextView.setText(user.getUsername());
            mobileTextView.setText(user.getMobile());
            emailTextView.setText(user.getEmail());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(user.getDistrict());
            if (!user.getUpazilla().isEmpty()) {
                stringBuilder.append(", " + user.getUpazilla());
            }
            if (!user.getUp().isEmpty()) {
                stringBuilder.append(", " + user.getUp());
            }
            addressTextView.setText(stringBuilder.toString());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @OnClick(R.id.account_text_view)
    void openAccountActivity(){
        Intent intent = new Intent(mContext, AccountActivity.class);
        startActivity(intent);

    }
}
