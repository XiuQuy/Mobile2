package com.example.appxemphim.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private boolean isSearchTVCompleted = false;
    private boolean isSearchMovieCompleted = false;
    List<Movie> listMovie;
    RecyclerView recyclerViewSearch;
    String languageCode;
    SearchView searchView;
    SearchAdapterActivitySearch searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //List movie recyclerVỉew
        listMovie = new ArrayList<>();

        // Lấy ngôn ngữ
        Locale currentLocale = getResources().getConfiguration().getLocales().get(0);
        languageCode = currentLocale.getLanguage();

        //RecyclerView
        recyclerViewSearch = findViewById(R.id.recycle_view_search_atv_search);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        searchAdapter = new SearchAdapterActivitySearch(SearchActivity.this, listMovie);

        recyclerViewSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    loadNextPage();
                }
            }
        });

        //SearchView
        searchView = findViewById(R.id.search_view_atv_search);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listMovie.clear();
                searchAdapter.notifyDataSetChanged();
                searchView.clearFocus();
                searchDataTV(query,false, languageCode,1,"");
                searchDataMovie(query,false, languageCode,1,"");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void loadNextPage() {
    }


    private void searchDataMovie(String query, boolean includeAdult, String language, int page, String year) {
        TMDBService tMDBService = ServiceApiBuilder.buildTMDBService(TMDBService.class);
        Call<TMDBSearchMovieResponse> call = tMDBService
                .searchMovie(query, includeAdult, language, page, year, ServiceApiBuilder.API_KEY_TMDB);

        call.enqueue(new Callback<TMDBSearchMovieResponse>() {
            @Override
            public void onResponse(Call<TMDBSearchMovieResponse> call, Response<TMDBSearchMovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TMDBSearchMovieResponse SearchMovieResponse = response.body();
                    importMovieResultToListMovie(SearchMovieResponse.getResults());
                    isSearchMovieCompleted = true;
                    updateDataRecycleView();
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
    private void importMovieResultToListMovie(List<TMDBSearchMovieResult> list){
        listMovie.addAll(TMDBSearchMovieResult.toListMovie(list));
    }

    private void searchDataTV(String query, boolean includeAdult, String language, int page, String year) {
        TMDBService tMDBService = ServiceApiBuilder.buildTMDBService(TMDBService.class);
        Call<TMDBSearchTVResponse> call = tMDBService
                .searchTV(query, includeAdult, language, page, year, ServiceApiBuilder.API_KEY_TMDB);

        call.enqueue(new Callback<TMDBSearchTVResponse>() {
            @Override
            public void onResponse(Call<TMDBSearchTVResponse> call, Response<TMDBSearchTVResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TMDBSearchTVResponse searchTVResponse = response.body();
                    importTVResultToListMovie(searchTVResponse.getResults());
                    isSearchTVCompleted = true;
                    updateDataRecycleView();
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
    private void importTVResultToListMovie(List<TMDBSearchTVResult> list){
        listMovie.addAll(TMDBSearchTVResult.toListMovie(list));
    }

    private void updateDataRecycleView(){
        if (isSearchMovieCompleted && isSearchTVCompleted) {
            Log.e("SIZE", ""+listMovie.size());
            recyclerViewSearch.setAdapter(searchAdapter);
            isSearchTVCompleted = false;
            isSearchMovieCompleted = false;
        }
    }
}