package uk.maxusint.maxus.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.model.Commission;

public class CommissionAdapter extends RecyclerView.Adapter<CommissionAdapter.CommissionHolder> {
    private List<Commission> commissionList = new ArrayList<>();

    @NonNull
    @Override
    public CommissionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.commission_row_item, viewGroup, false);
        return new CommissionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommissionHolder commissionHolder, int i) {
        commissionHolder.bindTo(commissionList.get(i));
    }

    @Override
    public int getItemCount() {
        return commissionList.size();
    }

    public void setCommissionList(List<Commission> commissionList) {
        this.commissionList = commissionList;
        notifyDataSetChanged();
    }

    class CommissionHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.date_text_view)
        TextView dateTextView;
        @BindView(R.id.amount_text_view)
        TextView amountTextView;
        @BindView(R.id.purpose_text_view)
        TextView purposeTextView;

        CommissionHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTo(Commission commission) {
            dateTextView.setText(getDateFromString(commission.getCommDate()));
            amountTextView.setText(String.format("%s$", commission.getAmount()));
            purposeTextView.setText(commission.getPurpose());
        }

        private String getDateFromString(String dateString) {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            try {
                Date date = dateTimeFormat.parse(dateString);
                SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.US);
                return timeFormat.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
