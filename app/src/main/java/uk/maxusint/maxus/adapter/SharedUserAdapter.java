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
import uk.maxusint.maxus.network.model.SharedUser;

public class SharedUserAdapter extends RecyclerView.Adapter<SharedUserAdapter.UserHolder> {
    private ItemClickListener itemClickListener;
    private List<SharedUser> users;

    public SharedUserAdapter(List<SharedUser> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public SharedUserAdapter.UserHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext().getApplicationContext())
                .inflate(R.layout.user_row_item, viewGroup, false);
        final SharedUserAdapter.UserHolder userHolder = new SharedUserAdapter.UserHolder(view);
        userHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.onClick(users.get(userHolder.getAdapterPosition()));
            }
        });
        return userHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SharedUserAdapter.UserHolder userHolder, int i) {
        userHolder.bindTo(users.get(i));
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name_text_view)
        TextView nameTextView;
        @BindView(R.id.user_type_text_view)
        TextView userTypeTextView;
        @BindView(R.id.address_text_view)
        TextView addressTextView;
        @BindView(R.id.mobile_text_view)
        TextView mobileTextView;

        UserHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTo(SharedUser user) {
            StringBuilder address = new StringBuilder();
            if (user.getUp() != null) {
                address.append(user.getUp()).append(", ");
            }
            if (user.getUpazilla() != null) {
                address.append(user.getUpazilla()).append(", ");
            }
            if (user.getDistrict() != null) {
                address.append(user.getDistrict());
            }
            nameTextView.setText(user.getName());
            addressTextView.setText(address.toString());
            mobileTextView.setText(user.getMobile());
            userTypeTextView.setText(String.format("%s%%", user.getSharedPercent()));
        }
    }

    public interface ItemClickListener {
        void onClick(SharedUser user);
    }
}
