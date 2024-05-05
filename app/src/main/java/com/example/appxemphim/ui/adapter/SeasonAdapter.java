package com.example.appxemphim.ui.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.ui.viewmodel.DetailTvResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.SeasonViewHolder> {
    private List<DetailTvResponse.Season> seasonList;

    public SeasonAdapter(List<DetailTvResponse.Season> seasonList) {
        this.seasonList = seasonList;
    }

    @NonNull
    @Override
    public SeasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.season_item_layout, parent, false);
        return new SeasonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonViewHolder holder, int position) {
        DetailTvResponse.Season season = seasonList.get(position);
        holder.bind(season);
    }

    @Override
    public int getItemCount() {
        return seasonList.size();
    }

    public static class SeasonViewHolder extends RecyclerView.ViewHolder {
        private TextView seasonNameTextView;
        private TextView episodeCountTextView;
        private TextView seasonDate,seasonOverview,seasonRate;
        private ImageView img;
        public SeasonViewHolder(@NonNull View itemView) {
            super(itemView);
            seasonNameTextView = itemView.findViewById(R.id.tvSeasonName);
            episodeCountTextView = itemView.findViewById(R.id.tvEpisodeCount);
            seasonDate=itemView.findViewById(R.id.tvAirDate);
            img=itemView.findViewById(R.id.imgSeasonPoster);
            seasonOverview=itemView.findViewById(R.id.tvOverview);
            seasonRate=itemView.findViewById(R.id.seasonRate);
        }

        public void bind(DetailTvResponse.Season season) {
            seasonNameTextView.setText(season.getName());
            episodeCountTextView.setText(season.getEpisodeCount()+" Episodes");
            double rating = season.getRating() * 10;
            String formattedRating;
            if (rating % 1 == 0) {
                formattedRating = String.valueOf((int) rating);
            } else {
                formattedRating = String.valueOf(rating);
            }
            seasonRate.setText(formattedRating + "%");

            // Kiểm tra xem date có null không trước khi sử dụng
            String date = season.getAirDate();
            if (date != null) {
                String[] parts = date.split("-");
                String year = parts[0];
                seasonDate.setText(year+" • ");
            } else {
                seasonDate.setText(""); // Xử lý trường hợp date là null
            }

            Picasso.get()
                    .load("https://image.tmdb.org/t/p/w500" + season.getPosterPath())
                    .fit()
                    .centerCrop()
                    .into(img);
            seasonOverview.setText(season.getOverview());
        }

    }
}
