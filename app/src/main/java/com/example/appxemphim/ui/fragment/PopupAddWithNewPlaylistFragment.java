package com.example.appxemphim.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.appxemphim.R;
import com.example.appxemphim.model.Movie;

public class PopupAddWithNewPlaylistFragment extends DialogFragment {
    View rootView;
    Movie movie;

    public PopupAddWithNewPlaylistFragment(Movie movie) {
        this.movie = movie;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_popup_add_with_new_playlist, container, false);
        initContent();
        return rootView;
    }

    private void initContent() {
        rootView.setFocusable(true);
        TextView btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> {
            dismiss();
        });
        TextView btnAdd = rootView.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(v -> {

        });
    }
}
