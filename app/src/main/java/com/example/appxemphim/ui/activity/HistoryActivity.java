package com.example.appxemphim.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.HistoryService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.YoutubeService;
import com.example.appxemphim.model.History;
import com.example.appxemphim.model.VideoYoutubeItem;
import com.example.appxemphim.model.VideoYoutubeResponse;
import com.example.appxemphim.model.VideoYoutubeSnippet;
import com.example.appxemphim.ui.adapter.HistoryAdapter;
import com.example.appxemphim.ui.adapter.ItemSpacingDecoration;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recycle_view_histories_atv_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        // Tạo một đối tượng ItemSpacingDecoration với khoảng cách mong muốn (đơn vị px)
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemDecoration = new ItemSpacingDecoration(spacingInPixels);
        recyclerView.addItemDecoration(itemDecoration);

        fetchData();


    }
    private void fetchData() {
        HistoryService historyService = ServiceApiBuilder.buildUserApiService(HistoryService.class);
        Call<List<History>> call = historyService.getHistory(15,1,"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJKV1RTZXJ2aWNlQWNjZXNzVG9rZW4iLCJqdGkiOiI3ODQ2Njk5Zi05YTE0LTQ5ZDgtYThhMC1mZjYxMDE5ZTc1OGEiLCJpYXQiOiIyNy8wMy8yMDI0IDM6MjE6MzUgQ0giLCJVc2VySWQiOiIxIiwiRW1haWwiOiJkb2xlaHV5MjIyQGdtYWlsLmNvbSIsImV4cCI6MTcxMjQxNjg5NSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0OTg3MCIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6NDk4NzAifQ.XF12bR6lft8fHtpz9JtR6JMTRXUYpLs6UcWE7jWqfEU");

        call.enqueue(new Callback<List<History>>() {
            @Override
            public void onResponse(Call<List<History>> call, Response<List<History>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<History> movies = response.body();
                    recyclerView.setAdapter(new HistoryAdapter(HistoryActivity.this, movies));
                } else {
                    Toast.makeText(HistoryActivity.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<History>> call, Throwable t) {
                Log.e("API_ERROR", "Network error", t);
                Toast.makeText(HistoryActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getVideoInfo(String videoId) {
        YoutubeService youtubeService = ServiceApiBuilder.buildYoutubeApiService(YoutubeService.class);

        Call<VideoYoutubeResponse> call = youtubeService.getVideoInfo("snippet", videoId, "AIzaSyBSBAJLOEJmynxkun7JBGJlPwjJTcnJQXI");
        call.enqueue(new Callback<VideoYoutubeResponse>() {
            @Override
            public void onResponse(Call<VideoYoutubeResponse> call, Response<VideoYoutubeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VideoYoutubeItem[] items = response.body().getItems();
                    if (items != null && items.length > 0) {
                        VideoYoutubeSnippet snippet = items[0].getSnippet();
                        // Ở đây bạn có thể truy cập các thông tin về video, bao gồm cả URL của thumbnail
                        String title = snippet.getTitle();
                        String description = snippet.getDescription();
                        String channelId = snippet.getChannelId();
                        String thumbnailUrl = snippet.getThumbnails().getDefaultThumbnail().getUrl();
                        String channelTitle = snippet.getChannelTitle();
                    }
                } else {
                    Toast.makeText(HistoryActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<VideoYoutubeResponse> call, Throwable t) {
                Log.e("API_ERROR", "Network error", t);
            }
        });
    }
}
