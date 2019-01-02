package uk.maxusint.maxus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.model.BetRate;
import uk.maxusint.maxus.network.response.MatchBetRateResponse;

public class MatchBetAdapter extends RecyclerView.Adapter<MatchBetAdapter.MatchBetHolder> {
    private static final String TAG = "MatchBetAdapter";
    private Context mContext;
    private List<MatchBetRateResponse.Match_> allMatches;
    private ItemClickListener itemClickListener;
    private String userType;

    public MatchBetAdapter(Context context, List<MatchBetRateResponse.Match_> allMatches, String userType) {
        this.allMatches = allMatches;
        mContext = context;
        this.userType = userType;
    }

    @NonNull
    @Override
    public MatchBetHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext().getApplicationContext())
                .inflate(R.layout.match_row_item, viewGroup, false);
        return new MatchBetHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MatchBetHolder matchBetHolder, final int i) {
        final MatchBetRateResponse.Match_ match = allMatches.get(i);
        if (match.getBets().size()==0){
            matchBetHolder.itemView.setVisibility(View.GONE);
        }
        matchBetHolder.questionRecyclerView.setHasFixedSize(true);
        matchBetHolder.questionRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        matchBetHolder.questionRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        BetAdapter adapter = new BetAdapter(mContext, match.getBets(),userType);
        matchBetHolder.questionRecyclerView.setAdapter(adapter);
        String matchTitle = match.getMatch().getTeam1() + " vs " + match.getMatch().getTeam2();
        matchBetHolder.matchTextView.setText(matchTitle);
        adapter.setItemClickListener(new BetAdapter.ItemClickListener() {
            @Override
            public void onBetClick(MatchBetRateResponse.Bet_ bet) {
                if (itemClickListener != null) {
                    itemClickListener.onBetClick(match, bet);
                }
            }

            @Override
            public void onRateClick(MatchBetRateResponse.Bet_ bet, BetRate betRate) {
                if (itemClickListener != null) {
                    itemClickListener.onBetRateClick(match, bet, betRate);
                }
            }

            @Override
            public void onFinishClick(MatchBetRateResponse.Bet_ bet_) {
                if (itemClickListener != null)
                    itemClickListener.onFinishClick(bet_);
            }

            @Override
            public void onCancelClick(MatchBetRateResponse.Bet_ bet_) {
                if (itemClickListener != null)
                    itemClickListener.onCancelClick(bet_);
            }
        });
        matchBetHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onMatchClick(match);
                }
            }
        });

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return allMatches.size();
    }

    public class MatchBetHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.match_text_view)
        TextView matchTextView;
        @BindView(R.id.question_recycler_view)
        RecyclerView questionRecyclerView;

        public MatchBetHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener {
        void onBetRateClick(MatchBetRateResponse.Match_ match_, MatchBetRateResponse.Bet_ bet_, BetRate betRate);

        void onBetClick(MatchBetRateResponse.Match_ match_, MatchBetRateResponse.Bet_ bet_);

        void onMatchClick(MatchBetRateResponse.Match_ match_);

        void onFinishClick(MatchBetRateResponse.Bet_ bet_);

        void onCancelClick(MatchBetRateResponse.Bet_ bet_);
    }
}
