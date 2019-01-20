package uk.maxusint.maxus.adapter;

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
import uk.maxusint.maxus.network.model.Match;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchHolder> {
    private List<Match> matchList = new ArrayList<>();
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MatchAdapter(List<Match> matchList) {
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MatchHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext().getApplicationContext())
                .inflate(R.layout.match_row_item, viewGroup, false);
        final MatchHolder matchHolder = new MatchHolder(itemView);
        matchHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.onClick(matchList.get(matchHolder.getAdapterPosition()));
            }
        });
        return matchHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchHolder matchHolder, int i) {
        Match match = matchList.get(i);
        matchHolder.tournamentTextView.setText(match.getTournament());
        matchHolder.matchFormatTextView.setText(match.getMatchFormat());
        matchHolder.team1TextView.setText(match.getTeam1());
        matchHolder.team2TextView.setText(match.getTeam2());
        matchHolder.timeTextView.setText(getTimeFromString(match.getDateTime()));
        matchHolder.dateTextView.setText(getDateFromString(match.getDateTime()));
        matchHolder.monthTextView.setText(getMonthFromString(match.getDateTime()));
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    private String getTimeFromString(String dateString) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date date = dateTimeFormat.parse(dateString);
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa", Locale.US);
            return timeFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
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

    public interface ItemClickListener {
        void onClick(Match match);
    }

    class MatchHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tournament_text_view)
        TextView tournamentTextView;
        @BindView(R.id.date_text_view)
        TextView dateTextView;
        @BindView(R.id.month_text_view)
        TextView monthTextView;
        @BindView(R.id.match_format_text_view)
        TextView matchFormatTextView;
        @BindView(R.id.team1_text_view)
        TextView team1TextView;
        @BindView(R.id.team2_text_view)
        TextView team2TextView;
        @BindView(R.id.time_text_view)
        TextView timeTextView;

        MatchHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
