package com.example.appxemphim.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.appxemphim.R;

public class PopupAddToPlayListFragment extends DialogFragment {
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_popup_add_to_playlist, container, false);
        initContent();
        return rootView;
    }

    private void initContent() {
        rootView.setFocusable(true);
        Button btnFinish = rootView.findViewById(R.id.btn_finish_popup_add_to_playlist);
        btnFinish.setOnClickListener(v -> {
            dismiss();
        });
    }
}