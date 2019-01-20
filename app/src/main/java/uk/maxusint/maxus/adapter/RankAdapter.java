package uk.maxusint.maxus.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.model.Rank;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankHolder> {
    private List<Rank> rankList = new ArrayList<>();

    @NonNull
    @Override
    public RankHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rank_row_item, viewGroup, false);
        RankHolder rankHolder = new RankHolder(view);
        rankHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return rankHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankHolder rankHolder, int i) {
        rankHolder.bindTo(rankList.get(i));
    }

    @Override
    public int getItemCount() {
        return rankList.size();
    }

    public void setRankList(List<Rank> rankList) {
        this.rankList = rankList;
        notifyDataSetChanged();
    }

    class RankHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rank_text_view)
        TextView rankTextView;
        @BindView(R.id.assert_need_text_view)
        TextView assertNeedTextView;
        @BindView(R.id.bonus_text_view)
        TextView bonusTextView;

        RankHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTo(Rank rank) {
            rankTextView.setText(rank.getRankName());
            assertNeedTextView.setText(String.valueOf(rank.getAssertNeed()));
            bonusTextView.setText(String.format("%s$", String.valueOf(rank.getBonus())));
        }
    }
}
