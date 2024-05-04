package com.example.appxemphim.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.PlaylistService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;

import com.example.appxemphim.data.remote.YoutubeService;
import com.example.appxemphim.model.InformationMovie;
import com.example.appxemphim.model.Movie;
import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.model.PlaylistWithOneItemDTO;
import com.example.appxemphim.model.YoutubeChannelItem;
import com.example.appxemphim.model.YoutubeChannelResponse;
import com.example.appxemphim.model.YoutubeVideoItem;
import com.example.appxemphim.ui.adapter.GlideLoadImgListener;
import com.example.appxemphim.ui.fragment.PopupAddToPlayListFragment;
import com.example.appxemphim.ui.fragment.PopupAddWithNewPlaylistFragment;
import com.example.appxemphim.ui.fragment.RightFilterFragmentSearchActivity;
import com.example.appxemphim.ui.viewmodel.PlaylistModel;
import com.example.appxemphim.util.AddMovieToPlaylistLoader;
import com.example.appxemphim.util.AddWithNewPlaylistLoader;
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

public class VideoYoutubePlayerActivity extends AppCompatActivity implements
        PopupAddWithNewPlaylistFragment.IPopupAddWithNewPlaylistAddSendListener,
        PopupAddToPlayListFragment.IPopupAddToPlaylistSendListener{
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
    PlaylistModel playlistModel;
    PopupAddToPlayListFragment popupAddToPlayListFragment;
    private int secondViewCount = 0;
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
        playlistModel = new ViewModelProvider(this).get(PlaylistModel.class);
        fetchPlaylists();

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
                secondViewCount = intent.getIntExtra("secondViewCount", 0);
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
                    youTubePlayer.loadVideo(videoPlaying.getId(), secondViewCount);
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
        TextView channelName, viewCount, likeCount, title, menu;
        for(YoutubeVideoItem video : listVideo){
            View itemLayout = inflater.inflate(
                    R.layout.item_list_video_youtube_activity_player,
                    containerVideo, false);
            imageThumbnail = itemLayout.findViewById(R.id.img_thumbnail);
            title = itemLayout.findViewById(R.id.tv_title);
            channelName = itemLayout.findViewById(R.id.tv_channel);
            viewCount = itemLayout.findViewById(R.id.view_count);
            likeCount = itemLayout.findViewById(R.id.like_count);
            menu = itemLayout.findViewById(R.id.menu);

            TextView finalMenu = menu;
            menu.setOnClickListener(v -> showOptionMenu(finalMenu, video));

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

    public static void sendIntent(Context context, YoutubeVideoItem youtubeVideo, List<YoutubeVideoItem> listVideo, int secondViewCount){
        Intent intent = new Intent(context, VideoYoutubePlayerActivity.class);
        intent.putExtra("youtubeVideo", youtubeVideo);
        intent.putExtra("listVideo", new Gson().toJson(listVideo));
        intent.putExtra("secondViewCount", secondViewCount);
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
    private void showOptionMenu(View view, YoutubeVideoItem video) {
        Movie movie = new Movie();
        movie.setId(video.getId());
        movie.setTag("YOUTUBE");
        movie.setName(video.getSnippet().getTitle());
        movie.setPosterPath(video.getSnippet().getThumbnails().getMediumThumbnail().getUrl());
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_youtube_video_item);
        popupMenu.setOnMenuItemClickListener(item -> {
            int idItem = item.getItemId();
            if(idItem == R.id.item_add_to_playlist){
                popupAddToPlayListFragment = new PopupAddToPlayListFragment(movie);
                popupAddToPlayListFragment.show(getSupportFragmentManager(), "popup_add_to_playlist_fragment");
                Log.i("ITEM RECYCLER VIEW", "click add playlist option");
                return true;
            }
            return false;
        });
        popupMenu.show();
    }
    //
    // LOADER ADD WITH NEW PLAYLIST
    //
    @Override
    public void btnAddWithNewPlaylistClick(PlaylistWithOneItemDTO playlist) {
        String playlistJson = new Gson().toJson(playlist);
        Bundle args = new Bundle();
        args.putString("playlistJson", playlistJson);
        LoaderManager.getInstance(VideoYoutubePlayerActivity.this).restartLoader(2001, args, loaderAddWithNewPlaylist);
    }

    LoaderManager.LoaderCallbacks<Playlist> loaderAddWithNewPlaylist = new LoaderManager.LoaderCallbacks<Playlist>() {
        @NonNull
        @Override
        public Loader<Playlist> onCreateLoader(int id, @Nullable Bundle args) {
            assert args != null;
            String playlistJson = args.getString("playlistJson");
            Gson gson = new Gson();
            PlaylistWithOneItemDTO playlist = gson.fromJson(playlistJson, PlaylistWithOneItemDTO.class);
            return new AddWithNewPlaylistLoader(VideoYoutubePlayerActivity.this, playlist);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Playlist> loader, Playlist data) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.layout_toast_message_add_or_remove_playlist_item,
                    findViewById(R.id.toast_message_add_or_remove_playlist_item_container));
            String addToString =  getString(R.string.add_to) + " ";
            String successfullyString = " " + getString(R.string.successfully);
            String failureString = " " + getString(R.string.failure);
            String message ;
            TextView text = layout.findViewById(R.id.message);
            if(data == null){
                message = addToString + "new playlist" + failureString;
            }else{
                message = addToString + data.getTitle() + successfullyString;
                playlistModel.addAPlaylist(data);
            }
            text.setText(message);

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();

            new Handler().postDelayed(toast::cancel, 2000);
            // Dọn dẹp Loader sau khi hoàn thành
            LoaderManager.getInstance(VideoYoutubePlayerActivity.this).destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Playlist> loader) {

        }
    };


    //
    // LOADER ADD OR REMOVE WITH EXIST PLAYLIST
    //
    @Override
    public void whenPopupAddToPlaylistDismiss(ArrayList<Integer> listAdd,
                                              ArrayList<Integer> listRemove,
                                              InformationMovie informationMovie) {
        String informationMovieJson = new Gson().toJson(informationMovie);
        Bundle args = new Bundle();
        args.putIntegerArrayList("listAdd", listAdd);
        args.putIntegerArrayList("listRemove", listRemove);
        args.putString("informationMovieJson", informationMovieJson);
        LoaderManager.getInstance(VideoYoutubePlayerActivity.this).restartLoader(2002, args, loaderAddOrRemoveExistPlaylist);
        Log.i("CALL_ADD_TO_PLAYLIST", "CALL ADD TO PLAYLIST");
    }

    LoaderManager.LoaderCallbacks<List<String>> loaderAddOrRemoveExistPlaylist = new LoaderManager.LoaderCallbacks<List<String>>() {
        @NonNull
        @Override
        public Loader<List<String>> onCreateLoader(int id, @Nullable Bundle args) {
            Log.i("LOADER_ADD_TO_PLAYLIST", "CREATE");
            assert args != null;
            ArrayList<Integer> listAdd = args.getIntegerArrayList("listAdd");
            ArrayList<Integer> listRemove = args.getIntegerArrayList("listRemove");
            String informationMovieJson = args.getString("informationMovieJson");
            Gson gson = new Gson();
            InformationMovie informationMovie = gson.fromJson(informationMovieJson, InformationMovie.class);
            return new AddMovieToPlaylistLoader(VideoYoutubePlayerActivity.this, listAdd, listRemove, informationMovie);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {
            if(data != null){
                List<String> updateMessage = new ArrayList<>();
                for (String message: data) {
                    updateMessage.add(replaceIdWithName(message));
                }
                showToastDelayed(updateMessage);
            }else{
                data = new ArrayList<>();
                data.add(getString(R.string.message_have_some_error));
                showToastDelayed(data);
            }
            // Dọn dẹp Loader sau khi hoàn thành
            LoaderManager.getInstance(VideoYoutubePlayerActivity.this).destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<String>> loader) {

        }
    };
    private void showToastDelayed(List<String> messages) {
        for (int i = 0; i < messages.size(); i++) {
            final int index = i;
            new Handler().postDelayed(() -> {
                // Hiển thị Toast với message tại vị trí hiện tại của danh sách
                showToastMessageAddItemPlaylist(messages.get(index));
            }, i * 2000L); // Mỗi Toast hiển thị sau 1 giây
        }
    }

    private void showToastMessageAddItemPlaylist(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_toast_message_add_or_remove_playlist_item,
                findViewById(R.id.toast_message_add_or_remove_playlist_item_container));
        TextView text = layout.findViewById(R.id.message);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    //Fetch playlist
    private void fetchPlaylists() {
        PlaylistService tmdbApi = ServiceApiBuilder.buildUserApiService(PlaylistService.class);
        SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        String userToken = prefs.getString("token", "");

        Call<List<Playlist>> call = tmdbApi.getPlaylist(userId, "Bearer " + userToken);

        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(@NonNull Call<List<Playlist>> call, @NonNull Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    playlistModel.setListPlaylist(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Playlist>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "", t);
            }
        });
    }

    private String replaceIdWithName(String message) {
        for (Playlist playlist : playlistModel.getListPlaylist()) {
            String idString = String.valueOf(playlist.getId());
            String searchPattern = "\\b" + idString + "\\b";
            message = message.replaceAll(searchPattern, playlist.getTitle());
        }
        return message;
    }
}
