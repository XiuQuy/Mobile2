package com.example.appxemphim.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.PlaylistService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.TMDBService;
import com.example.appxemphim.model.FilterSearch;
import com.example.appxemphim.model.InformationMovie;
import com.example.appxemphim.model.Movie;
import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.model.PlaylistWithOneItemDTO;
import com.example.appxemphim.model.TMDBSearchMovieResponse;
import com.example.appxemphim.model.TMDBMovieResult;
import com.example.appxemphim.model.TMDBSearchTVResponse;
import com.example.appxemphim.model.TMDBTVResult;
import com.example.appxemphim.ui.adapter.SearchAdapterActivitySearch;
import com.example.appxemphim.ui.fragment.PopupAddToPlayListFragment;
import com.example.appxemphim.ui.fragment.PopupAddWithNewPlaylistFragment;
import com.example.appxemphim.ui.fragment.RightFilterFragmentSearchActivity;
import com.example.appxemphim.ui.viewmodel.PlaylistModel;
import com.example.appxemphim.ui.viewmodel.SearchViewModel;
import com.example.appxemphim.util.AddMovieToPlaylistLoader;
import com.example.appxemphim.util.AddWithNewPlaylistLoader;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements
        RightFilterFragmentSearchActivity.FilterListener,
        PopupAddWithNewPlaylistFragment.IPopupAddWithNewPlaylistAddSendListener,
        PopupAddToPlayListFragment.IPopupAddToPlaylistSendListener{

    private String languageCode;
    private SearchView searchView;
    private SearchAdapterActivitySearch searchAdapter;
    private DrawerLayout drawerLayout;
    private RightFilterFragmentSearchActivity rightFilterFragment;
    PopupAddToPlayListFragment popupAddToPlayListFragment;
    private SearchViewModel searchViewModel;
    PlaylistModel playlistModel;
    private boolean isFirstTimeCreateActivity  = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Kiểm tra xem Activity được tạo lần đầu tiên hay không
        if (savedInstanceState != null) {
            // Activity được tạo lại từ trạng thái trước đó
            isFirstTimeCreateActivity = false;
        } else {
            // Activity được tạo lần đầu tiên
            isFirstTimeCreateActivity = true;
        }

        //Init
        drawerLayout = findViewById(R.id.drawer_layout_activity_search);
        rightFilterFragment = new RightFilterFragmentSearchActivity();
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        searchViewModel = viewModelProvider.get(SearchViewModel.class);
        playlistModel = viewModelProvider.get(PlaylistModel.class);
        fetchPlaylists();


        // Lấy ngôn ngữ
        Locale currentLocale = getResources().getConfiguration().getLocales().get(0);
        languageCode = currentLocale.getLanguage();


        //Popup add to playlist
        SearchAdapterActivitySearch.IPopupPlaylist iPopupPlaylist = (movie) -> {
            popupAddToPlayListFragment = new PopupAddToPlayListFragment(movie);
            popupAddToPlayListFragment.show(getSupportFragmentManager(), "popup_add_to_playlist_fragment");
        };


        //RecyclerView
        RecyclerView recyclerViewSearch = findViewById(R.id.recycle_view_search_atv_search);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        searchAdapter = new SearchAdapterActivitySearch(SearchActivity.this, iPopupPlaylist);
        recyclerViewSearch.setAdapter(searchAdapter);
        searchAdapter.addAllData(searchViewModel.getMovies());
        //Lazy load recycleView
        recyclerViewSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                if (lastVisibleItemPosition == totalItemCount - 1) {
                    loadNextPage();
                }
            }
        });


        //SearchView
        searchView = findViewById(R.id.search_view_atv_search);
        searchView.setSubmitButtonEnabled(true);
        //Submit searchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                rightFilterFragment.setDefaultFilter();
                searchViewModel.getFilterSearch().setDefault();
                findViewById(R.id.btn_submit_filter_search).setEnabled(true);
                searchMovieAndTv(query, searchViewModel.getFilterSearch());
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        //Fragment filter
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.right_drawer_search_activity, rightFilterFragment).commit();
        //Btn open filter
        ImageView btnFilter = findViewById(R.id.btn_filter_activity_search);
        // Lấy chủ đề hiện tại của ứng dụng
        int currentTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        // Đặt hình ảnh tùy thuộc vào chủ đề
        if (currentTheme == Configuration.UI_MODE_NIGHT_YES) {
            btnFilter.setImageResource(R.drawable.ic_filter_dark);
        } else {
            btnFilter.setImageResource(R.drawable.ic_filter_light);
        }

        btnFilter.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        ImageView btnBack = findViewById(R.id.imgBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get query in intent
        Intent intent = getIntent();
        if (intent != null && isFirstTimeCreateActivity) {
            String query = intent.getStringExtra("query");
            if(query != null){
                Log.i("SEARCH_ACTIVITY", "LOAD QUERY FROM INTENT");
                searchView.setQuery(query, true);
                searchView.setIconifiedByDefault(false);
                isFirstTimeCreateActivity = false;
            }
        }
    }

    // Function search movie
    public void searchMovieAndTv(String query, FilterSearch filter) {
        searchViewModel.clearMovies();
        searchViewModel.setPage(1);
        searchAdapter.clear();
        searchDataTV(query, languageCode, 1, filter);
        searchDataMovie(query, languageCode, 1, filter);
    }


    // function load next page for recyclerView
    private void loadNextPage() {
        int page = searchViewModel.plusOnePage();
        String query = searchView.getQuery().toString();
        if(!searchViewModel.isSearchTVNoData()){
            searchDataTV(query, languageCode, page, searchViewModel.getFilterSearch());
        }
        if(!searchViewModel.isSearchMovieNoData()){
            searchDataMovie(query,languageCode, page, searchViewModel.getFilterSearch());
        }
    }


    // function call api movie data
    private void searchDataMovie(String query, String language, int page, FilterSearch filter) {
        TMDBService tMDBService = ServiceApiBuilder.buildTMDBService(TMDBService.class);
        Call<TMDBSearchMovieResponse> call = tMDBService
                .searchMovie(query, filter.isAdult(), language, page, filter.getYear(), ServiceApiBuilder.API_KEY_TMDB);
        call.enqueue(new Callback<TMDBSearchMovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<TMDBSearchMovieResponse> call, @NonNull Response<TMDBSearchMovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TMDBSearchMovieResponse searchMovieResponse = response.body();
                    searchViewModel.setSearchMovieNoData(searchMovieResponse.getTotalPages() <= searchMovieResponse.getPage());
                    for(Movie movie : TMDBMovieResult.toListMovie(searchMovieResponse.getResults())){
                        addMovieToRecycleView(movie);
                    }
                } else {
                    Log.e("API_ERROR", "Failed to fetch movies");
                }
            }
            @Override
            public void onFailure(@NonNull Call<TMDBSearchMovieResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Network error fetch movies", t);
            }
        });
    }


    // function call api tv series data
    private void searchDataTV(String query, String language, int page, FilterSearch filter) {
        TMDBService tMDBService = ServiceApiBuilder.buildTMDBService(TMDBService.class);
        int yearInt = Objects.equals(filter.getYear(), "") ? 0 : Integer.parseInt(filter.getYear());
        Call<TMDBSearchTVResponse> call = tMDBService
                .searchTV(query, filter.isAdult(), language, page, yearInt, ServiceApiBuilder.API_KEY_TMDB);
        call.enqueue(new Callback<TMDBSearchTVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TMDBSearchTVResponse> call, @NonNull Response<TMDBSearchTVResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TMDBSearchTVResponse searchTVResponse = response.body();
                    searchViewModel.setSearchTVNoData(searchTVResponse.getTotalPages() <= searchTVResponse.getPage());
                    for(Movie movie : TMDBTVResult.toListMovie(searchTVResponse.getResults())){
                        addMovieToRecycleView(movie);
                    }
                } else {
                    Log.e("API_ERROR", "Failed to fetch tv series");
                }
            }
            @Override
            public void onFailure(@NonNull Call<TMDBSearchTVResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Network error fetch tv series", t);
            }
        });
    }



    private void addMovieToRecycleView(Movie movie) {
        for (Integer element : movie.getGenreIds()) {
            if (searchViewModel.getFilterSearch().getGenreIds().isEmpty() ||
                    searchViewModel.getFilterSearch().getGenreIds().contains(element)) {
                searchAdapter.addData(movie);
                searchViewModel.addMovie(movie);
                break;
            }
        }
    }


    // function when filter button click
    @Override
    public void onFilter(FilterSearch filter) {
        String query =  searchView.getQuery().toString();
        drawerLayout.closeDrawer(GravityCompat.END);
        searchViewModel.setFilterSearch(filter);
        searchMovieAndTv(query, filter);
    }

    @Override
    public void setDefaultFilter() {
        searchViewModel.getFilterSearch().setDefault();
    }


    // Function get language code
    public String getLanguageCode(){
        return languageCode;
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


    //
    // LOADER ADD WITH NEW PLAYLIST
    //
    @Override
    public void btnAddWithNewPlaylistClick(PlaylistWithOneItemDTO playlist) {
        String playlistJson = new Gson().toJson(playlist);
        Bundle args = new Bundle();
        args.putString("playlistJson", playlistJson);
        LoaderManager.getInstance(SearchActivity.this).restartLoader(2001, args, loaderAddWithNewPlaylist);
    }

    LoaderManager.LoaderCallbacks<Playlist> loaderAddWithNewPlaylist = new LoaderManager.LoaderCallbacks<Playlist>() {
        @NonNull
        @Override
        public Loader<Playlist> onCreateLoader(int id, @Nullable Bundle args) {
            assert args != null;
            String playlistJson = args.getString("playlistJson");
            Gson gson = new Gson();
            PlaylistWithOneItemDTO playlist = gson.fromJson(playlistJson, PlaylistWithOneItemDTO.class);
            return new AddWithNewPlaylistLoader(SearchActivity.this, playlist);
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
            LoaderManager.getInstance(SearchActivity.this).destroyLoader(loader.getId());
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
        LoaderManager.getInstance(SearchActivity.this).restartLoader(2002, args, loaderAddOrRemoveExistPlaylist);
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
            return new AddMovieToPlaylistLoader(SearchActivity.this, listAdd, listRemove, informationMovie);
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
            LoaderManager.getInstance(SearchActivity.this).destroyLoader(loader.getId());
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

    private String replaceIdWithName(String message) {
        for (Playlist playlist : playlistModel.getListPlaylist()) {
            String idString = String.valueOf(playlist.getId());
            String searchPattern = "\\b" + idString + "\\b";
            message = message.replaceAll(searchPattern, playlist.getTitle());
        }
        return message;
    }
}