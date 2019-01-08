package uk.maxusint.maxus.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import uk.maxusint.maxus.network.model.Transaction;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> {
    private List<Transaction> transactionList ;
    private ItemClickListener itemClickListener;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext().getApplicationContext())
                .inflate(R.layout.transaction_row_item, viewGroup, false);
        final TransactionHolder transactionHolder = new TransactionHolder(view);
        transactionHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(transactionList.get(transactionHolder.getAdapterPosition()));
                }
            }
        });
        return transactionHolder;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder transactionHolder, int i) {
        transactionHolder.bindTo(transactionList.get(i));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public interface ItemClickListener {
        void onClick(Transaction transaction);
    }

    class TransactionHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.date_text_view)
        TextView dateTextView;
        @BindView(R.id.username_text_view)
        TextView usernameTextView;
        @BindView(R.id.amount_text_view)
        TextView amountTextView;
        @BindView(R.id.status_text_view)
        TextView statusTextView;

        TransactionHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTo(Transaction transaction) {
            String date = "Date: " + getDateTimeFromString(transaction.getTransDate());
            String username = "To Username: " + transaction.getToUsername();
            String amount = transaction.getAmount() + " $";
            dateTextView.setText(date);
            usernameTextView.setText(username);
            amountTextView.setText(amount);
            statusTextView.setText(transaction.getStatus());

            if (transaction.getStatus().toLowerCase().equals(uk.maxusint.maxus.utils.Transaction.Status.PENDING.toLowerCase())) {
                statusTextView.setTextColor(Color.YELLOW);
            } else if (transaction.getStatus().toLowerCase().equals(uk.maxusint.maxus.utils.Transaction.Status.REQUEST_SEND.toLowerCase())) {
                statusTextView.setTextColor(Color.RED);
            } else if (transaction.getStatus().toLowerCase().equals(uk.maxusint.maxus.utils.Transaction.Status.SUCCESS.toLowerCase())){
                statusTextView.setTextColor(Color.GREEN);
            }
        }


        private String getDateTimeFromString(String dateString) {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            try {
                Date date = dateTimeFormat.parse(dateString);
                SimpleDateFormat timeFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aaa", Locale.US);
                return timeFormat.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

    }
}
