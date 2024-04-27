package com.example.appxemphim.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.TMDbApi;
import com.example.appxemphim.model.Movie;
import com.example.appxemphim.model.MovieResponse;
import com.example.appxemphim.model.Video;
import com.example.appxemphim.model.VideoResponse;
import com.example.appxemphim.ui.adapter.MovieAdapter;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoYoutubePlayerActivity extends AppCompatActivity {
    private static final String API_KEY = "9a169454f96888fb8284d35eb3042308";
    private RecyclerView recyclerView;
    private MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_youtube);
        recyclerView = findViewById(R.id.rcv_allcate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter với context của activity
        adapter = new MovieAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Gọi API và cập nhật danh sách phim
        fetchMovies();

        // Nhận đối tượng phim từ Intent
        Movie movie = (Movie) getIntent().getSerializableExtra("MOVIE");

        if (movie != null) {
            //int movieId = movie.getId(); // Lấy ID của phim từ đối tượng Movie
            // Gọi API để lấy thông tin chi tiết của phim với ID đã lấy được
            //fetchMovieDetails(movieId);
            TextView titleTextView = findViewById(R.id.movie_title_detail);
            TextView overviewTextView = findViewById(R.id.movie_overview_detail);
            TextView releaseDateTextView = findViewById(R.id.movie_release_date_detail);
            TextView ratingTextView = findViewById(R.id.movie_rating_detail);

            // Hiển thị tiêu đề phim
            titleTextView.setText(movie.getName());

            // Hiển thị tóm tắt của phim
            //overviewTextView.setText(movie.getOverview());
            // Hiển thị ngày phát hành của phim
            //releaseDateTextView.setText(getString(R.string.release_date, movie.getReleaseDate()));

            // Hiển thị rating của phim
            //ratingTextView.setText(getString(R.string.rating, Double.toString(movie.getRating())));
        }

        // YouTubePlayerView
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi người dùng nhấn vào ImageView
                onBackPressed();
            }
        });
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

    private void fetchMovies() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TMDbApi tmdbApi = retrofit.create(TMDbApi.class);

        Call<MovieResponse> call = tmdbApi.getPopularMovies(API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //List<Movie> movies = response.body().getResults();
                    //adapter.setMovies(movies);
                    adapter.setOnItemClickListener(new MovieAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Movie movie) {
                            //int movieId = movie.getId(); // Lấy ID của phim được chọn
                            //fetchMovieDetails(movieId); // Gọi phương thức để lấy thông tin chi tiết của phim với ID đã lấy được

                            TextView titleTextView = findViewById(R.id.movie_title_detail);
                            TextView overviewTextView = findViewById(R.id.movie_overview_detail);
                            TextView releaseDateTextView = findViewById(R.id.movie_release_date_detail);
                            TextView ratingTextView = findViewById(R.id.movie_rating_detail);

                            // Hiển thị tiêu đề phim
                            //titleTextView.setText(movie.getTitle());

                            // Hiển thị tóm tắt của phim
                            //overviewTextView.setText(movie.getOverview());
                            // Hiển thị ngày phát hành của phim
                            //releaseDateTextView.setText(getString(R.string.release_date, movie.getReleaseDate()));

                            // Hiển thị rating của phim
                            //ratingTextView.setText(getString(R.string.rating, Double.toString(movie.getRating())));

                        }
                    });
                } else {
                    Toast.makeText(VideoYoutubePlayerActivity.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(VideoYoutubePlayerActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private YouTubePlayer youTubePlayer; // Biến lưu trữ YouTubePlayer
    private boolean isVideoPlaying = false; // Biến boolean để kiểm tra trạng thái video

    private void playVideo(String key) {
        Log.d("Key", key);
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
            @Override
            public void onYouTubePlayer(@NonNull YouTubePlayer player) {
                youTubePlayer = player; // Lưu trữ YouTubePlayer để sử dụng sau này
                if (isVideoPlaying) {
                    // Nếu video trước đó đang phát, dừng nó trước khi phát video mới
                    youTubePlayer.pause();
                    isVideoPlaying = false; // Đặt lại trạng thái video
                }
                // Phát video mới
                player.loadVideo(key, 0);
                isVideoPlaying = true; // Đặt lại trạng thái video
            }
        });
    }

}
