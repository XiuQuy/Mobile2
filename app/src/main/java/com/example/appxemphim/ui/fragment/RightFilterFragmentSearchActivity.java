package com.example.appxemphim.ui.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.TMDBService;
import com.example.appxemphim.model.FilterSearch;
import com.example.appxemphim.model.Genre;
import com.example.appxemphim.model.TMDBGenreListResponse;
import com.example.appxemphim.ui.activity.SearchActivity;
import com.example.appxemphim.ui.viewmodel.FragmentFilterSearchViewModel;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RightFilterFragmentSearchActivity extends Fragment {
    private boolean isGetGenreTVComplete= false;
    private boolean isGetGenreMovieComplete = false;
    Set<Genre> genreSet;
    private View fView;
    private CheckBox cbAdult;
    private EditText txtYear;
    private ChipGroup genreGroup;
    private Button btnFilter;
    private FilterListener filterListener;
    private FragmentFilterSearchViewModel viewModel;

    public RightFilterFragmentSearchActivity() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        filterListener = (FilterListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fView = inflater.inflate(R.layout.layout_fragment_filter_search, container, false);
        initUi();
        return fView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.setAdult(cbAdult.isChecked());
        viewModel.setYear(txtYear.getText().toString());
        viewModel.setBtnFilterEnable(btnFilter.isEnabled());
        viewModel.setChipList(getGenreChipList());
    }

    private void initUi() {
        // init
        viewModel = new ViewModelProvider(requireActivity()).get(FragmentFilterSearchViewModel.class);
        cbAdult = fView.findViewById(R.id.checkbox_adult_filter_search);
        txtYear = fView.findViewById(R.id.txt_year_filter_search);
        Button btnSetDefault = fView.findViewById(R.id.btn_set_default_filter_search);
        genreGroup = fView.findViewById(R.id.chipGroup_genre_filter_search);
        btnFilter = fView.findViewById(R.id.btn_submit_filter_search);
        genreSet =  new HashSet<>();

        //Get data from view model
        txtYear.setText(viewModel.getYear());
        btnFilter.setEnabled(viewModel.isBtnFilterEnable());
        cbAdult.setChecked(viewModel.isAdult());

        //Get genre list
        if(viewModel.getChipList() == null){
            getGenreList(((SearchActivity) requireActivity()).getLanguageCode());
        }else{
            for(Chip chip : viewModel.getChipList()){
                ((ViewGroup) chip.getParent()).removeView(chip);
                genreGroup.addView(chip);
            }
        }


        //Nav filter
        btnSetDefault.setOnClickListener(v -> {
            setDefaultFilter();
        });
        btnFilter.setOnClickListener(v -> {
            FilterSearch filter = new FilterSearch(cbAdult.isChecked(),
                    txtYear.getText().toString(), getGenreChecked());
            filterListener.onFilter(filter);
        });
        //Button close filter nav
        ImageView btnCloseFilter = fView.findViewById(R.id.btn_close_filter_search);
        DrawerLayout drawerLayout = requireActivity().findViewById(R.id.drawer_layout_activity_search);
        btnCloseFilter.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.END);
        });



    }


    // function call api get genre list
    private void getGenreList(String language) {
        TMDBService tMDBService = ServiceApiBuilder.buildTMDBService(TMDBService.class);
        Call<TMDBGenreListResponse> callTV = tMDBService.listGenreTV(language, ServiceApiBuilder.API_KEY_TMDB);
        Call<TMDBGenreListResponse> callMovie = tMDBService.listGenreMovie(language, ServiceApiBuilder.API_KEY_TMDB);
        callTV.enqueue(new Callback<TMDBGenreListResponse>() {
            @Override
            public void onResponse(Call<TMDBGenreListResponse> call, Response<TMDBGenreListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TMDBGenreListResponse genreTVResponse = response.body();
                    genreSet.addAll(genreTVResponse.getGenres());
                    isGetGenreTVComplete = true;
                    setGenreChipList();
                } else {
                    Log.e("API_ERROR", "Failed to fetch genres");
                }
            }
            @Override
            public void onFailure(Call<TMDBGenreListResponse> call, Throwable t) {
                Log.e("API_ERROR", "Network error call genre tv", t);
            }
        });
        callMovie.enqueue(new Callback<TMDBGenreListResponse>() {
            @Override
            public void onResponse(Call<TMDBGenreListResponse> call, Response<TMDBGenreListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TMDBGenreListResponse genreMovieResponse = response.body();
                    genreSet.addAll(genreMovieResponse.getGenres());
                    isGetGenreMovieComplete = true;
                    setGenreChipList();
                } else {
                    Log.e("API_ERROR", "Failed to fetch genres");
                }
            }
            @Override
            public void onFailure(Call<TMDBGenreListResponse> call, Throwable t) {
                Log.e("API_ERROR", "Network error call genre movie", t);
            }
        });
    }


    //function set default filter
    public void setDefaultFilter(){
        filterListener.setDefaultFilter();
        txtYear.setText("");
        cbAdult.setChecked(false);
        genreGroup.clearCheck();
    }


    //set genre chip list ui
    private void setGenreChipList(){
        if(isGetGenreMovieComplete && isGetGenreTVComplete && getContext() != null){
            ColorStateList color = getResources()
                    .getColorStateList(R.color.text_color, requireContext().getTheme());
            for (Genre genre: genreSet) {
                Chip chip = new Chip(getContext(), null, R.style.Widget_Appxemphim_Chip_Filter);
                chip.setText(genre.getName());
                chip.setTag(genre.getId());
                chip.setCheckable(true);
                chip.setClickable(true);
                chip.setCheckedIconTint(color);
                chip.setTextColor(color);
                genreGroup.addView(chip);
            }
        }
    }


    //function get genre checked
    private Set<Integer> getGenreChecked(){
        Set<Integer> selectedGenreIds = new HashSet<>();
        for (int i = 0; i < genreGroup.getChildCount(); i++) {
            View childView = genreGroup.getChildAt(i);
            if (childView instanceof Chip) {
                Chip chip = (Chip) childView;
                if (chip.isChecked()) {
                    selectedGenreIds.add(Integer.parseInt(chip.getTag().toString()));
                }
            }
        }
        return selectedGenreIds;
    }

    private List<Chip> getGenreChipList(){
        List<Chip> list = new ArrayList<>();
        for (int i = 0; i < genreGroup.getChildCount(); i++) {
            View childView = genreGroup.getChildAt(i);
            if (childView instanceof Chip) {
                list.add((Chip) childView);
            }
        }
        return list;
    }

    //interface handle filter to activity
    public interface FilterListener {
        void onFilter(FilterSearch filter);
        void setDefaultFilter();
    }
}
