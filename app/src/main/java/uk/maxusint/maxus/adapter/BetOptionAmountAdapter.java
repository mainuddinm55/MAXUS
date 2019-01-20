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
import uk.maxusint.maxus.network.model.BetOptionCalculation;

public class BetOptionAmountAdapter extends RecyclerView.Adapter<BetOptionAmountAdapter.BetOptionAmountHolder> {
    private List<BetOptionCalculation> calculations = new ArrayList<>();

    @NonNull
    @Override
    public BetOptionAmountHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.option_total_bet_row_item, viewGroup, false);
        BetOptionAmountHolder holder = new BetOptionAmountHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BetOptionAmountHolder betOptionAmountHolder, int i) {
        betOptionAmountHolder.bindTo(calculations.get(i));
    }

    @Override
    public int getItemCount() {
        return calculations.size();
    }

    public void setCalculations(List<BetOptionCalculation> calculations) {
        this.calculations = calculations;
        notifyDataSetChanged();
    }

    class BetOptionAmountHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.option_text_view)
        TextView optionTextView;
        @BindView(R.id.amount_text_view)
        TextView amountTextView;
        @BindView(R.id.return_amount_text_view)
        TextView returnAmountTextView;

        public BetOptionAmountHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTo(BetOptionCalculation calculation) {
            optionTextView.setText(calculation.getBetOption());
            String amount = calculation.getBetAmount() + " $";
            String returnAmount = calculation.getBetReturnAmount() + " $";
            amountTextView.setText(amount);
            returnAmountTextView.setText(returnAmount);
        }
    }
}
