package uk.maxusint.maxus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.model.Transaction;
import uk.maxusint.maxus.utils.CircleTransform;

public class IncomingRequestAdapter extends RecyclerView.Adapter<IncomingRequestAdapter.IncomingRequestHolder> {
    private Context context;
    private List<Transaction> transactionList;
    private TransactionAdapter.ItemClickListener itemClickListener;

    public IncomingRequestAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public IncomingRequestAdapter.IncomingRequestHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext().getApplicationContext())
                .inflate(R.layout.incoming_request_row_item, viewGroup, false);
        final IncomingRequestHolder requestHolder = new IncomingRequestHolder(view);
        requestHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(transactionList.get(requestHolder.getAdapterPosition()));
                }
            }
        });
        return requestHolder;
    }

    public void setItemClickListener(TransactionAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull IncomingRequestAdapter.IncomingRequestHolder requestHolder, int i) {
        requestHolder.bindTo(transactionList.get(i));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public interface ItemClickListener {
        void onClick(Transaction transaction);
    }

    class IncomingRequestHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.date_text_view)
        TextView dateTextView;
        @BindView(R.id.username_text_view)
        TextView usernameTextView;
        @BindView(R.id.amount_text_view)
        TextView amountTextView;
        @BindView(R.id.status_text_view)
        TextView statusTextView;
        @BindView(R.id.request_type_icon)
        ImageView requestTypeImageView;

        IncomingRequestHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTo(Transaction transaction) {
            if (transaction.getToUserSeen() > 0) {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.seenColor));
            }else {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.unseenColor));
            }
            String amount = transaction.getAmount() + " $";
            dateTextView.setText(getDateTimeFromString(transaction.getTransDate()));
            usernameTextView.setText(transaction.getFromUsername());
            amountTextView.setText(amount);

            if (transaction.getStatus().toLowerCase().equals(uk.maxusint.maxus.utils.Transaction.Status.PENDING.toLowerCase())) {
                statusTextView.setTextColor(Color.YELLOW);
                statusTextView.setText("Review");
            } else if (transaction.getStatus().toLowerCase().equals(uk.maxusint.maxus.utils.Transaction.Status.REQUEST_SEND.toLowerCase())) {
                statusTextView.setTextColor(Color.RED);
                statusTextView.setText("Request");
            } else if (transaction.getStatus().toLowerCase().equals(uk.maxusint.maxus.utils.Transaction.Status.SUCCESS.toLowerCase())) {
                statusTextView.setTextColor(Color.GREEN);
                statusTextView.setText("Approved");
            }

            switch (transaction.getTransType()) {
                case uk.maxusint.maxus.utils.Transaction.Type.TypeString.DEPOSIT:
                    Glide.with(context).load(R.drawable.deposit)
                            .apply(RequestOptions.bitmapTransform(new CircleTransform()))
                            .thumbnail(0.5f)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .into(requestTypeImageView);
                    break;
                case uk.maxusint.maxus.utils.Transaction.Type.TypeString.WITHDRAW:
                    Glide.with(context).load(R.drawable.withdraw)
                            .apply(RequestOptions.bitmapTransform(new CircleTransform()))
                            .thumbnail(0.5f)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .into(requestTypeImageView);
                    break;
                case uk.maxusint.maxus.utils.Transaction.Type.TypeString.BALANCE_TRANSFER:
                    Glide.with(context).load(R.drawable.balance_transfer)
                            .apply(RequestOptions.bitmapTransform(new CircleTransform()))
                            .thumbnail(0.5f)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .into(requestTypeImageView);
                    break;

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
