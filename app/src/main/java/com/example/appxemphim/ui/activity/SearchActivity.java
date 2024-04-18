package com.example.appxemphim.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.TMDBService;
import com.example.appxemphim.model.FilterSearch;
import com.example.appxemphim.model.Movie;
import com.example.appxemphim.model.TMDBSearchMovieResponse;
import com.example.appxemphim.model.TMDBMovieResult;
import com.example.appxemphim.model.TMDBSearchTVResponse;
import com.example.appxemphim.model.TMDBTVResult;
import com.example.appxemphim.ui.adapter.SearchAdapterActivitySearch;
import com.example.appxemphim.ui.fragment.PopupAddToPlayListFragment;
import com.example.appxemphim.ui.fragment.RightFilterFragmentSearchActivity;
import com.example.appxemphim.ui.viewmodel.SearchViewModel;

import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements RightFilterFragmentSearchActivity.FilterListener{
    private String languageCode;
    private SearchView searchView;
    private SearchAdapterActivitySearch searchAdapter;
    private DrawerLayout drawerLayout;
    private RightFilterFragmentSearchActivity rightFilterFragment;
    private SearchAdapterActivitySearch.PopupPlaylist popupPlayList;
    PopupAddToPlayListFragment popupAddToPlayListFragment;
    private SearchViewModel searchViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        //Init
        drawerLayout = findViewById(R.id.drawer_layout_activity_search);
        rightFilterFragment = new RightFilterFragmentSearchActivity();
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);


        // Lấy ngôn ngữ
        getResources().getConfiguration().setLocale(new Locale("en"));
        Locale currentLocale = getResources().getConfiguration().getLocales().get(0);
        languageCode = currentLocale.getLanguage();


        //Popup add to playlist
        popupPlayList = (movie) -> {
            popupAddToPlayListFragment = new PopupAddToPlayListFragment(movie);
            popupAddToPlayListFragment.show(getSupportFragmentManager(), "popup_add_to_playlist_fragment");
        };


        //RecyclerView
        RecyclerView recyclerViewSearch = findViewById(R.id.recycle_view_search_atv_search);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        searchAdapter = new SearchAdapterActivitySearch(SearchActivity.this, popupPlayList);
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
        //Btn open
        ImageView btnFilter = findViewById(R.id.btn_filter_activity_search);
        btnFilter.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.END);
        });
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
            public void onResponse(Call<TMDBSearchMovieResponse> call, Response<TMDBSearchMovieResponse> response) {
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
            public void onFailure(Call<TMDBSearchMovieResponse> call, Throwable t) {
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
            public void onResponse(Call<TMDBSearchTVResponse> call, Response<TMDBSearchTVResponse> response) {
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
            public void onFailure(Call<TMDBSearchTVResponse> call, Throwable t) {
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
}