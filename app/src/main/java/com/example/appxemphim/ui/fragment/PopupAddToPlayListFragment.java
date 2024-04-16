package com.example.appxemphim.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.PlaylistService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.model.InformationMovie;
import com.example.appxemphim.model.Movie;
import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.ui.viewmodel.PlaylistModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopupAddToPlayListFragment extends DialogFragment {
    View rootView;
    PopupAddWithNewPlaylistFragment popupAddWithNewPlaylistFragment;
    PlaylistModel playlistModel;
    Movie movie;
    LinearLayout containerPlaylist;

    public PopupAddToPlayListFragment(Movie movie) {
        this.movie = movie;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_popup_add_to_playlist, container, false);
        initContent();
        return rootView;
    }

    private void initContent() {
        rootView.setFocusable(true);
        playlistModel = new ViewModelProvider(this).get(PlaylistModel.class);
        containerPlaylist = rootView.findViewById(R.id.container_checkbox_playlist);

        //Check movie in all playlist
        InformationMovie informationMovie =  new InformationMovie();
        informationMovie.setTitle(movie.getName());
        informationMovie.setTag(movie.getTag());
        informationMovie.setMovieId(String.valueOf(movie.getId()));
        checkMovieInPlaylist(informationMovie);

        //Set button finish click listener
        Button btnFinish = rootView.findViewById(R.id.btn_finish_popup_add_to_playlist);
        btnFinish.setOnClickListener(v -> {
            dismiss();
        });

        //Set button add with new playlist click listener
        TextView btnAddToNewPlaylist = rootView.findViewById(R.id.btn_add_to_new_playlist);
        btnAddToNewPlaylist.setOnClickListener(v -> {
            dismiss();
            popupAddWithNewPlaylistFragment = new PopupAddWithNewPlaylistFragment();
            popupAddWithNewPlaylistFragment.show(
                    requireActivity().getSupportFragmentManager(), "popup_add_with_new_playlist_fragment");
        });
    }
    private void checkMovieInPlaylist(InformationMovie informationMovie){
        PlaylistService playlistService = ServiceApiBuilder.buildUserApiService(PlaylistService.class);
        Call<List<Integer>> call = playlistService.checkMovieInAllPlaylist(1, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJKV1RTZXJ2aWNlQWNjZXNzVG9rZW4iLCJqdGkiOiIyNDVmN2M4My0wODMxLTRiNzUtYTBiYi0wMGU1ZmQ0OTVlNTMiLCJpYXQiOiIxNi8wNC8yMDI0IDk6Mzk6MzAgU0EiLCJVc2VySWQiOiIxIiwiRW1haWwiOiJkb2xlaHV5MjIyQGdtYWlsLmNvbSIsImV4cCI6MTcxNDEyNDM3MCwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0OTg3MCIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6NDk4NzAifQ.3c0qceENeVBMoDiqNgMuvLSFurfVS2PhPmjeZ0_m8pQ", informationMovie);
        call.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                containerPlaylist.removeAllViews();
                List<Playlist> playlists = playlistModel.getListPlaylist();
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
                for (Playlist playlist : playlists) {
                    CheckBox checkBox = new CheckBox(requireContext());
                    checkBox.setText(playlist.getTitle());
                    if(response.body() != null && response.body().contains(playlist.getId())){
                        checkBox.setChecked(true);
                    }
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            //Them vao playlist
                        } else {
                            //Xoa khoi playlist
                        }
                    });
                    containerPlaylist.addView(checkBox, layoutParams);
                }
            }
            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {

            }
        });
    }
}