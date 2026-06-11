package com.example.rapapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rapapp.R;
import com.example.rapapp.models.Booking;
import com.example.rapapp.models.Movie;
import com.example.rapapp.models.Showtime;
import com.example.rapapp.utils.PriceUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private FirebaseFirestore db;

    public TransactionAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // Display title and image from summary fields if available
        if (booking.getMainTitle() != null) {
            holder.tvTitle.setText(booking.getMainTitle());
        }
        
        if (booking.getMainImage() != null && !booking.getMainImage().isEmpty()) {
            int placeholder = "movie_ticket".equals(booking.getType()) ? R.drawable.bg_placeholder : R.drawable.gift;
            Glide.with(context).load(booking.getMainImage()).placeholder(placeholder).into(holder.ivPoster);
        } else {
            int resId = "movie_ticket".equals(booking.getType()) ? R.drawable.bg_placeholder : R.drawable.gift;
            holder.ivPoster.setImageResource(resId);
        }

        if ("movie_ticket".equals(booking.getType())) {
            holder.tvBookingType.setText("Vé xem phim");
            holder.tvBookingType.setBackgroundResource(R.drawable.bg_btn_outline_blue);
            holder.tvBookingType.setTextColor(ContextCompat.getColor(context, R.color.galaxy_blue));
            
            // Fetch additional movie and showtime info if mainTitle is missing (for older bookings)
            if (booking.getMainTitle() == null) {
                db.collection("movies").document(booking.getMovieId()).get().addOnSuccessListener(movieDoc -> {
                    Movie movie = movieDoc.toObject(Movie.class);
                    if (movie != null) {
                        holder.tvTitle.setText(movie.getTitle());
                        holder.tvAgeRating.setText(movie.getAgeRating());
                        Glide.with(context).load(movie.getPosterUrl()).placeholder(R.drawable.bg_placeholder).into(holder.ivPoster);
                    }
                });
            }

            db.collection("showtimes").document(booking.getShowtimeId()).get().addOnSuccessListener(showtimeDoc -> {
                Showtime showtime = showtimeDoc.toObject(Showtime.class);
                if (showtime != null) {
                    holder.tvFormat.setText(showtime.getFormat());
                    holder.tvCinemaName.setText(booking.getCinemaName());
                    holder.tvTimeDate.setText(showtime.getTime() + " - " + showtime.getDate());
                }
            });
        } else {
            holder.tvBookingType.setText("Star Shop");
            holder.tvBookingType.setBackgroundResource(R.drawable.bg_btn_outline_orange);
            holder.tvBookingType.setTextColor(ContextCompat.getColor(context, R.color.orange_primary));
            
            List<Map<String, Object>> items = booking.getItems();
            if (booking.getMainTitle() == null && items != null && !items.isEmpty()) {
                String firstProductName = (String) items.get(0).get("productName");
                String imageUrl = (String) items.get(0).get("productImage");
                
                if (items.size() > 1) {
                    holder.tvTitle.setText(firstProductName + " và " + (items.size() - 1) + " sản phẩm khác");
                } else {
                    holder.tvTitle.setText(firstProductName);
                }
                
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(context).load(imageUrl).placeholder(R.drawable.gift).into(holder.ivPoster);
                } else {
                    holder.ivPoster.setImageResource(R.drawable.gift);
                }
            } else if (booking.getMainTitle() == null) {
                holder.tvTitle.setText("Đơn hàng Star Shop");
                holder.ivPoster.setImageResource(R.drawable.gift);
            }

            holder.tvFormat.setText((items != null ? items.size() : 0) + " sản phẩm");
            holder.tvAgeRating.setVisibility(View.GONE);
            holder.tvCinemaName.setText("Nhận tại: " + booking.getCinemaName());
            holder.tvTimeDate.setText("Tổng cộng: " + PriceUtils.formatCurrency(booking.getTotalPrice()));
        }

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, com.example.rapapp.TransactionDetailActivity.class);
            intent.putExtra("booking", booking);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvFormat, tvAgeRating, tvCinemaName, tvTimeDate, tvBookingType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvFormat = itemView.findViewById(R.id.tvFormat);
            tvAgeRating = itemView.findViewById(R.id.tvAgeRating);
            tvCinemaName = itemView.findViewById(R.id.tvCinemaName);
            tvTimeDate = itemView.findViewById(R.id.tvTimeDate);
            tvBookingType = itemView.findViewById(R.id.tvBookingType);
        }
    }
}
