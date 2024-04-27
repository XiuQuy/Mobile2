package com.example.appxemphim.ui.adapter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.appxemphim.R;

public class GlideLoadImgListener implements RequestListener<Drawable> {
    private ImageView imageView;

    public GlideLoadImgListener(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                Target<Drawable> target, boolean isFirstResource) {
        imageView.setImageResource(R.drawable.no_img_available);
        return true;
    }
    @Override
    public boolean onResourceReady(@NonNull Drawable resource,
                                   @NonNull Object model, Target<Drawable> target,
                                   @NonNull DataSource dataSource,
                                   boolean isFirstResource) {
        return false;
    }
}