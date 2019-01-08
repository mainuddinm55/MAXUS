package uk.maxusint.maxus.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateUserFragment extends Fragment {
    public static final String USER = "uk.maxusint.maxus.fragment.USER";
    public static final String TAG = "UpdateUserFragment";
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.username_text_view)
    TextView usernameTextView;
    @BindView(R.id.email_text_view)
    TextView emailTextView;
    @BindView(R.id.mobile_text_view)
    TextView mobileTextView;
    @BindView(R.id.reference_text_view)
    TextView referenceTextView;
    @BindView(R.id.agent_text_view)
    TextView agentTextView;
    @BindView(R.id.district_text_view)
    TextView districtTextView;
    @BindView(R.id.balance_text_view)
    TextView balanceTextView;
    @BindView(R.id.rank_text_view)
    TextView rankTextView;
    @BindView(R.id.active_inactive_btn)
    Button activeInactiveBtn;

    public UpdateUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(view);
        Bundle userBundle = getArguments();
        if (userBundle != null) {
            User user = userBundle.getParcelable(USER);
            if (user != null) {
                nameTextView.setText(user.getName());
                usernameTextView.setText(user.getUsername());
                emailTextView.setText(user.getEmail());
                mobileTextView.setText(user.getMobile());
                referenceTextView.setText(user.getReference());
                agentTextView.setText(String.valueOf(user.getAgentId()));
                districtTextView.setText(user.getDistrict());
                balanceTextView.setText(String.valueOf(user.getTotalBalance()));
                rankTextView.setText(String.valueOf(user.getRankId()));
                if (user.getStatus().equals("Active")) {
                    activeInactiveBtn.setText("Deactivate");
                } else {
                    activeInactiveBtn.setText("Activate");
                }
            }
        }
    }
}
