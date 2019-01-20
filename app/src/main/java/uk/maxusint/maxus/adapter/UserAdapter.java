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
import uk.maxusint.maxus.network.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    private ItemClickListener itemClickListener;
    private List<User> users;

    public UserAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext().getApplicationContext())
                .inflate(R.layout.user_row_item, viewGroup, false);
        final UserHolder userHolder = new UserHolder(view);
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
    public void onBindViewHolder(@NonNull UserHolder userHolder, int i) {
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

        void bindTo(User user) {
            StringBuilder address = new StringBuilder();
            if (user.getUp() != null && !user.getUp().equals("")) {
                address.append(user.getUp()).append(", ");
            }
            if (user.getUpazilla() != null && !user.getUpazilla().equals("")) {
                address.append(user.getUpazilla()).append(", ");
            }
            if (user.getDistrict() != null && !user.getDistrict().equals("")) {
                address.append(user.getDistrict());
            }
            nameTextView.setText(user.getName());
            addressTextView.setText(address);
            mobileTextView.setText(user.getMobile());
            switch (user.getTypeId()) {
                case User.UserType.ADMIN:
                    userTypeTextView.setText(R.string.admin_text);
                    break;
                case User.UserType.CLUB:
                    userTypeTextView.setText(R.string.club_text);
                    break;
                case User.UserType.AGENT:
                    userTypeTextView.setText(R.string.agent_text);
                    break;
                case User.UserType.ROYAL:
                    userTypeTextView.setText(R.string.royal_text);
                    break;
                case User.UserType.CLASSIC:
                    userTypeTextView.setText(R.string.classic_text);
                    break;
                case User.UserType.PREMIUM:
                    userTypeTextView.setText(R.string.premium_text);
                    break;
            }
        }
    }

    public interface ItemClickListener {
        void onClick(User user);
    }
}
