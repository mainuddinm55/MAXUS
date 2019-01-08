package uk.maxusint.maxus.adapter;

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
import uk.maxusint.maxus.network.model.Bet;
import uk.maxusint.maxus.network.model.UserBet;

public class UserBetAdapter extends RecyclerView.Adapter<UserBetAdapter.UserBetHolder> {
    private List<UserBet> userBetList;
    private ItemClickListener itemClickListener;

    public UserBetAdapter(List<UserBet> userBetList) {
        this.userBetList = userBetList;
    }

    @NonNull
    @Override
    public UserBetHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext().getApplicationContext())
                .inflate(R.layout.user_bet_row_item, viewGroup, false);
        final UserBetHolder userBetHolder = new UserBetHolder(view);
        userBetHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(userBetList.get(userBetHolder.getAdapterPosition()));
                }
            }
        });
        return userBetHolder;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull UserBetHolder userBetHolder, int i) {
        userBetHolder.bindTo(userBetList.get(i));
    }

    @Override
    public int getItemCount() {
        return userBetList.size();
    }

    public interface ItemClickListener {
        void onClick(UserBet userBet);
    }

    class UserBetHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.username_text_view)
        TextView usernameTextView;
        @BindView(R.id.question_text_view)
        TextView questionTextView;
        @BindView(R.id.bet_mode_text_view)
        TextView betModeTextView;
        @BindView(R.id.amount_text_view)
        TextView amountTextView;

        UserBetHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTo(UserBet userBet) {
            String username = "Username: " + userBet.getUsername();
            String question = "Question: " + userBet.getQuestion();
            String betMode = null;
            switch (userBet.getBetModeId()) {
                case Bet.BetMode.TRADE:
                    betMode = "Bet Mode: Trade";
                    break;
                case Bet.BetMode.ADVANCED:
                    betMode = "Bet Mode: Advanced";
                    break;
            }
            String amount = userBet.getBetAmount() + " $";
            usernameTextView.setText(username);
            questionTextView.setText(question);
            betModeTextView.setText(betMode);
            amountTextView.setText(amount);
        }
    }
}
