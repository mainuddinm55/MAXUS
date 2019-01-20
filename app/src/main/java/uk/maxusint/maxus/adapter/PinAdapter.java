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
import uk.maxusint.maxus.network.model.Pin;
import uk.maxusint.maxus.network.model.User;

public class PinAdapter extends RecyclerView.Adapter<PinAdapter.PinHolder> {
    private List<Pin> pinList = new ArrayList<>();

    @NonNull
    @Override
    public PinHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.pin_row_item, viewGroup, false);
        PinHolder pinHolder = new PinHolder(itemView);
        return pinHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PinHolder pinHolder, int i) {
        pinHolder.bindTo(pinList.get(i));
    }

    @Override
    public int getItemCount() {
        return pinList.size();
    }

    public void setPinList(List<Pin> pinList) {
        this.pinList = pinList;
        notifyDataSetChanged();
    }

    class PinHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pin_text_view)
        TextView pinTextView;
        @BindView(R.id.user_type_text_view)
        TextView userTypeTextView;
        @BindView(R.id.pin_owner_text_view)
        TextView pinOwnerTextView;
        @BindView(R.id.used_text_view)
        TextView usedTextView;

        PinHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTo(Pin pin) {
            pinTextView.setText(pin.getPin());
            boolean used;
            used = pin.getUsed() > 0;
            String type = null;
            if (pin.getUserTypeId() == User.UserType.CLASSIC) {
                type = "Classic";
            } else if (pin.getUserTypeId() == User.UserType.ROYAL) {
                type = "Royal";
            }
            userTypeTextView.setText(type);
            pinOwnerTextView.setText(pin.getOwnerId()+"");
            usedTextView.setText(String.valueOf(used));
        }
    }
}
