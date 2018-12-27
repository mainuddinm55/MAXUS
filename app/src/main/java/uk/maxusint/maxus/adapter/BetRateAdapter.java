package uk.maxusint.maxus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.model.BetRate;

public class BetRateAdapter extends RecyclerView.Adapter<BetRateAdapter.BetRateHolder> {
    private Context mContext;
    private List<BetRate> betRates;
    private ItemClickListener itemClickListener;

    public BetRateAdapter(Context context, List<BetRate> betRates) {
        this.mContext = context;
        this.betRates = betRates;
    }

    @NonNull
    @Override
    public BetRateHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext().getApplicationContext())
                .inflate(R.layout.bet_option_row_item, viewGroup, false);
        return new BetRateHolder(view);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull BetRateHolder betRateHolder, int i) {
        final BetRate betRate = betRates.get(i);
        String optionWithRate = (i + 1) + ". " + betRate.getOptions() + " ( " + betRate.getRate() + " )";
        betRateHolder.optionTextView.setText(optionWithRate);
        betRateHolder.optionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(betRate);
                }
            }
        });

        betRateHolder.optionTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onLongClick(betRate);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return betRates.size();
    }

    public class BetRateHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.option_text_view)
        TextView optionTextView;

        public BetRateHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener {
        void onClick(BetRate betRate);

        void onLongClick(BetRate betRate);
    }
}
