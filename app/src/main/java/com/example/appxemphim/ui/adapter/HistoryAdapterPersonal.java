package com.example.appxemphim.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.YoutubeService;
import com.example.appxemphim.model.History;
import com.example.appxemphim.model.YoutubeVideoItem;
import com.example.appxemphim.model.YoutubeVideoResponse;
import com.example.appxemphim.ui.activity.MovieDetailActivity;
import com.example.appxemphim.ui.activity.VideoYoutubePlayerActivity;
import com.example.appxemphim.util.ConvertISO8601Time;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryAdapterPersonal extends RecyclerView.Adapter<HistoryAdapterPersonal.MyViewHolder> {

    private Context context;
    private List<History> listHistory;

    public HistoryAdapterPersonal(Context context, List<History> listHistory) {

        this.context = context;
        this.listHistory = listHistory;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_view_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        History history = listHistory.get(position);
        holder.bind(history);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tagMovie = history.getInformationMovie().getTag();
                if(Objects.equals(tagMovie, "TMDB_MOVIE") ||
                   Objects.equals(tagMovie, "TMDB_TV_SERIES")){
                    MovieDetailActivity.sendIntent(
                            context,
                            history.getInformationMovie().getMovieId(),
                            history.getInformationMovie().getTag(),
                            history.getInformationMovie().getTitle(),
                            history.getInformationMovie().getImageLink());
                }
                if(tagMovie.equals("YOUTUBE")){
                    String[] ids = {history.getInformationMovie().getMovieId()};
                    getVideoYoutube(context, ids, history.getSecondsCount());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView output_title;
        private TextView output_tag;
        private TextView output_time;
        private AppCompatImageView output_image;
        private ProgressBar output_progress;


        public MyViewHolder(View itemView) {
            super(itemView);
            output_title = itemView.findViewById(R.id.titleTextView);
            output_tag = itemView.findViewById(R.id.tagTextView);
            output_image = itemView.findViewById(R.id.item_image);
            output_time = itemView.findViewById(R.id.timeTextView);
            output_progress = itemView.findViewById(R.id.progressBar);
        }

        public void bind(History history) {
            output_title.setText(history.getInformationMovie().getTitle());
            String tagString = history.getInformationMovie().getTag();
            if(Objects.equals(tagString, "YOUTUBE")){
                tagString = context.getString(R.string.youtube_tag);
            }else if (Objects.equals(tagString, "TMDB_MOVIE")){
                tagString = context.getString(R.string.movie_tag);
            }else if (Objects.equals(tagString, "TMDB_TV_SERIES")){
                tagString = context.getString(R.string.tv_series_tag);
            }
            output_tag.setText(tagString);
            // Load image using Picasso or any other image loading library
            String imageUrl = history.getInformationMovie().getImageLink();
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder_img_load))
                    .listener(new GlideLoadImgListener(output_image))
                    .into(output_image);

            String tag = history.getInformationMovie().getTag();
            if ("YOUTUBE".equals(tag)) {
                int durationsInSeconds = history.getInformationMovie().getDurations();
                String formattedDuration = ConvertISO8601Time.formatSeconds(durationsInSeconds);
                output_time.setText(formattedDuration);
                output_time.setPadding(10,0,10,0);
                int secondsCount = history.getSecondsCount();
                // Tính phần trăm tiến trình
                int progress = (int) ((float) secondsCount / durationsInSeconds * 100);

                // Xử lý trường hợp khi progress vượt quá 100
                progress = Math.min(progress, 100);

                // Cập nhật giá trị của ProgressBar
                output_progress.setProgress(progress);

            } else {
                output_time.setText(""); // Nếu không phải là YOUTUBE, không hiển thị thời lượng
                output_progress.setProgress(100);
            }
        }
    }

    public String formatDuration(int durationInSeconds) {
        long hours = TimeUnit.SECONDS.toHours(durationInSeconds);
        long minutes = TimeUnit.SECONDS.toMinutes(durationInSeconds) % 60;
        long seconds = durationInSeconds % 60;

        StringBuilder formattedDuration = new StringBuilder();

        if (hours > 0) {
            formattedDuration.append(String.format("%02d:", hours));
        }
        if (minutes > 0 || hours > 0) {
            formattedDuration.append(String.format("%02d:", minutes));
        }
        formattedDuration.append(String.format("%02d", seconds));

        return formattedDuration.toString();
    }
    public static void getVideoYoutube(Context context, String[] videoId, int secondCount) {
        String[] part = {"snippet", "statistics", "contentDetails"};
        YoutubeService youtubeService = ServiceApiBuilder.buildYoutubeApiService(YoutubeService.class);
        Call<YoutubeVideoResponse> call = youtubeService.getVideoInfo(
                part,
                videoId,
                ServiceApiBuilder.API_KEY_YOUTUBE_DATA
        );
        call.enqueue(new Callback<YoutubeVideoResponse>() {
            @Override
            public void onResponse(Call<YoutubeVideoResponse> call, Response<YoutubeVideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<YoutubeVideoItem> items = response.body().getItems();
                    if (items != null && !items.isEmpty()) {
                        VideoYoutubePlayerActivity.sendIntent(context, items.get(0), new ArrayList<>(), secondCount);
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<YoutubeVideoResponse> call, Throwable t) {
                Log.e("API_ERROR", "Network error", t);
            }
        });
    }

}

