package com.example.appxemphim.model;

import com.example.appxemphim.util.ConvertISO8601Time;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class YoutubeVideoContentDetails implements Serializable {
    @SerializedName("duration")
    private String duration;

    public String getDuration() {
        return duration;
    }
    public int getSecondDuration(){
        return  ConvertISO8601Time.toIntSecond(this.duration);
    }
    public String getDurationMinuteFormat(){
        return ConvertISO8601Time.toStringMinute(this.duration);
    }
}

