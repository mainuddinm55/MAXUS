package uk.maxusint.maxus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.activity.LoginActivity;
import uk.maxusint.maxus.network.model.BetRate;
import uk.maxusint.maxus.network.response.MatchBetRateResponse;

public class BetAdapter extends RecyclerView.Adapter<BetAdapter.BetHolder> {
    private static final String TAG = "BetAdapter";
    private Context mContext;
    private List<MatchBetRateResponse.Bet_> bets = new ArrayList<>();
    private ItemClickListener itemClickListener;
    private String userType;

    public BetAdapter(Context mContext, List<MatchBetRateResponse.Bet_> bets, String userType) {
        this.mContext = mContext;
        this.bets = bets;
        this.userType = userType;
    }

    @NonNull
    @Override
    public BetHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext().getApplicationContext())
                .inflate(R.layout.bet_row_item, viewGroup, false);
        return new BetHolder(itemView);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull final BetHolder betHolder, final int i) {
        betHolder.allBetsRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        betHolder.allBetsRecyclerView.setHasFixedSize(true);
        //betHolder.allBetsRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL));
        final MatchBetRateResponse.Bet_ bet = bets.get(i);
        BetRateAdapter adapter = new BetRateAdapter(mContext, bet.getBetRates());
        betHolder.allBetsRecyclerView.setAdapter(adapter);
        betHolder.questionTextView.setText(bet.getBet().getQuestion());
        betHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onBetClick(bet);
                }
            }
        });
        betHolder.finishBetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.onFinishClick(bet);
            }
        });
        betHolder.cancelBetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.onCancelClick(bet);
            }
        });
        adapter.setItemClickListener(new BetRateAdapter.ItemClickListener() {
            @Override
            public void onClick(BetRate betRate) {
                if (itemClickListener != null) {
                    itemClickListener.onRateClick(bet, betRate);
                }
            }

            @Override
            public void onLongClick(BetRate betRate) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return bets.size();
    }


    public class BetHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.question_text_view)
        TextView questionTextView;
        @BindView(R.id.option_recycler_view)
        RecyclerView allBetsRecyclerView;
        @BindView(R.id.finish_bet_btn)
        Button finishBetBtn;
        @BindView(R.id.cancel_bet_btn)
        Button cancelBetBtn;

        public BetHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (userType.equals(LoginActivity.NORMAL_TYPE)) {
                finishBetBtn.setVisibility(View.GONE);
                cancelBetBtn.setVisibility(View.GONE);
            }
        }
    }

    public interface ItemClickListener {
        void onBetClick(MatchBetRateResponse.Bet_ bet);

        void onRateClick(MatchBetRateResponse.Bet_ bet, BetRate betRate);

        void onFinishClick(MatchBetRateResponse.Bet_ bet_);

        void onCancelClick(MatchBetRateResponse.Bet_ bet_);
    }
}
