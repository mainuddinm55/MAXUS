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
import io.reactivex.disposables.CompositeDisposable;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.model.UserBetHistory;

public class UserBetHistoryAdapter extends RecyclerView.Adapter<UserBetHistoryAdapter.HistoryHolder> {
    private List<UserBetHistory> betHistoryList = new ArrayList<>();

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_bet_history_row_item, viewGroup, false);
        HistoryHolder historyHolder = new HistoryHolder(view);
        return historyHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder historyHolder, int i) {
        historyHolder.bindTo(betHistoryList.get(i));
    }

    @Override
    public int getItemCount() {
        return betHistoryList.size();
    }

    public void setBetHistoryList(List<UserBetHistory> betHistoryList) {
        this.betHistoryList = betHistoryList;
        notifyDataSetChanged();
    }

    class HistoryHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.date_text_view)
        TextView dateTextView;
        @BindView(R.id.month_text_view)
        TextView monthTextView;
        @BindView(R.id.question_text_view)
        TextView questionTextView;
        @BindView(R.id.amount_text_view)
        TextView amountTextView;
        @BindView(R.id.return_amount_text_view)
        TextView returnAmountTextView;
        @BindView(R.id.status_text_view)
        TextView statusTextView;

        HistoryHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTo(UserBetHistory history) {
            dateTextView.setText(getDateFromString(history.getBetDate()));
            monthTextView.setText(getMonthFromString(history.getBetDate()));
            questionTextView.setText(history.getQuestion());
            String amount = "Bet Amount: " + history.getBetAmount() + "$";
            String returnAmount = "Return Amount: " + history.getBetReturnAmount() + "$";
            amountTextView.setText(amount);
            returnAmountTextView.setText(returnAmount);
            statusTextView.setText(history.getResult());
            if (history.getResult().equalsIgnoreCase("Won")) {
                statusTextView.setTextColor(Color.GREEN);
            } else if (history.getResult().equalsIgnoreCase("Loss")) {
                statusTextView.setTextColor(Color.RED);
            }
        }

        private String getDateFromString(String dateString) {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            try {
                Date date = dateTimeFormat.parse(dateString);
                SimpleDateFormat timeFormat = new SimpleDateFormat("dd", Locale.US);
                return timeFormat.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String getMonthFromString(String dateString) {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            try {
                Date date = dateTimeFormat.parse(dateString);
                SimpleDateFormat timeFormat = new SimpleDateFormat("MMM", Locale.US);
                return timeFormat.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
