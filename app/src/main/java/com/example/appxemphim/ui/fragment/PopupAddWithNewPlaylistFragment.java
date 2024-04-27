package com.example.appxemphim.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.appxemphim.R;
import com.example.appxemphim.model.InformationMovie;
import com.example.appxemphim.model.Movie;
import com.example.appxemphim.model.PlaylistItem;
import com.example.appxemphim.model.PlaylistWithOneItemDTO;

public class PopupAddWithNewPlaylistFragment extends DialogFragment {
    View rootView;
    Movie movie;
    TextView btnAdd;
    EditText txtNewPlaylistName;
    IPopupAddWithNewPlaylistAddSendListener iPopupAddWithNewPlaylistAddSendListener;

    public PopupAddWithNewPlaylistFragment(Movie movie) {
        this.movie = movie;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iPopupAddWithNewPlaylistAddSendListener = (IPopupAddWithNewPlaylistAddSendListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_popup_add_with_new_playlist, container, false);
        initContent();
        return rootView;
    }

    private void initContent() {
        //init
        rootView.setFocusable(true);
        TextView btnCancel = rootView.findViewById(R.id.btn_cancel);
        txtNewPlaylistName = rootView.findViewById(R.id.txt_name_playlist);
        btnAdd = rootView.findViewById(R.id.btn_add);


        //listener
        btnCancel.setOnClickListener(v -> {
            dismiss();
        });
        btnAdd.setOnClickListener(v -> {
            InformationMovie informationMovie = new InformationMovie();
            informationMovie.setMovieId(String.valueOf(movie.getId()));
            informationMovie.setTitle(movie.getName());
            informationMovie.setTag(movie.getTag());
            informationMovie.setImageLink(movie.getPosterPath());

            PlaylistItem playlistItem = new PlaylistItem();
            playlistItem.setInformationMovie(informationMovie);

            PlaylistWithOneItemDTO playlist = new PlaylistWithOneItemDTO();
            playlist.setTitle(txtNewPlaylistName.getText().toString().trim());
            playlist.setUserId(1);
            playlist.setItem(playlistItem);
            iPopupAddWithNewPlaylistAddSendListener.btnAddWithNewPlaylistClick(playlist);
            dismiss();
        });
        txtNewPlaylistName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().isEmpty()) {
                    btnAdd.setEnabled(true);
                    btnAdd.setTextColor(getResources().getColor(R.color.btn_link_color, requireActivity().getTheme()));
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    btnAdd.setEnabled(false);
                    btnAdd.setTextColor(getResources().getColor(R.color.btn_link_disable_color, requireActivity().getTheme()));
                }
            }
        });
    }
    public interface IPopupAddWithNewPlaylistAddSendListener {
        void btnAddWithNewPlaylistClick(PlaylistWithOneItemDTO playlist);
    }
}
