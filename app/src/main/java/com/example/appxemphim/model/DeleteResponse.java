package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

public class DeleteResponse {//Lớp DeleteResponse định nghĩa cấu trúc của phản hồi từ hệ thống sau khi gửi một yêu cầu xóa
    @SerializedName("success")//thuoc tinh success
    private boolean success;//kieu boolean

    public boolean isSuccess() {
        return success;
    } //kiểm tra giá trị của thuộc tính success để biết xem yêu cầu xóa đã thành công hay không.
}
