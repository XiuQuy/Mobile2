package com.example.appxemphim.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;

import com.example.appxemphim.data.remote.YoutubeService;
import com.example.appxemphim.model.YoutubeChannelItem;
import com.example.appxemphim.model.YoutubeChannelResponse;
import com.example.appxemphim.model.YoutubeVideoItem;
import com.example.appxemphim.ui.adapter.GlideLoadImgListener;
import com.example.appxemphim.util.ConvertDateToDayAgo;
import com.example.appxemphim.util.ConvertNumberToShortFormat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoYoutubePlayerActivity extends AppCompatActivity {
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;
    private FrameLayout fullscreenViewContainer;
    private static final int MAX_LINES_TEXTVIEW_OVERVIEW = 3;
    private TextView textViewOverview, textViewTitle, textViewViewCount, textViewLikeCount,
            textViewDayAgo, textViewChannelName;
    private ImageView imgChannel;
    private TextView btnHideLessOverview, btnShowMoreOverview;
    private List<YoutubeVideoItem> listVideo;
    private YoutubeVideoItem videoPlaying;
    private String languageCode;
    private LinearLayout containerVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_youtube);

        //Init
        containerVideo = findViewById(R.id.container_video);
        fullscreenViewContainer = findViewById(R.id.full_screen_view_container);
        textViewOverview = findViewById(R.id.overview);
        textViewOverview.setMaxLines(MAX_LINES_TEXTVIEW_OVERVIEW + 1);
        textViewTitle = findViewById(R.id.title);
        textViewViewCount = findViewById(R.id.view_count);
        textViewLikeCount = findViewById(R.id.like_count);
        textViewDayAgo = findViewById(R.id.day_ago);
        textViewChannelName = findViewById(R.id.channel_name);
        imgChannel = findViewById(R.id.channel_img);
        btnHideLessOverview = findViewById(R.id.btn_hide_less_overview);
        btnShowMoreOverview = findViewById(R.id.btn_show_more_overview);
        btnShowMoreOverview.setPaintFlags(textViewOverview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btnHideLessOverview.setPaintFlags(textViewOverview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //click bnt view overview
        btnShowMoreOverview.setOnClickListener(v -> {
            textViewOverview.setMaxLines(Integer.MAX_VALUE);
            btnShowMoreOverview.setVisibility(View.GONE);
            btnHideLessOverview.setVisibility(View.VISIBLE);
        });
        // hide less btn click
        btnHideLessOverview.setOnClickListener(v -> {
            textViewOverview.setMaxLines(MAX_LINES_TEXTVIEW_OVERVIEW + 1);
            btnHideLessOverview.setVisibility(View.GONE);
            btnShowMoreOverview.setVisibility(View.VISIBLE);
        });

        //Get data from intent
        Intent intent = getIntent();
        if(intent != null){
            YoutubeVideoItem videoItem = (YoutubeVideoItem) intent.getSerializableExtra("youtubeVideo");
            if(videoItem != null){
                videoPlaying = videoItem;
                listVideo = new Gson().fromJson(
                        intent.getStringExtra("listVideo"),
                        new TypeToken<ArrayList<YoutubeVideoItem>>(){}.getType()
                );
            }
            else {
                finish();
            }
        }

        // Lấy ngôn ngữ
        Locale currentLocale = getResources().getConfiguration().getLocales().get(0);
        languageCode = currentLocale.getLanguage();

        //xoa video dang phat ra khoi list
        Optional<YoutubeVideoItem> optionalVideoInList= listVideo.stream()
                .filter(video -> Objects.equals(video.getId(), videoPlaying.getId())).findFirst();
        optionalVideoInList.ifPresent(youtubeVideoItem -> listVideo.remove(youtubeVideoItem));

        //info video playing
        setVideoPlayingInfo(videoPlaying);
        bindListVideoToContainer(listVideo);

        // YouTubePlayerView
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.setEnableAutomaticInitialization(false);
        youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View view, @NonNull Function0<Unit> function0) {
                // the video will continue playing in fullscreenView
                youTubePlayerView.setVisibility(View.GONE);
                fullscreenViewContainer.setVisibility(View.VISIBLE);
                fullscreenViewContainer.addView(view);
            }
            @Override
            public void onExitFullscreen() {
                // the video will continue playing in the player
                youTubePlayerView.setVisibility(View.VISIBLE);
                fullscreenViewContainer.setVisibility(View.GONE);
                fullscreenViewContainer.removeAllViews();
            }
        });

        //Thiết lập nút toàn màn hình
        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1)
                .build();

        // Khởi tạo YouTubePlayerListener
        AbstractYouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                VideoYoutubePlayerActivity.this.youTubePlayer = youTubePlayer;
                if(videoPlaying != null){
                    // Phát video
                    youTubePlayer.loadVideo(videoPlaying.getId(), 0);
                }

            }
        };
        // Khởi tạo YouTubePlayerView
        youTubePlayerView.initialize(listener, iFramePlayerOptions);
        getLifecycle().addObserver(youTubePlayerView);

        // Sự kiện btn back
        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> {
            // Xử lý sự kiện khi người dùng nhấn vào ImageView
            finish();
        });

        //xư lý hiẻn thị overview
        textViewOverview.post(() -> {
            Log.i("LAYOUT_POST", "");
            setViewButtonShowOverview();
        });

    }

    private void handlePlayVideoInActivity(YoutubeVideoItem newVideo){
        // add video cu vao
        listVideo.add(videoPlaying);
        //lay video moi
        videoPlaying = newVideo;
        //xoa video dang phat ra khoi list
        Optional<YoutubeVideoItem> optionalVideoInList= listVideo.stream()
                .filter(video -> Objects.equals(video.getId(), videoPlaying.getId())).findFirst();
        optionalVideoInList.ifPresent(youtubeVideoItem -> listVideo.remove(youtubeVideoItem));
        // chay video
        youTubePlayer.loadVideo(videoPlaying.getId(), 0);
        setVideoPlayingInfo(videoPlaying);
    }

    private void bindListVideoToContainer(List<YoutubeVideoItem> listVideo){
        LayoutInflater inflater = LayoutInflater.from(this);
        ImageView imageThumbnail;
        TextView channelName, viewCount, likeCount, title;
        for(YoutubeVideoItem video : listVideo){
            View itemLayout = inflater.inflate(
                    R.layout.item_list_video_youtube_activity_player,
                    containerVideo, false);
            imageThumbnail = itemLayout.findViewById(R.id.img_thumbnail);
            title = itemLayout.findViewById(R.id.tv_title);
            channelName = itemLayout.findViewById(R.id.tv_channel);
            viewCount = itemLayout.findViewById(R.id.view_count);
            likeCount = itemLayout.findViewById(R.id.like_count);

            title.setText(video.getSnippet().getTitle());
            channelName.setText(video.getSnippet().getChannelTitle());
            Glide.with(this)
                    .load(video.getSnippet().getThumbnails().getMediumThumbnail().getUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder_img_load))
                    .listener(new GlideLoadImgListener(imageThumbnail))
                    .into(imageThumbnail);
            String viewCountString = ConvertNumberToShortFormat.convert(
                    Long.parseLong(video.getStatistics().getViewCount()), languageCode) +
                    " " + getString(R.string.views_string);
            viewCount.setText(viewCountString);
            String likeCountString = ConvertNumberToShortFormat.convert(
                    Long.parseLong(video.getStatistics().getLikeCount()), languageCode) +
                    " " + getString(R.string.likes_string);
            likeCount.setText(likeCountString);
            itemLayout.setOnClickListener(v -> {
                containerVideo.removeView(itemLayout);
                List<YoutubeVideoItem> i = new ArrayList<>();
                i.add(videoPlaying);
                bindListVideoToContainer(i);
                handlePlayVideoInActivity(video);
            });
            containerVideo.addView(itemLayout);
        }

    }

    private void setViewButtonShowOverview(){
        btnHideLessOverview.setVisibility(View.GONE);
        String textDefault = textViewOverview.getText().toString();
        if (textViewOverview.getLayout() != null &&
                textDefault.length() > MAX_LINES_TEXTVIEW_OVERVIEW *
                        textViewOverview.getLayout().getLineEnd(0)) {
            btnShowMoreOverview.setVisibility(View.VISIBLE);
        }
    }

    public static void sendIntent(Context context, YoutubeVideoItem youtubeVideo, List<YoutubeVideoItem> listVideo){
        Intent intent = new Intent(context, VideoYoutubePlayerActivity.class);
        intent.putExtra("youtubeVideo", youtubeVideo);
        intent.putExtra("listVideo", new Gson().toJson(listVideo));
        context.startActivity(intent);
    }

    private void setVideoPlayingInfo(YoutubeVideoItem video){
        textViewOverview.setText(video.getSnippet().getDescription());
        setViewButtonShowOverview();
        textViewTitle.setText(video.getSnippet().getTitle());
        textViewViewCount.setText(
                ConvertNumberToShortFormat.convert(
                        Long.parseLong(video.getStatistics().getViewCount()),
                        languageCode));
        textViewLikeCount.setText(ConvertNumberToShortFormat.convert(
                Long.parseLong(video.getStatistics().getLikeCount()),
                languageCode));
        LocalDateTime localDateTime = LocalDateTime.parse(
                video.getSnippet().getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME);
        textViewDayAgo.setText(ConvertDateToDayAgo.convert(
                localDateTime, getString(R.string.day_ago),
                getString(R.string.month_ago), getString(R.string.year_ago)));
        String[] idChanelList = {video.getSnippet().getChannelId()};
        fetchChannelInfo(idChanelList);
    }

    private void fetchChannelInfo(String[] id){
        String[] part = {"snippet"};
        YoutubeService youtubeService = ServiceApiBuilder.buildYoutubeApiService(YoutubeService.class);
        Call<YoutubeChannelResponse> call = youtubeService.getChannelInfo(part, id, ServiceApiBuilder.API_KEY_YOUTUBE_DATA);
        call.enqueue(new Callback<YoutubeChannelResponse>() {
            @Override
            public void onResponse(Call<YoutubeChannelResponse> call, Response<YoutubeChannelResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<YoutubeChannelItem> channelItems = response.body().getItems();
                    textViewChannelName.setText(channelItems.get(0).getSnippet().getTitle());
                    Glide.with(VideoYoutubePlayerActivity.this)
                            .load(channelItems.get(0).getSnippet().getThumbnails().getDefaultThumbnail().getUrl())
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder_img_load))
                            .into(imgChannel);
                }
            }
            @Override
            public void onFailure(Call<YoutubeChannelResponse> call, Throwable t) {

            }
        });
    }

}
