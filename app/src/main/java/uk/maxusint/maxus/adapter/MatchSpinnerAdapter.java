package uk.maxusint.maxus.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.model.Match;

public class MatchSpinnerAdapter extends BaseAdapter {
    private Context mContext;
    private List<Match> matches = new ArrayList<>();


    public MatchSpinnerAdapter(Context context, List<Match> matches) {
        this.mContext = context;
        this.matches = matches;
    }

    @Override
    public int getCount() {
        return matches.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.dropdown_spinner_item, null, false);
        }
        TextView textView = convertView.findViewById(R.id.text_view_list_item);
        if (position == 0) {
            textView.setText("Select match");
        } else {
            Match match = matches.get(position - 1);
            String matchTitle = match.getTeam1() + " vs " + match.getTeam2();
            textView.setText(matchTitle);
        }

        return convertView;
    }
}
