    package com.example.appxemphim.model;

    import androidx.annotation.Nullable;

    import com.google.gson.annotations.SerializedName;

    import java.io.Serializable;

    public class YoutubeVideoSnippet implements Serializable {
        @SerializedName("title")
        private String title;

        @SerializedName("description")
        private String description;

        @SerializedName("channelId")
        private String channelId;

        @SerializedName("channelTitle")
        private String channelTitle;

        @SerializedName("thumbnails")
        private ThumbnailsYoutube thumbnails;

        @SerializedName("publishedAt")
        private String publishedAt;

        public String getPublishedAt() {
            return publishedAt;
        }

        public String getTitle() {
            return title;
        }

        public String getChannelTitle() {
            return channelTitle;
        }

        public String getDescription() {
            return description;
        }

        public String getChannelId() {
            return channelId;
        }

        public ThumbnailsYoutube getThumbnails() {
            return thumbnails;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return super.equals(obj);
        }
    }
