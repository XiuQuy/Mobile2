package com.example.appxemphim.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.appxemphim.R;
import com.example.appxemphim.ui.activity.MovieDetailActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class FirebaseNotification extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.i("RUN_HANDLE_NOTIFICATION", "RUN HANDLE NOTIFICATION");
        if(!message.getData().isEmpty()){
            handleFirebaseMessage(
                    Objects.requireNonNull(message.getNotification()).getTitle(),
                    message.getNotification().getBody(),
                    message.getData(),
                    Objects.requireNonNull(message.getNotification().getImageUrl()).toString());
        }
    }

    private void handleFirebaseMessage(String title, String body, @NonNull Map<String, String> data, String imageUrl) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movieId", data.get("movieId"));
        intent.putExtra("tag", data.get("tag"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent =  PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );
        // Tạo một hình ảnh Bitmap từ URL
        Bitmap bitmap = getBitmapFromUrl(imageUrl);

        String channelId = "new_content";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.user1)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null));;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Kiểm tra phiên bản Android để đảm bảo sử dụng phương thức notificationManager đúng cách
        NotificationChannel channel = new NotificationChannel(
                channelId,
                "The new movie",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            return Picasso.get().load(imageUrl).get();
        } catch (IOException e) {
            return null;
        }
    }
}
