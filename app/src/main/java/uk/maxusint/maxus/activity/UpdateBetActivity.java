package uk.maxusint.maxus.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.maxusint.maxus.R;
import uk.maxusint.maxus.fragment.AddMatchFragment;
import uk.maxusint.maxus.fragment.AllClassicBetFragment;
import uk.maxusint.maxus.fragment.AllRoyalBetFragment;
import uk.maxusint.maxus.fragment.CreateBetFragment;
import uk.maxusint.maxus.fragment.UpcomingMatchFragment;
import uk.maxusint.maxus.fragment.UpdateBetFragment;
import uk.maxusint.maxus.fragment.UpdateUserFragment;
import uk.maxusint.maxus.listener.FragmentLoader;
import uk.maxusint.maxus.network.model.Bet;

public class UpdateBetActivity extends AppCompatActivity implements FragmentLoader {

    public static final String BET = "uk.maxusint.maxus.activity.BET";
    public static final String MATCH = "uk.maxusint.maxus.activity.MATCH";
    public static final String BET_RATES = "uk.maxusint.maxus.activity.BET_RATES";
    public static final String ACTION = "uk.maxusint.maxus.activity.ACTION";
    public static final String ACTION_UPDATE_BET = "uk.maxusint.maxus.activity.ACTION_UPDATE_BET";
    public static final String ACTION_ALL_CLASSIC_BETS = "uk.maxusint.maxus.activity.ACTION_ALL_CLASSIC_BETS";
    public static final String ACTION_ALL_ROYAL_BETS = "uk.maxusint.maxus.activity.ACTION_ALL_ROYAL_BETS";
    public static final String ACTION_ADD_MATCH = "uk.maxusint.maxus.activity.ACTION_ADD_MATCH";
    public static final String ACTION_UPDATE_USER = "uk.maxusint.maxus.activity.ACTION_UPDATE_USER";
    public static final String ACTION_EXISTING_MATCH_BET = "uk.maxusint.maxus.activity.ACTION_EXISTING_MATCH_BET";
    public static final String ACTION_CREATE_NEW_BET = "uk.maxusint.maxus.activity.ACTION_CREATE_NEW_BET";
    public static final String ACTION_ADD_AGENT = "uk.maxusint.maxus.activity.ACTION_ADD_AGENT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bet);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String action = bundle.getString(ACTION);
            if (action != null) {
                switch (action) {
                    case ACTION_UPDATE_BET:
                        UpdateBetFragment fragment = new UpdateBetFragment();
                        Bundle betBundle = new Bundle();
                        betBundle.putParcelable(UpdateBetFragment.BET, bundle.getParcelable(BET));
                        betBundle.putParcelable(UpdateBetFragment.MATCH, bundle.getParcelable(MATCH));
                        betBundle.putParcelableArrayList(UpdateBetFragment.BET_RATES, bundle.getParcelableArrayList(BET_RATES));
                        fragment.setArguments(betBundle);
                        loadFragment(fragment, UpdateBetFragment.TAG);
                        break;
                    case ACTION_ALL_CLASSIC_BETS:
                        AllClassicBetFragment classicBetFragment = new AllClassicBetFragment();
                        Bundle betModeBundle = new Bundle();
                        betModeBundle.putInt(AllClassicBetFragment.BET_MODE, Bet.BetMode.TRADE);
                        classicBetFragment.setArguments(betModeBundle);
                        loadFragment(classicBetFragment, AllClassicBetFragment.TAG);
                        break;
                    case ACTION_ALL_ROYAL_BETS:
                        AllRoyalBetFragment royalBetFragment = new AllRoyalBetFragment();
                        Bundle betModeBundl = new Bundle();
                        betModeBundl.putInt(AllRoyalBetFragment.BET_MODE, Bet.BetMode.TRADE);
                        royalBetFragment.setArguments(betModeBundl);
                        loadFragment(royalBetFragment, AllRoyalBetFragment.TAG);
                        break;
                    case ACTION_ADD_MATCH:
                        loadFragment(new AddMatchFragment(), AddMatchFragment.TAG);
                        break;
                    case ACTION_UPDATE_USER:
                        UpdateUserFragment updateUserFragment = new UpdateUserFragment();
                        Bundle userBundle = new Bundle();
                        userBundle.putParcelable(UpdateUserFragment.USER, bundle.getParcelable(UpdateUserFragment.USER));
                        updateUserFragment.setArguments(userBundle);
                        loadFragment(updateUserFragment, UpdateUserFragment.TAG);
                        break;
                    case ACTION_EXISTING_MATCH_BET:
                        Bundle actionBundle = new Bundle();
                        actionBundle.putString(UpcomingMatchFragment.ACTION, UpcomingMatchFragment.ACTION_CREATE_BET);
                        UpcomingMatchFragment upcomingMatchFragment = new UpcomingMatchFragment();
                        upcomingMatchFragment.setArguments(actionBundle);
                        loadFragment(upcomingMatchFragment, UpcomingMatchFragment.TAG);
                        break;
                    case ACTION_CREATE_NEW_BET:
                        Bundle matchBundle = new Bundle();
                        matchBundle.putParcelable(CreateBetFragment.MATCH, bundle.getParcelable(MATCH));
                        CreateBetFragment createBetFragment= new CreateBetFragment();
                        createBetFragment.setArguments(matchBundle);
                        loadFragment(createBetFragment,CreateBetFragment.TAG);
                        break;
                }
            }
        }

    }

    @Override
    public void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment, tag);
        ft.commit();
    }
}
