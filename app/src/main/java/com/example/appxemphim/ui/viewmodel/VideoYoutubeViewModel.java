package com.example.appxemphim.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.appxemphim.model.YoutubeVideoSnippet;

import java.util.List;

public class VideoYoutubeViewModel extends ViewModel {
    List<YoutubeVideoSnippet> listVideoTrailer;
    List<YoutubeVideoSnippet> listVideoReview;

    public List<YoutubeVideoSnippet> getListVideoTrailer() {
        return listVideoTrailer;
    }

    public void setListVideoTrailer(List<YoutubeVideoSnippet> listVideoTrailer) {
        this.listVideoTrailer = listVideoTrailer;
    }

    public List<YoutubeVideoSnippet> getListVideoReview() {
        return listVideoReview;
    }

    public void setListVideoReview(List<YoutubeVideoSnippet> listVideoReview) {
        this.listVideoReview = listVideoReview;
    }
}
