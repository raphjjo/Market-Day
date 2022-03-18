package com.raphjjodev.marketday;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class MarketRecyclerAdapter extends ListAdapter<Market, MarketRecyclerAdapter.MarketHolder> {

    private OnItemClicked onItemClicked;

    public MarketRecyclerAdapter() {
        super(DIFF_CALLBACK);
    }

    public static final DiffUtil.ItemCallback<Market> DIFF_CALLBACK = new DiffUtil.ItemCallback<Market>() {
        @Override
        public boolean areItemsTheSame(@NonNull Market oldItem, @NonNull Market newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Market oldItem, @NonNull Market newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getLocation().equals(newItem.getLocation()) &&
                    oldItem.getLastDate().equals(newItem.getLastDate()) &&
                    oldItem.isDayInclusive() == newItem.isDayInclusive() &&
                    oldItem.isFavourite() == newItem.isFavourite();
        }
    };

    public interface OnItemClicked{

        void onFavouriteIconClicked(Market market);
        void onEditIconClicked(Market market);
    }

    public void setOnItemClickedListener(OnItemClicked onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

    public Market getMarketAt(int position) {

        return getItem(position);
    }

    class MarketHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, daysRemainingTextView, currentDateTextView, nextDateTextView;
        ImageView favouriteIcon, editIcon;

        public MarketHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name_location_txtView);
            daysRemainingTextView = itemView.findViewById(R.id.days_left_txtView);
            currentDateTextView = itemView.findViewById(R.id.current_date_txtView);
            nextDateTextView = itemView.findViewById(R.id.next_market_date_txtView);

            favouriteIcon = itemView.findViewById(R.id.favourite_icon);
            editIcon = itemView.findViewById(R.id.edit_icon);

            favouriteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    if (onItemClicked != null && position != RecyclerView.NO_POSITION)
                        onItemClicked.onFavouriteIconClicked(getMarketAt(position));
                }
            });

            editIcon.setOnClickListener(view -> {

                int position = getAdapterPosition();
                if (onItemClicked != null && position != RecyclerView.NO_POSITION)
                    onItemClicked.onEditIconClicked(getMarketAt(position));
            });
        }
    }

    @NonNull
    @Override
    public MarketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.market_card, parent, false);

        return new MarketHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketHolder holder, int position) {

        Market currentMarket = getItem(position);
        MarketDayAlgorithm marketDayAlgorithm = new MarketDayAlgorithm(currentMarket.getLastDate(),
                currentMarket.getInterval(), currentMarket.isDayInclusive());
        holder.nameTextView.setText(currentMarket.getName() + ", " + currentMarket.getLocation());
        holder.daysRemainingTextView.setText(marketDayAlgorithm.getDaysRemaining());
        holder.currentDateTextView.setText(marketDayAlgorithm.getCurrentDate());
        holder.nextDateTextView.setText(marketDayAlgorithm.getNextDateString());
        holder.favouriteIcon.setImageResource(changeNotificationIcon(currentMarket.isFavourite()));
        holder.favouriteIcon.setTag(changeNotificationIconTag(currentMarket.isFavourite()));

    }

    private int changeNotificationIcon(boolean favoriteBellActive) {

        if (favoriteBellActive)
            return R.drawable.ic_notifications_active;
        else
            return R.drawable.ic_notifications_off;
    }

    private int changeNotificationIconTag(boolean favoriteBellActive) {

        if (favoriteBellActive)
            return 1;
        else
            return 2;
    }
}
