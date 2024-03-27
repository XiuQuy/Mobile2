package com.example.appxemphim;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Log;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String API_KEY = "9a169454f96888fb8284d35eb3042308";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

//        // Nhận thông tin phim từ Intent
//        Movie movie = (Movie) getIntent().getSerializableExtra("MOVIE");
//
//        // Hiển thị thông tin phim trên giao diện
//        if (movie != null) {
//            ImageView posterImageView = findViewById(R.id.movie_poster_detail);
//            TextView titleTextView = findViewById(R.id.movie_title_detail);
//            TextView overviewTextView = findViewById(R.id.movie_overview_detail);
//            TextView releaseDateTextView = findViewById(R.id.movie_release_date_detail);
//            TextView ratingTextView = findViewById(R.id.movie_rating_detail);
//
//            // Hiển thị tiêu đề phim
//            titleTextView.setText(movie.getTitle());
//
//            // Hiển thị tóm tắt của phim
//            overviewTextView.setText(movie.getOverview());
//
//            // Hiển thị ngày phát hành của phim
//            releaseDateTextView.setText(getString(R.string.release_date, movie.getReleaseDate()));
//
//            // Hiển thị rating của phim
//            ratingTextView.setText(getString(R.string.rating, Double.toString(movie.getRating())));
//
//            // Load và hiển thị hình ảnh của phim
//            Glide.with(this)
//                    .load("http://image.tmdb.org/t/p/w500" + movie.getPosterPath())
//                    .into(posterImageView);
//
//        }


        // Nhận ID của phim từ Intent
        int movieId = getIntent().getIntExtra("MOVIEID", 0); // 0 là giá trị mặc định

        if (movieId != 0) {
            // Gọi API để lấy thông tin chi tiết của phim
            fetchMovieDetails(movieId);
        }

        // YouTubePlayerView
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
    }

    private void fetchMovieDetails(int movieId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TMDbApi tmdbApi = retrofit.create(TMDbApi.class);
        tmdbApi.getMovieVideos(movieId, API_KEY).enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VideoResponse videoResponse = response.body();
                    List<Video> videos = videoResponse.getResults();

                    if (!videos.isEmpty()) { // Kiểm tra danh sách không trống
                        Video firstVideo = videos.get(0); // Lấy video đầu tiên từ danh sách
                        String key = firstVideo.getKey();
                        Log.d("First Video Key", key);
                        playVideo(key); // Gọi phương thức để phát video với key đã lấy được
                    }

                } else {
                    Log.e("Video API", "Failed to get videos"); // Hiển thị thông báo lỗi trong logcat
                    // Xử lý trường hợp không thành công khi lấy thông tin video
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.e("Video API", "Network error: " + t.getMessage()); // Hiển thị thông báo lỗi trong logcat
                // Xử lý trường hợp lỗi kết nối mạng
            }
        });
    }

    private void playVideo(String key) {
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(key, 0);
            }
        });
    }



}
