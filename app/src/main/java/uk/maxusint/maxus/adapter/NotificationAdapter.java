package uk.maxusint.maxus.adapter;

import android.content.Context;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.model.Notification;
import uk.maxusint.maxus.utils.CircleTransform;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private Context context;
    private List<Notification> notificationList = new ArrayList<Notification>();
    private ItemClickListener itemClickListener;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_row_item, viewGroup, false);
        final NotificationHolder notificationHolder = new NotificationHolder(view);
        notificationHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(notificationList.get(notificationHolder.getAdapterPosition()));
                }
            }
        });
        return notificationHolder;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder notificationHolder, int i) {
        notificationHolder.bindTo(notificationList.get(i));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public interface ItemClickListener {
        void onClick(Notification notification);
    }

    class NotificationHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.notification_icon_image_view)
        ImageView notificationIconImageView;
        @BindView(R.id.notification_body_text_view)
        TextView notificationBodyTextView;
        @BindView(R.id.date_text_view)
        TextView dateTextView;

        NotificationHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTo(Notification notification) {
            if (notification.getSeen() > 0) {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.seenColor));
            } else {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.unseenColor));
            }
            notificationBodyTextView.setText(notification.getBody());
            dateTextView.setText(timeBetween(notification.getDate()));

            switch (notification.getType()) {
                case "Transaction":
                    Glide.with(context).load(R.drawable.ic_bet_black)
                            .apply(RequestOptions.bitmapTransform(new CircleTransform()))
                            .thumbnail(0.5f)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .into(notificationIconImageView);
                    break;
            }

        }


        private String timeBetween(String dateString) {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            try {
                Date oldDate = dateTimeFormat.parse(dateString);
                Date currentDate = new Date(System.currentTimeMillis());
                TimeUnit timeUnit = TimeUnit.MINUTES;
                long differentMilliSeconds = currentDate.getTime() - oldDate.getTime();
                long differentMinute = timeUnit.convert(differentMilliSeconds, TimeUnit.MILLISECONDS);
                String time;
                if (differentMinute >= 60) {
                    long hour = differentMinute / 60;
                    if (hour > 24) {
                        long day = hour / 24;
                        if (day > 1) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                            time = dateFormat.format(oldDate);
                        } else {
                            time = String.valueOf(day) + " day ago";
                        }

                    } else {
                        time = String.valueOf(hour) + " hour ago";
                    }
                } else {
                    time = String.valueOf(differentMinute) + " minute ago";
                }
                return time;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
