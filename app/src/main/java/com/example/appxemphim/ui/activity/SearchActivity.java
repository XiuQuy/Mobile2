package com.example.appxemphim.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.TMDBService;
import com.example.appxemphim.model.Movie;
import com.example.appxemphim.model.TMDBSearchMovieResponse;
import com.example.appxemphim.model.TMDBSearchMovieResult;
import com.example.appxemphim.model.TMDBSearchTVResponse;
import com.example.appxemphim.model.TMDBSearchTVResult;
import com.example.appxemphim.ui.adapter.SearchAdapterActivitySearch;
import com.example.appxemphim.ui.fragment.RightFilterFragmentSearchActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private boolean isSearchTVNoData = false;
    private boolean isSearchMovieNoData = false;
    List<Movie> listMovie;
    RecyclerView recyclerViewSearch;
    String languageCode;
    SearchView searchView;
    SearchAdapterActivitySearch searchAdapter;
    ImageView btnFilter;
    ImageView btnCloseFilter;
    private int page;
    private DrawerLayout drawerLayout;
    private RightFilterFragmentSearchActivity rightFilterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //List movie recyclerVỉew
        listMovie = new ArrayList<>();

        // Lấy ngôn ngữ
        getResources().getConfiguration().setLocale(new Locale("en"));
        Locale currentLocale = getResources().getConfiguration().getLocales().get(0);
        languageCode = currentLocale.getLanguage();

        //RecyclerView
        recyclerViewSearch = findViewById(R.id.recycle_view_search_atv_search);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        searchAdapter = new SearchAdapterActivitySearch(SearchActivity.this);
        recyclerViewSearch.setAdapter(searchAdapter);
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
                page = 1;
                searchAdapter.clear();
                searchDataTV(query,false, languageCode, page,"");
                searchDataMovie(query,false, languageCode, page,"");
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        //Fragment filter
        drawerLayout = findViewById(R.id.drawer_layout_activity_search);
        rightFilterFragment = new RightFilterFragmentSearchActivity();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.right_drawer_search_activity, rightFilterFragment).commit();
        //Btn open and close
        btnFilter = findViewById(R.id.btn_filter_activity_search);
        btnFilter.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.END);
        });
        btnCloseFilter = findViewById(R.id.btn_close_filter_search);
        btnCloseFilter.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.END);
        });
    }
















    // function load next page for recyclerView
    private void loadNextPage() {
        page += 1;
        String query =  searchView.getQuery().toString();
        if(!isSearchTVNoData){
            searchDataTV(query,false, languageCode, page,"");
        }
        if(!isSearchMovieNoData){
            searchDataMovie(query,false, languageCode, page,"");
        }
    }

    // function call api movie data
    private void searchDataMovie(String query, boolean includeAdult, String language, int page, String year) {
        TMDBService tMDBService = ServiceApiBuilder.buildTMDBService(TMDBService.class);
        Call<TMDBSearchMovieResponse> call = tMDBService
                .searchMovie(query, includeAdult, language, page, year, ServiceApiBuilder.API_KEY_TMDB);
        call.enqueue(new Callback<TMDBSearchMovieResponse>() {
            @Override
            public void onResponse(Call<TMDBSearchMovieResponse> call, Response<TMDBSearchMovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TMDBSearchMovieResponse searchMovieResponse = response.body();
                    isSearchMovieNoData = searchMovieResponse.getTotalPages() <= searchMovieResponse.getPage();
                    searchAdapter.addData(TMDBSearchMovieResult.toListMovie(searchMovieResponse.getResults()));
                } else {
                    Log.e("API_ERROR", "Failed to fetch movies");
                }
            }
            @Override
            public void onFailure(Call<TMDBSearchMovieResponse> call, Throwable t) {
                Log.e("API_ERROR", "Network error", t);
            }
        });
    }


    // function call api tv series data
    private void searchDataTV(String query, boolean includeAdult, String language, int page, String year) {
        TMDBService tMDBService = ServiceApiBuilder.buildTMDBService(TMDBService.class);
        Call<TMDBSearchTVResponse> call = tMDBService
                .searchTV(query, includeAdult, language, page, year, ServiceApiBuilder.API_KEY_TMDB);
        call.enqueue(new Callback<TMDBSearchTVResponse>() {
            @Override
            public void onResponse(Call<TMDBSearchTVResponse> call, Response<TMDBSearchTVResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TMDBSearchTVResponse searchTVResponse = response.body();
                    isSearchTVNoData = searchTVResponse.getTotalPages() <= searchTVResponse.getPage();
                    searchAdapter.addData(TMDBSearchTVResult.toListMovie(searchTVResponse.getResults()));
                } else {
                    Log.e("API_ERROR", "Failed to fetch movies");
                }
            }
            @Override
            public void onFailure(Call<TMDBSearchTVResponse> call, Throwable t) {
                Log.e("API_ERROR", "Network error", t);
            }
        });
    }
}