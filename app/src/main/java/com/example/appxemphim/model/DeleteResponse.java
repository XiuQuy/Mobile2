package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

public class DeleteResponse {
    @SerializedName("success")
    private boolean success;

    public boolean isSuccess() {
        return success;
    }
}
