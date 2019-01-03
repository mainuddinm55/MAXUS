package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenu;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.reactivex.disposables.CompositeDisposable;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.BetRateAdapter;
import uk.maxusint.maxus.listener.FragmentLoader;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Bet;
import uk.maxusint.maxus.network.model.BetRate;
import uk.maxusint.maxus.network.response.MatchBetRateResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateBetFragment extends Fragment implements BetRateAdapter.ItemClickListener {
    public static final String TAG = "UpdateBetFragment";
    public static final String BET = "uk.maxusint.maxus.fragment.BET";
    public static final String MATCH = "uk.maxusint.maxus.fragment.MATCH";
    public static final String BET_RATES = "uk.maxusint.maxus.fragment.BET_RATES";
    private CompositeDisposable disposable = new CompositeDisposable();
    private Context mContext;
    private FragmentLoader fragmentLoader;

    private MatchBetRateResponse.Bet_ bet_;
    private MatchBetRateResponse.Match_ match_;

    private ApiService apiService;

    @BindView(R.id.match_text_view)
    TextView matchTextView;
    @BindView(R.id.question_text_view)
    TextView questionTextView;
    @BindView(R.id.bet_mode_text_view)
    TextView betModeTextView;
    @BindView(R.id.status_text_view)
    TextView statusTextView;
    @BindView(R.id.option_recycler_view)
    RecyclerView optionRecyclerView;
    @BindView(R.id.fab_speed_dial)
    FabSpeedDial fabSpeedDial;


    public UpdateBetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_bet, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        fragmentLoader = (FragmentLoader) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        apiService = ApiClient.getInstance().getApi();
        Bundle bundle = getArguments();
        if (bundle != null) {
            bet_ = bundle.getParcelable(BET);
            match_ = bundle.getParcelable(MATCH);
            List<BetRate> betRates = bundle.getParcelableArrayList(BET_RATES);
            if (bet_ != null) {
                if (match_ != null) {
                    String matchTitle = match_.getMatch().getTeam1() + " vs " + match_.getMatch().getTeam2();
                    matchTextView.setText(matchTitle);
                }
                questionTextView.setText("Question: " + bet_.getBet().getQuestion());
                if (bet_.getBet().getBetMode() == 1) {
                    betModeTextView.setText("Bet Mode : Trade");
                } else if (bet_.getBet().getBetMode() == 2) {
                    betModeTextView.setText("Bet Mode : Advanced");
                } else if (bet_.getBet().getBetMode() == 3) {
                    betModeTextView.setText("Bet Mode : Both");
                }
                statusTextView.setText("Status: " + bet_.getBet().getResult());
                optionRecyclerView.setHasFixedSize(true);
                optionRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
                optionRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL));
                BetRateAdapter adapter = new BetRateAdapter(mContext, betRates);
                adapter.setItemClickListener(UpdateBetFragment.this);
                optionRecyclerView.setAdapter(adapter);
                fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
                    @Override
                    public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_add_bet:
                                CreateBetFragment createBetFragment = new CreateBetFragment();
                                Bundle betModeBundle = new Bundle();
                                betModeBundle.putInt(CreateBetFragment.BET_MODE, bet_.getBet().getBetMode());
                                createBetFragment.setArguments(betModeBundle);
                                fragmentLoader.loadFragment(createBetFragment, CreateBetFragment.TAG);
                                return true;
                            case R.id.action_add_bet_rate:
                                if (bet_ != null) {
                                    Bundle betBundle = new Bundle();
                                    betBundle.putParcelable(SetBetRateFragment.BET, bet_.getBet());
                                    SetBetRateFragment fragment = new SetBetRateFragment();
                                    fragment.setArguments(betBundle);
                                    fragmentLoader.loadFragment(fragment, SetBetRateFragment.TAG);
                                }
                                return true;
                            case R.id.action_update:
                                if (bet_ != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(CreateBetFragment.BET, bet_);
                                    bundle.putParcelable(CreateBetFragment.MATCH, match_.getMatch());
                                    CreateBetFragment fragment = new CreateBetFragment();
                                    fragment.setArguments(bundle);
                                    fragmentLoader.loadFragment(fragment, CreateBetFragment.TAG);
                                }
                                return true;
                            case R.id.action_delete:
                                if (bet_ != null) {

                                }
                                return true;
                            default:
                                return false;
                        }

                    }

                    @Override
                    public void onMenuClosed() {

                    }
                });
            }
        }

    }


    @Override
    public void onClick(BetRate betRate) {
        Toast.makeText(mContext, betRate.getOptions(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClick(final BetRate betRate) {
        String[] actions = new String[]{"Update", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Select option");
        builder.setItems(actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Bundle betRateBundle = new Bundle();
                    betRateBundle.putParcelable(SetBetRateFragment.BET, bet_.getBet());
                    betRateBundle.putParcelable(SetBetRateFragment.BET_RATE, betRate);
                    SetBetRateFragment fragment = new SetBetRateFragment();
                    fragment.setArguments(betRateBundle);
                    fragmentLoader.loadFragment(fragment, SetBetRateFragment.TAG);
                } else if (which == 1) {
                    Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }
}
