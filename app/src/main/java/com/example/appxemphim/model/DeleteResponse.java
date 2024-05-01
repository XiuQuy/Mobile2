package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

public class DeleteResponse {
    @SerializedName("success")
    private boolean success;

    public boolean isSuccess() {
        return success;
    }//xu ly yeu cau phan hoi co thanh cong khong
}
